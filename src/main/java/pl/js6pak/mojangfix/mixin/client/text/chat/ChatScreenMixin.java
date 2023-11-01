/*
 * Copyright (C) 2022-2023 js6pak
 *
 * This file is part of MojangFix.
 *
 * MojangFix is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, version 3.
 *
 * MojangFix is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with MojangFix. If not, see <https://www.gnu.org/licenses/>.
 */

package pl.js6pak.mojangfix.mixin.client.text.chat;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pl.js6pak.mojangfix.mixinterface.ChatScreenAccessor;
import pl.js6pak.mojangfix.mixinterface.TextFieldWidgetAccessor;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements ChatScreenAccessor {
    @Unique
    private String initialMessage = "";

    public ChatScreen setInitialMessage(String initialMessage) {
        this.initialMessage = initialMessage;
        return (ChatScreen) (Object) this;
    }

    @Unique
    private TextFieldWidget textField;

    @Unique
    private int chatHistoryPosition;

    @Unique
    private static final List<String> CHAT_HISTORY = new ArrayList<>();

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.textField = new TextFieldWidget(this, this.textRenderer, 2, this.height - 14, this.width - 2, this.height - 2, this.initialMessage);
        this.textField.setFocused(true);
        this.textField.setMaxLength(100);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        this.textField.tick();
        ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;drawStringWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void redirectDrawString(ChatScreen chatScreen, TextRenderer textRenderer, String text, int x, int y, int color) {
        this.drawStringWithShadow(textRenderer, "> " + ((TextFieldWidgetAccessor) this.textField).getDisplayText(), x, y, color);
    }

    @Redirect(method = "*", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;text:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String getMessage(ChatScreen chatScreen) {
        return this.textField.getText();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSendChatMessage(char character, int keyCode, CallbackInfo ci, String var3, String message) {
        int size = CHAT_HISTORY.size();
        if (size > 0 && CHAT_HISTORY.get(size - 1).equals(message)) {
            return;
        }

        CHAT_HISTORY.add(message);
    }

    private void setTextFromHistory() {
        this.textField.setText(CHAT_HISTORY.get(CHAT_HISTORY.size() + this.chatHistoryPosition));
    }

    @Inject(method = "keyPressed", at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPNE, ordinal = 2), cancellable = true)
    private void onKeyPressed(char character, int keyCode, CallbackInfo ci) {
        if (keyCode == Keyboard.KEY_UP && this.chatHistoryPosition > -CHAT_HISTORY.size()) {
            --this.chatHistoryPosition;
            this.setTextFromHistory();
        } else if (keyCode == Keyboard.KEY_DOWN && this.chatHistoryPosition < -1) {
            ++this.chatHistoryPosition;
            this.setTextFromHistory();
        } else {
            this.textField.keyPressed(character, keyCode);
        }

        ci.cancel();
    }
}
