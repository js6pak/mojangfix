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

package pl.js6pak.mojangfix.mixin.client.text.sign;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.network.packet.play.UpdateSignPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pl.js6pak.mojangfix.mixinterface.SignBlockEntityAccessor;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {
    @Inject(method = "handleUpdateSign", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;markDirty()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onHandleSignUpdate(UpdateSignPacket packet, CallbackInfo ci, BlockEntity blockEntity, SignBlockEntity sign, int var4) {
        TextFieldWidget[] textFields = ((SignBlockEntityAccessor) sign).getTextFields();
        for (int i = 0; i < packet.text.length; i++) {
            textFields[i].setText(packet.text[i]);
        }
    }
}
