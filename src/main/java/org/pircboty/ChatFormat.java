/**
 * Copyright (C) 2010-2013
 *
 * This file is part of PircBotY.
 *
 * PircBotY is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotY is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotY. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircboty;

/**
 * The Colors class provides several static fields and methods that you may find
 * useful when writing an IRC Bot.
 * <p>
 * This class contains constants that are useful for formatting lines sent to
 * IRC servers. These constants allow you to apply various formatting to the
 * lines, such as colours, boldness, underlining and reverse text.
 * <p>
 * The class contains static methods to remove colours and formatting from lines
 * of IRC text.
 * <p>
 * Here are some examples of how to use the constants
 * <pre>
 * message(Colors.BOLD + "A bold hello!");
 * <b>A bold hello!</b>
 * message(Colors.RED + "Red" + Colors.NORMAL + " text");
 * <font color="red">Red</font> text
 * message(Colors.BOLD + Colors.RED + "Bold and red");
 * <b><font color="red">Bold and red</font></b></pre>
 * <p/>
 * Please note that some IRC channels may be configured to reject any messages
 * that use colours. Also note that older IRC clients may be unable to correctly
 * display lines that contain colours and other control characters.
 * <p>
 * Note that this class name has been spelt in the American style in order to
 * remain consistent with the rest of the Java API.
 *
 *
 * @since PircBot 0.9.12
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a
 * href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>
 * Forked and Maintained by in <a
 * href="http://PircBotY.googlecode.com">PircBotY</a>
 */
public enum ChatFormat {

    /**
     * Removes all previously applied color and formatting attributes.
     */
    NORMAL("\u000f", Style.RESET),
    /**
     * Bold text.
     */
    BOLD("\u0002", Style.FORMAT),
    /**
     * Underlined text.
     */
    UNDERLINE("\u001f", Style.FORMAT),
    /**
     * Reversed text (may be rendered as italic text in some clients).
     */
    REVERSE("\u0016", Style.FORMAT),
    /**
     * White coloured text.
     */
    WHITE("\u000300", Style.COLOR),
    /**
     * Black coloured text.
     */
    BLACK("\u000301", Style.COLOR),
    /**
     * Dark blue coloured text.
     */
    DARK_BLUE("\u000302", Style.COLOR),
    /**
     * Dark green coloured text.
     */
    DARK_GREEN("\u000303", Style.COLOR),
    /**
     * Red coloured text.
     */
    RED("\u000304", Style.COLOR),
    /**
     * Brown coloured text.
     */
    BROWN("\u000305", Style.COLOR),
    /**
     * Purple coloured text.
     */
    PURPLE("\u000306", Style.COLOR),
    /**
     * Olive coloured text.
     */
    OLIVE("\u000307", Style.COLOR),
    /**
     * Yellow coloured text.
     */
    YELLOW("\u000308", Style.COLOR),
    /**
     * Green coloured text.
     */
    GREEN("\u000309", Style.COLOR),
    /**
     * Teal coloured text.
     */
    TEAL("\u000310", Style.COLOR),
    /**
     * Cyan coloured text.
     */
    CYAN("\u000311", Style.COLOR),
    /**
     * Blue coloured text.
     */
    BLUE("\u000312", Style.COLOR),
    /**
     * Magenta coloured text.
     */
    MAGENTA("\u000313", Style.COLOR),
    /**
     * Dark gray coloured text.
     */
    DARK_GRAY("\u000314", Style.COLOR),
    /**
     * Light gray coloured text.
     */
    LIGHT_GRAY("\u000315", Style.COLOR);
    private final String code;
    private final Style style;

    private ChatFormat(String c, Style s) {
        code = c;
        style = s;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * Removes all colours from a line of IRC text.
     *
     * @since PircBot 1.2.0
     *
     * @param line the input text.
     *
     * @return the same text, but with all colours removed.
     */
    public static String removeColors(String line) {
        for (ChatFormat color : ChatFormat.values()) {
            if (color.style == Style.COLOR) {
                line = line.replace(color.toString(), "");
            }
        }
        return line;
    }

    /**
     * Remove formatting from a line of IRC text.
     *
     * @since PircBot 1.2.0
     *
     * @param line the input text.
     *
     * @return the same text, but without any bold, underlining, reverse, etc.
     */
    public static String removeFormatting(String line) {
        for (ChatFormat color : ChatFormat.values()) {
            if (color.style == Style.FORMAT) {
                line = line.replace(color.toString(), "");
            }
        }
        return line;
    }

    /**
     * Removes all formatting and colours from a line of IRC text.
     *
     * @since PircBot 1.2.0
     *
     * @param line the input text.
     *
     * @return the same text, but without formatting and colour characters.
     *
     */
    public static String removeFormattingAndColors(String line) {
        return removeFormatting(removeColors(line));
    }

    private enum Style {

        FORMAT, COLOR, RESET;
    }
}
