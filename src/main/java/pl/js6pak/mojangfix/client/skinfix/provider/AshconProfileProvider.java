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
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.util.HTTP;
import lombok.Data;

import java.net.Proxy;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AshconProfileProvider implements ProfileProvider {
    @Override
    public Future<GameProfile> get(String username) {
        try {
            Response response = HTTP.makeRequest(Proxy.NO_PROXY, URI.create("https://api.ashcon.app/mojang/v2/user/" + username), null, Response.class);
            GameProfile gameProfile = new GameProfile(response.uuid, username);

            Response.Textures.Property raw = response.textures.raw;
            gameProfile.setProperties(Collections.singletonList(new GameProfile.Property("textures", raw.value, raw.signature)));

            return CompletableFuture.completedFuture(gameProfile);
        } catch (RequestException e) {
            CompletableFuture<GameProfile> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Data
    private static class Response {
        private UUID uuid;
        private Textures textures;

        @Data
        private static class Textures {
            private boolean slim;
            private boolean custom;
            private Property raw;

            @Data
            private static class Property {
                private String value;
                private String signature;
            }
        }
    }
}
