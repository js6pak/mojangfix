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

import com.github.steveice10.mc.auth.data.GameProfile;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class PlayerProfile {
    private UUID uuid;
    private String skinUrl;
    private String capeUrl;
    private GameProfile.TextureModel model;
    private Instant lastFetched = Instant.now();

    public PlayerProfile(UUID uuid, String skinUrl, String capeUrl, GameProfile.TextureModel model) {
        this.uuid = uuid;
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.model = model;
    }
}
