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
import com.github.steveice10.mc.auth.exception.property.PropertyException;
import net.minecraft.entity.player.PlayerEntity;
import pl.js6pak.mojangfix.MojangFixMod;
import pl.js6pak.mojangfix.client.skinfix.provider.AshconProfileProvider;
import pl.js6pak.mojangfix.client.skinfix.provider.MojangProfileProvider;
import pl.js6pak.mojangfix.client.skinfix.provider.ProfileProvider;
import pl.js6pak.mojangfix.mixin.client.MinecraftAccessor;
import pl.js6pak.mojangfix.mixinterface.PlayerEntityAccessor;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public class SkinService {
    public static final String RESOURCES_URL = "http://mcresources.modification-station.net/MinecraftResources/";
    public static final String STEVE_TEXTURE = "/mob/steve.png";
    public static final String ALEX_TEXTURE = "/mob/alex.png";

    private static final SkinService INSTANCE = new SkinService();

    public static SkinService getInstance() {
        return INSTANCE;
    }

    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Map<String, PlayerProfile> profiles = new HashMap<>();

    private final List<ProfileProvider> providers = Arrays.asList(new AshconProfileProvider(), new MojangProfileProvider());

    private static GameProfile.TextureModel getTextureModelForUUID(UUID uuid) {
        return (uuid.hashCode() & 1) != 0 ? GameProfile.TextureModel.SLIM : GameProfile.TextureModel.NORMAL;
    }

    private void updatePlayer(PlayerEntity player, PlayerProfile playerProfile) {
        if (playerProfile == null) return;

        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        accessor.setTextureModel(playerProfile.getModel());
        player.skinUrl = playerProfile.getSkinUrl();
        player.capeUrl = player.playerCapeUrl = playerProfile.getCapeUrl();
        MinecraftAccessor.getInstance().worldRenderer.loadEntitySkin(player);
    }

    private boolean updatePlayer(PlayerEntity player) {
        if (profiles.containsKey(player.name)) {
            PlayerProfile profile = profiles.get(player.name);
            updatePlayer(player, profile);
            return true;
        }

        return false;
    }

    private void initOffline(PlayerEntity player) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.name).getBytes(StandardCharsets.UTF_8));
        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        final GameProfile.TextureModel model = getTextureModelForUUID(uuid);
        accessor.setTextureModel(model);
    }

    public void init(PlayerEntity player) {
        if (updatePlayer(player)) return;

        initOffline(player);

        (new Thread(() -> {
            init(player.name);
            updatePlayer(player);
        })).start();
    }

    public void init(String name) {
        if (profiles.containsKey(name)) return;

        ReentrantLock lock;
        if (locks.containsKey(name)) {
            lock = locks.get(name);
        } else {
            locks.put(name, lock = new ReentrantLock());
        }

        lock.lock();
        try {
            if (profiles.containsKey(name)) return;

            for (ProfileProvider provider : providers) {
                GameProfile profile;

                try {
                    profile = provider.get(name).get();
                } catch (InterruptedException | ExecutionException e) {
                    MojangFixMod.getLogger().warn("Lookup using {} for profile {} failed!", provider.getClass().getSimpleName(), name);
                    continue;
                }

                Map<GameProfile.TextureType, GameProfile.Texture> textures;

                try {
                    textures = profile.getTextures();
                } catch (PropertyException e) {
                    MojangFixMod.getLogger().warn("Texture decoding using {} for profile {} failed!", provider.getClass().getSimpleName(), name);
                    continue;
                }

                GameProfile.Texture skin = textures.get(GameProfile.TextureType.SKIN);
                GameProfile.TextureModel model = skin == null ? SkinService.getTextureModelForUUID(profile.getId()) : skin.getModel();
                String skinUrl = skin == null ? null : skin.getURL();
                GameProfile.Texture cape = textures.get(GameProfile.TextureType.CAPE);
                String capeUrl = cape == null ? null : cape.getURL();
                PlayerProfile playerProfile = new PlayerProfile(profile.getId(), skinUrl, capeUrl, model);

                MojangFixMod.getLogger().info("Downloaded profile: " + profile.getName() + " (" + profile.getId() + ")");
                profiles.put(name, playerProfile);
                return;
            }

            profiles.put(name, null);
        } finally {
            lock.unlock();
        }
    }
}
