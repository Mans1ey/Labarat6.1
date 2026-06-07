package com.example.labarat5;

public class CaesarCipher {

    public String encrypt(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            // Английские буквы
            if (c >= 'A' && c <= 'Z') {
                result.append((char) ((c - 'A' + 3) % 26 + 'A'));
            }
            else if (c >= 'a' && c <= 'z') {
                result.append((char) ((c - 'a' + 3) % 26 + 'a'));
            }
            // Русские буквы
            else if (c >= 'А' && c <= 'Я') {
                result.append((char) ((c - 'А' + 3) % 33 + 'А'));
            }
            else if (c >= 'а' && c <= 'я') {
                result.append((char) ((c - 'а' + 3) % 33 + 'а'));
            }
            // Буква Ё
            else if (c == 'Ё') {
                result.append('Й');
            }
            else if (c == 'ё') {
                result.append('й');
            }
            else {
                result.append(c);
            }
        }

        return result.toString();
    }
}