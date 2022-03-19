/*
 * Copyright (C) 2022 js6pak
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

package pl.js6pak.mojangfix.mixin.client.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.mixinterface.TextFieldWidgetAccessor;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldWidgetAccessor {
    @Shadow
    public boolean focused;

    @Shadow
    public boolean enabled;

    @Shadow
    private String text;

    @Shadow
    private int focusedTicks;

    @Shadow
    private int maxLength;

    @Unique
    private int cursorPosition;

    @Redirect(method = "keyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;text:Ljava/lang/String;", opcode = Opcodes.PUTFIELD))
    private void onClipboardPaste(TextFieldWidget guiTextField, String value) {
        this.write(value.substring(this.text.length()));
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = Keyboard.KEY_BACK))
    private int cancelRemoveKey(int def) {
        return -1;
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 32))
    private int getClipboardMaxLength(int def) {
        return this.maxLength;
    }

    @Inject(method = "keyPressed", at = @At(value = "JUMP", ordinal = 2))
    private void onKeyTyped(char character, int keyCode, CallbackInfo ci) {
        if (keyCode == Keyboard.KEY_BACK && this.text.length() > 0 && -this.cursorPosition < this.text.length()) {
            this.text = (new StringBuilder(this.text)).deleteCharAt(this.text.length() + this.cursorPosition - 1).toString();
        }

        if (keyCode == Keyboard.KEY_DELETE && this.text.length() > 0 && this.cursorPosition < 0) {
            this.text = (new StringBuilder(this.text)).delete(this.text.length() + this.cursorPosition, this.text.length() + this.cursorPosition + 1).toString();
            ++this.cursorPosition;
        }

        if (keyCode == Keyboard.KEY_LEFT && -this.cursorPosition < this.text.length()) {
            --this.cursorPosition;
        }

        if (keyCode == Keyboard.KEY_RIGHT && this.cursorPosition < 0) {
            ++this.cursorPosition;
        }

        if (keyCode == Keyboard.KEY_HOME) {
            this.cursorPosition = -this.text.length();
        }

        if (keyCode == Keyboard.KEY_END) {
            this.cursorPosition = 0;
        }
    }

    public void write(String text) {
        this.text = (new StringBuilder(this.text)).insert(this.text.length() + this.cursorPosition, text).toString();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;drawStringWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void onDrawTextBox(TextFieldWidget guiTextField, TextRenderer textRenderer, String text, int x, int y, int color) {
        guiTextField.drawStringWithShadow(textRenderer, this.getDisplayText(), x, y, color);
    }

    public String getDisplayText() {
        boolean caretVisible = this.focused && this.focusedTicks / 6 % 2 == 0;
        return this.enabled ? (new StringBuilder(this.text)).insert(this.text.length() + this.cursorPosition, caretVisible ? "_" : "").toString() : this.text;
    }
}
