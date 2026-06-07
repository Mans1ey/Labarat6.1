package quiz;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Загрузчик данных о достопримечательностях
 */
public class DataLoader {

    /**
     * Загружает данные из файла
     * Формат файла: достопримечательность|страна
     * Пример: Эйфелева башня|Франция
     */
    public static Map<String, String> loadFromFile(String filePath) {
        Map<String, String> data = new HashMap<>();

        // Если файла нет, используем данные по умолчанию
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠️ Файл " + filePath + " не найден. Использую данные по умолчанию.");
            return getDefaultData();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length != 2) {
                    System.out.println("⚠️ Ошибка в строке " + lineNumber + ": " + line);
                    continue;
                }

                String landmark = parts[0].trim();
                String country = parts[1].trim();

                if (!landmark.isEmpty() && !country.isEmpty()) {
                    data.put(landmark, country);
                }
            }

            System.out.println("✅ Загружено " + data.size() + " достопримечательностей из файла");

        } catch (IOException e) {
            System.err.println("❌ Ошибка чтения файла: " + e.getMessage());
            return getDefaultData();
        }

        if (data.isEmpty()) {
            System.out.println("⚠️ Файл пуст или имеет неверный формат. Использую данные по умолчанию.");
            return getDefaultData();
        }

        return data;
    }

    /**
     * Данные по умолчанию (если файл не найден)
     */
    private static Map<String, String> getDefaultData() {
        Map<String, String> data = new HashMap<>();
        data.put("Эйфелева башня", "Франция");
        data.put("Статуя Свободы", "США");
        data.put("Колизей", "Италия");
        data.put("Тадж-Махал", "Индия");
        data.put("Великая Китайская стена", "Китай");
        data.put("Мачу-Пикчу", "Перу");
        data.put("Петра", "Иордания");
        data.put("Биг-Бен", "Великобритания");
        data.put("Пизанская башня", "Италия");
        data.put("Сиднейский оперный театр", "Австралия");
        data.put("Храм Неба", "Китай");
        data.put("Кремль", "Россия");
        data.put("Гиза (пирамиды)", "Египет");
        data.put("Статуя Христа-Искупителя", "Бразилия");
        data.put("Стоунхендж", "Великобритания");
        return data;
    }
}