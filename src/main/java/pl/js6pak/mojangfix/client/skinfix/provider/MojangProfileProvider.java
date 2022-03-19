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

package pl.js6pak.mojangfix.client.skinfix.provider;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.profile.ProfileException;
import com.github.steveice10.mc.auth.service.ProfileService;
import com.github.steveice10.mc.auth.service.SessionService;
import pl.js6pak.mojangfix.mixin.client.MinecraftAccessor;
import pl.js6pak.mojangfix.mixinterface.SessionAccessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class MojangProfileProvider implements ProfileProvider {
    private final ProfileService profileService = new ProfileService();

    @Override
    public Future<GameProfile> get(String username) {
        CompletableFuture<GameProfile> future = new CompletableFuture<>();

        final SessionAccessor session = (SessionAccessor) MinecraftAccessor.getInstance().session;
        profileService.findProfilesByName(new String[]{username}, new ProfileService.ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile profile) {
                SessionService sessionService = session.getSessionService();

                try {
                    sessionService.fillProfileProperties(profile);
                    future.complete(profile);
                } catch (ProfileException e) {
                    future.completeExceptionally(e);
                }
            }

            public void onProfileLookupFailed(GameProfile profile, Exception e) {
                future.completeExceptionally(e);
            }
        }, false);

        return future;
    }
}
