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

package pl.js6pak.mojangfix.client.skinfix;

import net.minecraft.client.texture.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CapeImageProcessor implements ImageProcessor {
    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            return null;
        } else {
            int width = 64;
            int height = 32;

            for (int i = image.getHeight(); width < image.getWidth() || height < i; height *= 2) {
                width *= 2;
            }

            BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedimage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return bufferedimage;
        }
    }
}
