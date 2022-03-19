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

import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;

public class MultiplayerServerListWidget extends EntryListWidget {
    private final MultiplayerScreen parent;

    public MultiplayerServerListWidget(MultiplayerScreen parent) {
        super(parent.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, 36);
        this.parent = parent;
    }

    @Override
    protected int getEntryCount() {
        return this.parent.getServersList().size();
    }

    @Override
    protected void entryClicked(int slot, boolean doubleClick) {
        this.parent.selectServer(slot, doubleClick);
    }

    @Override
    protected boolean isSelectedEntry(int i) {
        return i == this.parent.getServersList().indexOf(this.parent.getSelectedServer());
    }

    @Override
    protected int getEntriesHeight() {
        return this.parent.getServersList().size() * 36;
    }

    @Override
    protected void renderBackground() {
        this.parent.renderBackground();
    }

    @Override
    protected void renderEntry(int index, int x, int y, int l, Tessellator arg) {
        ServerData server = this.parent.getServersList().get(index);
        this.parent.drawStringWithShadow(this.parent.getFontRenderer(), server.getName(), x + 2, y + 1, 0xffffff);
        this.parent.drawStringWithShadow(this.parent.getFontRenderer(), server.getIp(), x + 2, y + 12, 0x808080);
    }
}
