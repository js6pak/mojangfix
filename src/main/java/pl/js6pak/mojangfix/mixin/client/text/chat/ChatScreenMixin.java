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
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private static final List<String> CHAT_HISTORY = new ArrayList<>();
    @Shadow
    protected String text;
    @Unique
    private int chatHistoryPosition = 0;
    @Unique
    private String afterText = "";

    @Unique
    void setChatHistory() {
        if (chatHistoryPosition == 0) {
            text = "";
            return;
        }

        text = CHAT_HISTORY.get(CHAT_HISTORY.size() - chatHistoryPosition);
    }

    @Inject(method = "keyPressed", at = @At("TAIL"))
    protected void keyPressed(char keyCode, int scanCode, CallbackInfo ci) {
        if (scanCode == 200 && chatHistoryPosition < CHAT_HISTORY.size()) // up
        {
            chatHistoryPosition++;
            setChatHistory();
        } else if (scanCode == 208 && chatHistoryPosition > 0) // down
        {
            chatHistoryPosition--;
            setChatHistory();
        }
        if (scanCode == 203) // left
        {
            if (text.length() > 0) {
                afterText = text.charAt(text.length() - 1) + afterText;
                text = text.substring(0, text.length() - 1);
            }
        }
        if (scanCode == 205) // right
        {
            if (afterText.length() > 0) {
                text = text + afterText.charAt(0);
                afterText = afterText.substring(1);
            }
        }
        if (scanCode == 211) // del
        {
            if (afterText.length() > 0) {
                afterText = afterText.substring(1);
            }
        }
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V"))
    protected void sendChat(ClientPlayerEntity instance, String s) {
        CHAT_HISTORY.add(s + afterText);
        instance.sendChatMessage(s + afterText);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;drawStringWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    public void drawTextWithAfterText(ChatScreen instance, TextRenderer textRenderer, String s, int x, int y, int c) {
        instance.drawStringWithShadow(textRenderer, s + afterText, x, y, c);
    }
}
