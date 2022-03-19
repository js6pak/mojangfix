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

package pl.js6pak.mojangfix.mixin.client.auth;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import lombok.Getter;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.MojangFixMod;
import pl.js6pak.mojangfix.client.skinfix.SkinService;
import pl.js6pak.mojangfix.mixinterface.SessionAccessor;

import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(Session.class)
public class SessionMixin implements SessionAccessor {
    @Unique
    @Getter
    private GameProfile gameProfile;

    @Unique
    @Getter
    private String accessToken;

    @Unique
    @Getter
    private SessionService sessionService;

    @Unique
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String username, String sessionId, CallbackInfo ci) {
        this.sessionService = new SessionService();

        String[] split = sessionId.split(":");
        if (split.length == 3 && split[0].equalsIgnoreCase("token")) {
            accessToken = split[1];
            UUID uuid = UUID.fromString(UUID_PATTERN.matcher(split[2]).replaceAll("$1-$2-$3-$4-$5"));
            gameProfile = new GameProfile(uuid, username);

            MojangFixMod.getLogger().info("Signed as {} ({})", username, uuid);
        } else {
            MojangFixMod.getLogger().info("Signed as {}", username);
        }

        SkinService.getInstance().init(username);
    }
}
