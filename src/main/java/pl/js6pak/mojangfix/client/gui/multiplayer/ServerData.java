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

package pl.js6pak.mojangfix.client.gui.multiplayer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ServerData {
    @NonNull
    private String name;

    @NonNull
    private String ip;

    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putString("ip", ip);
        return nbt;
    }

    public ServerData(NbtCompound nbt) {
        this(nbt.getString("name"), nbt.getString("ip"));
    }

    public static NbtList save(List<ServerData> servers) {
        NbtList nbt = new NbtList();
        for (ServerData server : servers) {
            nbt.add(server.save());
        }
        return nbt;
    }

    public static List<ServerData> load(NbtList nbt) {
        ArrayList<ServerData> servers = new ArrayList<>();
        for (int i = 0; i < nbt.size(); i++) {
            servers.add(new ServerData((NbtCompound) nbt.get(i)));
        }
        return servers;
    }
}
