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

package pl.js6pak.mojangfix.client.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Tessellator;
import pl.js6pak.mojangfix.mixinterface.KeyBindingAccessor;

import java.util.HashMap;
import java.util.Map;

public class ControlsListWidget extends EntryListWidget {
    private final Minecraft minecraft;
    private final GameOptions options;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class KeyBindingEntry {
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;
    }

    @Getter
    private final Map<KeyBinding, KeyBindingEntry> buttons = new HashMap<>();

    public ControlsListWidget(KeybindsScreen parent, Minecraft minecraft, GameOptions options) {
        super(minecraft, parent.width, parent.height, 36, parent.height - 36, 20);
        this.minecraft = minecraft;
        this.options = options;
    }

    @Override
    protected int getEntryCount() {
        return options.allKeys.length;
    }

    @Override
    protected void entryClicked(int i, boolean bl) {
    }

    @Override
    protected boolean isSelectedEntry(int i) {
        return false;
    }

    @Override
    protected void renderBackground() {
    }

    private int mouseX;
    private int mouseY;

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void renderEntry(int index, int x, int y, int l, Tessellator tessellator) {
        KeyBinding keyBinding = options.allKeys[index];
        KeyBindingEntry entry = buttons.get(keyBinding);

        minecraft.textRenderer.drawWithShadow(options.getKeybindName(index), x, y + 5, -1);

        ButtonWidget editButton = entry.getEditButton();
        editButton.x = x + 100;
        editButton.y = y;
        editButton.render(minecraft, mouseX, mouseY);

        ButtonWidget resetButton = entry.getResetButton();
        resetButton.x = editButton.x + 75;
        resetButton.y = editButton.y;
        resetButton.active = ((KeyBindingAccessor) keyBinding).getDefaultKeyCode() != keyBinding.code;
        resetButton.render(minecraft, mouseX, mouseY);
    }
}
