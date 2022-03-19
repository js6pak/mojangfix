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

import com.github.steveice10.mc.auth.data.GameProfile;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityAccessor;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityRendererAccessor;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private Map<Class<? extends Entity>, EntityRenderer> renderers;

    @Unique
    private final Map<GameProfile.TextureModel, PlayerEntityRenderer> playerRenderers = new HashMap<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        for (GameProfile.TextureModel model : GameProfile.TextureModel.values()) {
            PlayerEntityRenderer renderer = new PlayerEntityRenderer();
            ((PlayerEntityRendererAccessor) renderer).setThinArms(model == GameProfile.TextureModel.SLIM);
            renderer.setDispatcher((EntityRenderDispatcher) (Object) this);
            playerRenderers.put(model, renderer);
        }

        this.renderers.put(PlayerEntity.class, playerRenderers.get(GameProfile.TextureModel.NORMAL));
    }

    @Inject(method = "get(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    private void onGet(Entity entity, CallbackInfoReturnable<EntityRenderer> cir) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            GameProfile.TextureModel textureModel = ((PlayerEntityAccessor) player).getTextureModel();
            cir.setReturnValue(playerRenderers.get(textureModel));
        }
    }
}
