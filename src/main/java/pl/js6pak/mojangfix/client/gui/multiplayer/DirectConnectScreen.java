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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Keyboard;
import pl.js6pak.mojangfix.client.gui.CallbackButtonWidget;

public class DirectConnectScreen extends Screen {
    private static final int DEFAULT_PORT = 25565;

    private final Screen parent;
    private TextFieldWidget addressField;
    private CallbackButtonWidget connectButton;

    public DirectConnectScreen(Screen parent) {
        this.parent = parent;
    }

    public static int parseIntWithDefault(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static void connect(Minecraft minecraft, String addressText) {
        String[] split = addressText.split(":");
        minecraft.setScreen(new ConnectScreen(minecraft, split[0], split.length > 1 ? parseIntWithDefault(split[1], DEFAULT_PORT) : DEFAULT_PORT));
    }

    @Override
    public void tick() {
        this.addressField.tick();
    }

    public void init() {
        Keyboard.enableRepeatEvents(true);

        TranslationStorage translationStorage = TranslationStorage.getInstance();
        this.buttons.add(connectButton = new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, translationStorage.get("multiplayer.connect"), button -> {
            String address = this.addressField.getText().trim();
            this.minecraft.options.lastServer = address.replaceAll(":", "_");
            this.minecraft.options.save();
            connect(this.minecraft, address);
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, translationStorage.get("gui.cancel"), button -> {
            this.minecraft.setScreen(this.parent);
        }));
        String lastServer = this.minecraft.options.lastServer.replaceAll("_", ":");
        connectButton.active = lastServer.length() > 0;
        this.addressField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, this.height / 4 - 10 + 50 + 18, 200, 20, lastServer);
        this.addressField.focused = true;
        this.addressField.setMaxLength(128);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyPressed(char character, int keyCode) {
        this.addressField.keyPressed(character, keyCode);
        if (keyCode == Keyboard.KEY_RETURN) {
            this.buttonClicked((ButtonWidget) this.buttons.get(0));
        }

        ((ButtonWidget) this.buttons.get(0)).active = this.addressField.getText().length() > 0;
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.addressField.mouseClicked(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, "Direct connect", this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawCenteredTextWithShadow(this.textRenderer, TranslationStorage.getInstance().get("multiplayer.ipinfo"), this.width / 2, this.height / 4 - 60 + 60 + 36, 10526880);
        this.addressField.render();
        super.render(mouseX, mouseY, delta);
    }
}
