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

package pl.js6pak.mojangfix.mixin.client.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.client.MojangFixClientMod;
import pl.js6pak.mojangfix.mixinterface.ChatScreenAccessor;
import pl.js6pak.mojangfix.mixinterface.GameSettingsAccessor;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public GameOptions options;

    @Shadow
    public abstract void setScreen(Screen screen);

    @Shadow
    public abstract boolean isWorldRemote();

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugHud:Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void onF3(CallbackInfo ci) {
        GameSettingsAccessor gameSettings = (GameSettingsAccessor) this.options;
        gameSettings.setShowDebugInfoGraph(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
    }

    @Redirect(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugHud:Z", opcode = Opcodes.GETFIELD))
    private boolean getShowDebugInfo(GameOptions gameSettings) {
        return gameSettings.debugHud && ((GameSettingsAccessor) this.options).isShowDebugInfoGraph();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isWorldRemote()Z", ordinal = 0))
    private void onKey(CallbackInfo ci) {
        if (this.isWorldRemote() && Keyboard.getEventKey() == MojangFixClientMod.COMMAND_KEYBIND.code) {
            this.setScreen(((ChatScreenAccessor) new ChatScreen()).setInitialMessage("/"));
        }
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V", ordinal = 0, remap = false))
    private void onDisplayCreate() throws LWJGLException {
        PixelFormat pixelformat = new PixelFormat();
        pixelformat = pixelformat.withDepthBits(24);
        Display.create(pixelformat);
    }

    @Inject(method = "startSessionCheck", at = @At("HEAD"), cancellable = true)
    private void disableSessionCheck(CallbackInfo ci) {
        ci.cancel();
    }
}
