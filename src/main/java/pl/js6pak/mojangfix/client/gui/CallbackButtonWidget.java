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

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

public class CallbackButtonWidget extends ButtonWidget {
    private final Consumer<CallbackButtonWidget> onPress;

    public CallbackButtonWidget(int x, int y, String label, Consumer<CallbackButtonWidget> onPress) {
        this(x, y, 200, 20, label, onPress);
    }

    public CallbackButtonWidget(int x, int y, int width, int height, String label, Consumer<CallbackButtonWidget> onPress) {
        super(-1, x, y, width, height, label);
        this.onPress = onPress;
    }

    public void onPress() {
        this.onPress.accept(this);
    }
}
