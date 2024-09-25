package com.saltpgp.notionproxy.util;

public class StringUtils {

    public static String normalizeSwedishAlphabet(String input) {
        return input
                .replaceAll("Ã¥", "å")
                .replaceAll("Ã\u0085", "Å")
                .replaceAll("Ã¤", "ä")
                .replaceAll("Ã\u0084", "Ä")
                .replaceAll("Ã¶", "ö")
                .replaceAll("Ã\u0096", "Ö");
    }
}