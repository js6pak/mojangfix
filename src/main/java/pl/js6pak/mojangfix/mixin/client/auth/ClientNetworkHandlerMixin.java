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

import com.github.steveice10.mc.auth.exception.request.RequestException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.handshake.HandshakePacket;
import net.minecraft.network.packet.login.LoginHelloPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.js6pak.mojangfix.mixinterface.SessionAccessor;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private Connection connection;

    @Shadow
    public abstract void sendPacket(Packet arg);

    @Redirect(method = "handleHandshake", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean checkServerId(String serverId, Object offline) {
        return serverId.trim().isEmpty() || serverId.equals(offline) || this.minecraft.session.sessionId.trim().isEmpty() || this.minecraft.session.sessionId.equals(offline);
    }

    @Inject(method = "handleHandshake", at = @At(value = "NEW", target = "java/net/URL"), cancellable = true)
    private void onJoinServer(HandshakePacket packet, CallbackInfo ci) {
        SessionAccessor session = (SessionAccessor) this.minecraft.session;

        try {
            if (session.getGameProfile() == null || session.getAccessToken() == null) {
                this.connection.disconnect("disconnect.loginFailedInfo", "Invalid access token!");
            }

            session.getSessionService().joinServer(session.getGameProfile(), session.getAccessToken(), packet.name);
            this.sendPacket(new LoginHelloPacket(this.minecraft.session.username, 14));
        } catch (RequestException e) {
            this.connection.disconnect("disconnect.loginFailedInfo", e.getClass().getSimpleName() + "\n" + e.getMessage());
        }

        ci.cancel();
    }
}
