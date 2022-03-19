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
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import pl.js6pak.mojangfix.client.gui.CallbackButtonWidget;
import pl.js6pak.mojangfix.client.gui.CallbackConfirmScreen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerScreen extends Screen {
    private final Screen parent;
    private String title;
    private boolean joining;

    @Getter
    private ServerData selectedServer;

    @Getter
    private List<ServerData> serversList;

    private MultiplayerServerListWidget serverListWidget;

    private ButtonWidget buttonEdit;
    private ButtonWidget buttonConnect;
    private ButtonWidget buttonDelete;

    public MultiplayerScreen() {
        this(new TitleScreen());
    }

    public MultiplayerScreen(Screen parent) {
        this.parent = parent;
    }

    public void init() {
        this.title = TranslationStorage.getInstance().get("multiplayer.title");
        this.loadServers();
        this.serverListWidget = new MultiplayerServerListWidget(this);
        this.initButtons();
    }

    private void loadServers() {
        this.selectedServer = null;

        try {
            File serversFile = new File(Minecraft.getRunDirectory(), "servers.dat");
            if (serversFile.exists()) {
                NbtCompound nbt = NbtIo.read(new DataInputStream(new FileInputStream(serversFile)));
                this.serversList = ServerData.load(nbt.getList("servers"));
            } else {
                this.serversList = new ArrayList<>();
                this.saveServers();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initButtons() {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        this.buttons.add(this.buttonConnect = new CallbackButtonWidget(this.width / 2 - 150 - 4, this.height - 52, 100, 20, "Connect", button -> {
            this.joinServer(this.selectedServer);
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, "Direct connect", button -> {
            this.minecraft.setScreen(new DirectConnectScreen(this));
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 50 + 4, this.height - 52, 100, 20, "Add server", button -> {
            this.minecraft.setScreen(new EditServerScreen(this, null));
        }));
        this.buttons.add(this.buttonEdit = new CallbackButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, "Edit", button -> {
            this.minecraft.setScreen(new EditServerScreen(this, this.selectedServer));
        }));
        this.buttons.add(this.buttonDelete = new CallbackButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, translationStorage.get("selectWorld.delete"), button -> {
            TranslationStorage translate = TranslationStorage.getInstance();
            this.minecraft.setScreen(new CallbackConfirmScreen(this,
                    translate.get("Are you sure you want to delete this server?"),
                    "'" + this.selectedServer.getName() + "' " + translate.get("selectWorld.deleteWarning"),
                    translate.get("selectWorld.deleteButton"),
                    translate.get("gui.cancel"),
                    (result) -> {
                        if (result) {
                            this.deleteServer(this.selectedServer);
                        }

                        this.minecraft.setScreen(this);
                    }));
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 + 4, this.height - 28, 150, 20, translationStorage.get("gui.cancel"), button -> {
            this.minecraft.setScreen(this.parent);
        }));
        this.buttonConnect.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        this.serverListWidget.buttonClicked(button);
    }

    public void selectServer(int slot, boolean join) {
        selectedServer = serversList.get(slot);
        boolean selected = selectedServer != null;

        buttonEdit.active = selected;
        buttonDelete.active = selected;
        buttonConnect.active = selected;

        if (selected && join) {
            joinServer(selectedServer);
        }
    }

    public void joinServer(ServerData server) {
        this.minecraft.setScreen(null);
        if (!this.joining) {
            this.joining = true;
            DirectConnectScreen.connect(this.minecraft, server.getIp());
        }
    }

    public void deleteServer(ServerData server) {
        this.serversList.remove(server);
        this.saveServers();
    }

    public void saveServers() {
        try {
            NbtCompound compound = new NbtCompound();
            compound.put("servers", ServerData.save(serversList));
            NbtIo.write(compound, new DataOutputStream(new FileOutputStream(new File(Minecraft.getRunDirectory(), "servers.dat"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.serverListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public TextRenderer getFontRenderer() {
        return this.textRenderer;
    }
}
