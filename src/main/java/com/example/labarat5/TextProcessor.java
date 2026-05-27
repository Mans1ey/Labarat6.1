package com.example.labarat5;

public class TextProcessor {

    public static String caesarCipher(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                char encrypted = (char) ((c - 'A' + 3) % 26 + 'A');
                result.append(encrypted);
            }
            else if (c >= 'a' && c <= 'z') {
                char encrypted = (char) ((c - 'a' + 3) % 26 + 'a');
                result.append(encrypted);
            }
            else {
                result.append(c);
            }
        }

        return result.toString();
    }
}