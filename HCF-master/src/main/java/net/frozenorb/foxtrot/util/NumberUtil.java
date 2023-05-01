package net.frozenorb.foxtrot.util;

public class NumberUtil {
    public static String getOrdinal(Integer number) {
        char lastNumber = number.toString().charAt(number.toString().length()-1);

        if (lastNumber == 1 || number == 1) {
            return number + "st";
        }

        if (lastNumber == 2) {
            return number + "nd";
        }

        if (lastNumber == 3) {
            return number + "rd";
        }

        return number + "th";
    }

}
