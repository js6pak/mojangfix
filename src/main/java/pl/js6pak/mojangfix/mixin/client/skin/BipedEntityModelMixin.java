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

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import pl.js6pak.mojangfix.mixinterface.ModelPartAccessor;
import pl.js6pak.mojangfix.client.skinfix.PlayerEntityModel;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin {
    @Redirect(
            method = "<init>(FF)V",
            at = @At(value = "NEW", target = "net/minecraft/client/model/ModelPart"),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;ears:Lnet/minecraft/client/model/ModelPart;", shift = Shift.AFTER))
    )
    private ModelPart onTexturedQuad(int u, int v) {
        ModelPart modelPart = new ModelPart(u, v);

        BipedEntityModel self = (BipedEntityModel) (Object) this;
        if (self instanceof PlayerEntityModel) {
            ((ModelPartAccessor) modelPart).setTextureHeight(64);
        }

        return modelPart;
    }
}
