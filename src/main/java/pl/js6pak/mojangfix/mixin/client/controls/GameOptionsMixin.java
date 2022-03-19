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

package pl.js6pak.mojangfix.mixin.client.controls;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.client.MojangFixClientMod;
import pl.js6pak.mojangfix.mixinterface.GameSettingsAccessor;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements GameSettingsAccessor {
    @Shadow
    public KeyBinding[] allKeys;

    @Unique
    @Getter
    @Setter
    private boolean showDebugInfoGraph;

    @Inject(method = {"<init>()V", "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V"}, at = @At("RETURN"))
    public void onInit(CallbackInfo ci) {
        ArrayList<KeyBinding> newKeys = new ArrayList<>(Arrays.asList(allKeys));
        newKeys.add(MojangFixClientMod.COMMAND_KEYBIND);
        allKeys = newKeys.toArray(new KeyBinding[0]);
    }
}
