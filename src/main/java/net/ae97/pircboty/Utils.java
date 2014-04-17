package net.ae97.pircboty;

import com.google.common.base.CharMatcher;
import java.util.ArrayList;
import java.util.List;
import net.ae97.pircboty.hooks.Event;

public final class Utils {

    private Utils() {
    }

    protected static void dispatchEvent(PircBotY bot, Event<PircBotY> event) {
        bot.getConfiguration().getListenerManager().dispatchEvent(event);
    }

    public static int tryParseInt(String intString, int defaultValue) {
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long tryParseLong(String longString, int defaultValue) {
        try {
            return Long.parseLong(longString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static <V> V tryGetIndex(List<V> list, int index, V defaultValue) {
        if (index < list.size()) {
            return list.get(index);
        } else {
            return defaultValue;
        }
    }

    public static void sendRawLineToServer(PircBotY bot, String rawLine) {
        bot.sendRawLineToServer(rawLine);
    }

    public static List<String> tokenizeLine(String input) {
        List<String> stringParts = new ArrayList<String>();
        if (input == null || input.length() == 0) {
            return stringParts;
        }
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(input);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':') {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }
}
