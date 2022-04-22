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

package pl.js6pak.mojangfix.mixin.server.auth;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.SessionService;
import net.minecraft.network.packet.login.LoginHelloPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.*;
import pl.js6pak.mojangfix.MojangFixMod;

@Mixin(targets = "net.minecraft.server.network.ServerLoginNetworkHandler$AuthThread")
public class ServerNetworkHandlerMixin {
    @Shadow
    @Final
    ServerLoginNetworkHandler networkHandler;

    @Shadow
    @Final
    LoginHelloPacket loginPacket;

    @Unique
    private static final SessionService SESSION_SERVICE = new SessionService();

    /**
     * @reason Swap auth logic completely
     * @author js6pak
     */
    @Overwrite(remap = false)
    public void run() {
        try {
            ServerNetworkHandlerAccessor accessor = (ServerNetworkHandlerAccessor) networkHandler;

            try {
                GameProfile gameProfile = SESSION_SERVICE.getProfileByServer(loginPacket.username, accessor.getServerId());

                if (gameProfile != null) {
                    MojangFixMod.getLogger().info("Authenticated " + gameProfile.getName() + " as " + gameProfile.getId());
                    accessor.setLoginPacket(loginPacket);
                    return;
                }
            } catch (RequestException ignored) {
            }

            networkHandler.disconnect("Failed to verify username!");
        } catch (Exception e) {
            networkHandler.disconnect("Failed to verify username! [internal error " + e + "]");
            e.printStackTrace();
        }
    }
}
