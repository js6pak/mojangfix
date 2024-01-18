///*
// * Copyright (C) 2022 js6pak
// *
// * This file is part of MojangFix.
// *
// * MojangFix is free software: you can redistribute it and/or modify it under the terms of the
// * GNU Lesser General Public License as published by the Free Software Foundation, version 3.
// *
// * MojangFix is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// * See the GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License along with MojangFix. If not, see <https://www.gnu.org/licenses/>.
// */
//
//package pl.js6pak.mojangfix.mixin.client.inventory;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.network.MultiplayerClientPlayerEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import org.lwjgl.input.Keyboard;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import pl.js6pak.mojangfix.mixin.client.MinecraftAccessor;
//
//@Mixin({MultiplayerClientPlayerEntity.class, PlayerEntity.class})
//public abstract class PlayerEntityMixin {
//    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
//    private void onDropSelectedItem(CallbackInfo ci) {
//        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
//            Minecraft minecraft = MinecraftAccessor.getInstance();
//            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
//
//            minecraft.interactionManager.clickSlot(0, 36 + playerEntity.inventory.selectedSlot, 0, false, minecraft.player);
//            minecraft.interactionManager.clickSlot(0, -999, 0, false, minecraft.player);
//            ci.cancel();
//        }
//    }
//}
