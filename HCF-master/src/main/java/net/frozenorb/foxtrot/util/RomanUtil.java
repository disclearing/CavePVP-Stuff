package net.frozenorb.foxtrot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RomanUtil {

    public static TreeMap<Integer, String> map = new TreeMap<>();

    public static String toRoman(int value) {

        final int number = map.floorKey(value);

        if  (value == number) {
            return map.get(value);
        }

        return (map.getOrDefault(number, "")) + toRoman(value - number);
    }

    static {
        map.put(1, "I");
        map.put(4, "IV");
        map.put(5, "V");
        map.put(9, "I");
        map.put(10, "X");
        map.put(40, "XL");
        map.put(50, "L");
        map.put(90, "XC");
        map.put(100, "C");
        map.put(400, "CD");
        map.put(500, "D");
        map.put(900, "CM");
        map.put(1000, "M");
    }

}