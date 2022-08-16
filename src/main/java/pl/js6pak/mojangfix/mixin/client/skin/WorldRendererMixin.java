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

package pl.js6pak.mojangfix.mixin.client.skin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.ImageProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.client.skinfix.CapeImageProcessor;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityAccessor;
import pl.js6pak.mojangfix.mixinterface.SkinImageProcessorAccessor;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "unloadEntitySkin", at = @At("HEAD"), cancellable = true)
    private void dontUnloadLocalPlayerSkin(Entity entity, CallbackInfo ci) {
        if (entity instanceof ClientPlayerEntity) {
            ci.cancel();
        }
    }

    @Unique
    private Entity currentEntity; // I hate this but there is no way to get it from @ModifyArg

    @Inject(method = "loadEntitySkin", at = @At("HEAD"))
    private void getEnttity(Entity entity, CallbackInfo ci) {
        currentEntity = entity;
    }

    @ModifyArg(
        method = "loadEntitySkin", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;downloadImage(Ljava/lang/String;Lnet/minecraft/client/texture/ImageProcessor;)Lnet/minecraft/client/texture/ImageDownload;", ordinal = 0)
    )
    private ImageProcessor redirectSkinProcessor(ImageProcessor def) {
        if (currentEntity instanceof PlayerEntity) {
            PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) currentEntity;
            SkinImageProcessorAccessor skinImageProcessorAccessor = (SkinImageProcessorAccessor) def;
            skinImageProcessorAccessor.setTextureModel(playerEntityAccessor.getTextureModel());
        }
        return def;
    }

    @ModifyArg(
        method = "loadEntitySkin", index = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;downloadImage(Ljava/lang/String;Lnet/minecraft/client/texture/ImageProcessor;)Lnet/minecraft/client/texture/ImageDownload;", ordinal = 1)
    )
    private ImageProcessor redirectCapeProcessor(ImageProcessor def) {
        return new CapeImageProcessor();
    }
}
