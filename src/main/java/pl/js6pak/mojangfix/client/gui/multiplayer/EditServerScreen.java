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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Keyboard;
import pl.js6pak.mojangfix.client.gui.CallbackButtonWidget;

public class EditServerScreen extends Screen {
    private final ServerData server;
    private ButtonWidget button;
    private final MultiplayerScreen parent;
    private TextFieldWidget nameTextField;
    private TextFieldWidget ipTextField;

    public EditServerScreen(MultiplayerScreen parent, ServerData server) {
        this.parent = parent;
        this.server = server;
    }

    public void tick() {
        this.nameTextField.tick();
        this.ipTextField.tick();
    }

    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(this.button = new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, this.server == null ? "Add" : "Edit", button -> {
            if (this.server != null) {
                this.server.setName(this.nameTextField.getText());
                this.server.setIp(this.ipTextField.getText());
            } else {
                this.parent.getServersList().add(new ServerData(this.nameTextField.getText(), this.ipTextField.getText()));
            }

            this.parent.saveServers();
            this.minecraft.setScreen(this.parent);
        }));
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, TranslationStorage.getInstance().get("gui.cancel"), button -> {
            this.minecraft.setScreen(this.parent);
        }));
        this.nameTextField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, 60, 200, 20, this.server == null ? "" : this.server.getName());
        this.nameTextField.setMaxLength(32);
        this.ipTextField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, 106, 200, 20, this.server == null ? "" : this.server.getIp());
        this.ipTextField.setMaxLength(32);
        this.updateButton();
    }

    private void updateButton() {
        this.button.active = this.nameTextField.getText().trim().length() > 0 && this.ipTextField.getText().trim().length() > 0;
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        this.nameTextField.keyPressed(character, keyCode);
        this.ipTextField.keyPressed(character, keyCode);
        this.updateButton();
        if (character == Keyboard.KEY_RETURN) {
            this.buttonClicked(this.button);
        }

    }

    protected void mouseClicked(int mouseX, int mouseY, int varbutton) {
        super.mouseClicked(mouseX, mouseY, varbutton);
        this.nameTextField.mouseClicked(mouseX, mouseY, varbutton);
        this.ipTextField.mouseClicked(mouseX, mouseY, varbutton);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawStringWithShadow(this.textRenderer, (this.server == null ? "Add" : "Edit") + " Server", this.width / 2, 20, 16777215);
        this.drawStringWithShadow(this.textRenderer, "Server name", this.width / 2 - 100, 47, 10526880);
        this.drawStringWithShadow(this.textRenderer, "Server IP", this.width / 2 - 100, 94, 10526880);
        this.nameTextField.render();
        this.ipTextField.render();
        super.render(mouseX, mouseY, delta);
    }
}
