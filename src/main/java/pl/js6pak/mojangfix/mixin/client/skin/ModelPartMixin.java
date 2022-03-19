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

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.Quad;
import net.minecraft.client.model.Vertex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pl.js6pak.mojangfix.mixinterface.ModelPartAccessor;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartAccessor {
    @Unique
    @Setter
    @Getter
    private int textureWidth = 64;

    @Unique
    @Setter
    @Getter
    private int textureHeight = 32;

    @Redirect(method = "addCuboid(FFFIIIF)V", at = @At(value = "NEW", target = "([Lnet/minecraft/client/model/Vertex;IIII)Lnet/minecraft/client/model/Quad;"))
    private Quad redirectQuad(Vertex[] vertices, int u1, int v1, int u2, int v2) {
        Quad quad = new Quad(vertices);

        vertices[0] = vertices[0].remap((float) u2 / textureWidth, (float) v1 / textureHeight);
        vertices[1] = vertices[1].remap((float) u1 / textureWidth, (float) v1 / textureHeight);
        vertices[2] = vertices[2].remap((float) u1 / textureWidth, (float) v2 / textureHeight);
        vertices[3] = vertices[3].remap((float) u2 / textureWidth, (float) v2 / textureHeight);

        return quad;
    }
}
