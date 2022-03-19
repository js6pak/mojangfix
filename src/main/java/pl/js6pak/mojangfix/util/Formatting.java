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

package pl.js6pak.mojangfix.util;

import java.util.Locale;
import java.util.regex.Pattern;

public enum Formatting {
    BLACK("BLACK", '0', 0, 0x0),
    DARK_BLUE("DARK_BLUE", '1', 1, 0xaa),
    DARK_GREEN("DARK_GREEN", '2', 2, 0xaa00),
    DARK_AQUA("DARK_AQUA", '3', 3, 0xaaaa),
    DARK_RED("DARK_RED", '4', 4, 0xaa0000),
    DARK_PURPLE("DARK_PURPLE", '5', 5, 0xaa00aa),
    GOLD("GOLD", '6', 6, 0xffaa00),
    GRAY("GRAY", '7', 7, 0xaaaaaa),
    DARK_GRAY("DARK_GRAY", '8', 8, 0x555555),
    BLUE("BLUE", '9', 9, 0x5555ff),
    GREEN("GREEN", 'a', 10, 0x55ff55),
    AQUA("AQUA", 'b', 11, 0x55ffff),
    RED("RED", 'c', 12, 0xff5555),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 0xff55ff),
    YELLOW("YELLOW", 'e', 14, 0xffff55),
    WHITE("WHITE", 'f', 15, 0xffffff),

    OBFUSCATED("OBFUSCATED", 'k', true),
    RESET("RESET", 'r', -1, null);

    public static final char FORMATTING_CODE_PREFIX = '\u00a7';
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)" + FORMATTING_CODE_PREFIX + "[0-9A-FKR]");

    private final String name;
    private final char code;
    private final boolean modifier;
    private final String stringValue;
    private final int colorIndex;
    private final Integer colorValue;

    Formatting(String name, char code, int colorIndex, Integer colorValue) {
        this(name, code, false, colorIndex, colorValue);
    }

    Formatting(String name, char code, boolean modifier) {
        this(name, code, modifier, -1, null);
    }

    Formatting(String name, char code, boolean modifier, int colorIndex, Integer colorValue) {
        this.name = name;
        this.code = code;
        this.modifier = modifier;
        this.colorIndex = colorIndex;
        this.colorValue = colorValue;
        this.stringValue = FORMATTING_CODE_PREFIX + Character.toString(code);
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public boolean isModifier() {
        return this.modifier;
    }

    public boolean isColor() {
        return !this.modifier && this != RESET;
    }

    public Integer getColorValue() {
        return this.colorValue;
    }

    public boolean affectsGlyphWidth() {
        return !this.modifier;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.stringValue;
    }

    public static String strip(String string) {
        return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
    }
}