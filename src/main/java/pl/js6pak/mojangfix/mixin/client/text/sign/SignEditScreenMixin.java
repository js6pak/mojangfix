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

package pl.js6pak.mojangfix.mixin.client.text.sign;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.mixinterface.SignBlockEntityAccessor;

import java.util.Arrays;

@Mixin(SignEditScreen.class)
public class SignEditScreenMixin {
    @Shadow
    private SignBlockEntity sign;

    @Shadow
    private int currentRow;

    @Shadow
    private int ticksSinceOpened;

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        TextFieldWidget[] textFields = ((SignBlockEntityAccessor) this.sign).getTextFields();
        textFields[0].setFocused(true);
    }

    @Inject(method = "keyPressed", at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPNE, ordinal = 2), cancellable = true)
    private void onKeyPressed(char character, int keyCode, CallbackInfo ci) {
        TextFieldWidget[] textFields = ((SignBlockEntityAccessor) this.sign).getTextFields();
        for (TextFieldWidget textField : textFields) {
            textField.setFocused(false);
        }
        textFields[this.currentRow].setFocused(true);
        textFields[this.currentRow].keyPressed(character, keyCode);
        this.sign.texts[this.currentRow] = textFields[this.currentRow].getText();
        ci.cancel();
    }

    @Inject(method = "removed", at = @At("RETURN"))
    private void onRemoved(CallbackInfo ci) {
        TextFieldWidget[] textFields = ((SignBlockEntityAccessor) this.sign).getTextFields();
        for (TextFieldWidget textField : textFields) {
            textField.setFocused(false);
        }
    }

    @Redirect(method = "removed", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/SignBlockEntity;texts:[Ljava/lang/String;"))
    private String[] getSignText(SignBlockEntity sign) {
        return Arrays.stream(((SignBlockEntityAccessor) sign).getTextFields()).map(TextFieldWidget::getText).toArray(String[]::new);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        ((SignBlockEntityAccessor) this.sign).getTextFields()[this.currentRow].tick();
        ticksSinceOpened = 6;
        ci.cancel();
    }
}