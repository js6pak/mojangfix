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
import lombok.Setter;
import net.minecraft.client.texture.SkinImageProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pl.js6pak.mojangfix.mixinterface.SkinImageProcessorAccessor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

@Mixin(SkinImageProcessor.class)
public class SkinImageProcessorMixin implements SkinImageProcessorAccessor {
    @Setter
    private GameProfile.TextureModel textureModel;

    @ModifyConstant(method = "process", constant = @Constant(intValue = 32, ordinal = 0))
    private int getImageHeight(int def) {
        return 64;
    }

    @Inject(method = "process", at = @At(value = "INVOKE", target = "Ljava/awt/Graphics;dispose()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSkinGraphics(BufferedImage bufferedImage, CallbackInfoReturnable<BufferedImage> cir, BufferedImage parsedImage, Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D graphics = (Graphics2D) g;
            if (bufferedImage.getHeight() == 32) {
                graphics.drawImage(bufferedImage.getSubimage(0, 16, 16, 16), 16, 48, 16, 16, null);
                graphics.drawImage(bufferedImage.getSubimage(40, 16, 16, 16), 32, 48, 16, 16, null);
            }

            for (int i = 0; i < 2; ++i) {
                this.flipArea(graphics, parsedImage, 16 + i * 32, 0, 8, 8);
            }

            boolean isSlim = textureModel == GameProfile.TextureModel.SLIM;

            for (int i = 1; i <= 2; ++i) {
                this.flipArea(graphics, parsedImage, 8, i * 16, 4, 4);
                this.flipArea(graphics, parsedImage, 28, i * 16, 8, 4);
                this.flipArea(graphics, parsedImage, isSlim ? 47 : 48, i * 16, isSlim ? 3 : 4, 4);
            }

            for (int i = 0; i < 4; ++i) {
                boolean isSlimArm = isSlim && i >= 2;
                this.flipArea(graphics, parsedImage, (isSlimArm ? 7 : 8) + i * 16, 48, isSlimArm ? 3 : 4, 4);
            }
        }

    }

    public BufferedImage deepCopy(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }

    private void flipArea(Graphics2D graphics, BufferedImage bufferedImage, int x, int y, int width, int height) {
        BufferedImage image = this.deepCopy(bufferedImage.getSubimage(x, y, width, height));
        graphics.setBackground(new Color(0, true));
        graphics.clearRect(x, y, width, height);
        graphics.drawImage(image, x, y + height, width, -height, null);
    }
}
