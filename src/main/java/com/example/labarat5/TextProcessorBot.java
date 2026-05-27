package com.example.labarat5;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class TextProcessorBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public TextProcessorBot() {
        System.out.println("   🔧 Конструктор TextProcessorBot вызван");

        // Настройка клиента с таймаутами (для стабильной работы)
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        this.telegramClient = new OkHttpTelegramClient(okHttpClient, BotConfig.BOT_TOKEN);
        System.out.println("   ✅ TelegramClient инициализирован");
    }

    @Override
    public void consume(Update update) {
        System.out.println("\n📨 ПОЛУЧЕНО НОВОЕ СООБЩЕНИЕ!");

        try {
            // Проверяем, есть ли сообщение и текст
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                System.out.println("   ⚠️ Сообщение не содержит текст");
                return;
            }

            // Получаем данные из сообщения
            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            System.out.println("   👤 Пользователь: " + userName);
            System.out.println("   💬 Текст: \"" + userMessage + "\"");

            // Формируем ответ в зависимости от команды
            String answerText;

            if (userMessage.equals("/start")) {
                answerText = "Привет, " + userName + "! 👋\n\n" +
                        "Я бот с шифром Цезаря!\n" +
                        "Просто отправь мне любой текст, и я зашифрую его (сдвиг +3).\n\n" +
                        "📝 Поддерживаются:\n" +
                        "• Английские буквы (A-Z, a-z)\n" +
                        "• Русские буквы (А-Я, а-я)\n" +
                        "• Цифры и знаки препинания (остаются без изменений)\n\n" +
                        "Примеры:\n" +
                        "'Hello' → 'Khoor'\n" +
                        "'Привет' → 'Тулзгу'\n\n" +
                        "Используй /help для справки";
                System.out.println("   📝 Обработка команды /start");
            }
            else if (userMessage.equals("/help")) {
                answerText = "📖 Как пользоваться ботом:\n\n" +
                        "1. Отправь любой текст (на русском или английском)\n" +
                        "2. Бот зашифрует его шифром Цезаря со сдвигом 3\n" +
                        "3. Буквы меняются, остальные символы остаются без изменений\n\n" +
                        "🔐 О шифре:\n" +
                        "Каждая буква заменяется на букву через 3 позиции в алфавите.\n\n" +
                        "Примеры:\n" +
                        "А → Г, Б → Д, В → Е, ...\n" +
                        "A → D, B → E, C → F, ...\n\n" +
                        "Команды:\n" +
                        "/start - Начать\n" +
                        "/help - Помощь\n" +
                        "/about - О шифре Цезаря";
                System.out.println("   📝 Обработка команды /help");
            }
            else if (userMessage.equals("/about")) {
                answerText = "🔐 О шифре Цезаря:\n\n" +
                        "Это один из древнейших шифров, названный в честь Юлия Цезаря.\n\n" +
                        "Принцип работы: каждая буква в тексте заменяется на букву,\n" +
                        "находящуюся на фиксированном расстоянии (сдвиге).\n\n" +
                        "В моём случае сдвиг = +3.\n\n" +
                        "Английский алфавит (26 букв):\n" +
                        "A→D, B→E, C→F, ..., X→A, Y→B, Z→C\n\n" +
                        "Русский алфавит (33 буквы):\n" +
                        "А→Г, Б→Д, В→Е, ..., Ю→А, Я→Б";
                System.out.println("   📝 Обработка команды /about");
            }
            else {
                // Шифруем текст
                String encrypted = caesarCipher(userMessage);
                answerText = "🔐 Зашифрованный текст:\n\n" +
                        "«" + encrypted + "»\n\n" +
                        "📥 Исходный текст:\n" +
                        "«" + userMessage + "»";
                System.out.println("   🔐 Зашифровано: \"" + encrypted + "\"");
            }

            // Отправляем ответ
            SendMessage reply = SendMessage.builder()
                    .chatId(chatId)
                    .text(answerText)
                    .build();

            telegramClient.execute(reply);
            System.out.println("   ✅ Ответ отправлен пользователю " + userName);

        } catch (TelegramApiException e) {
            System.err.println("   ❌ Ошибка Telegram API: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("   ❌ Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Шифр Цезаря со сдвигом +3
     * Поддерживает английские и русские буквы
     */
    private String caesarCipher(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            // === АНГЛИЙСКИЕ БУКВЫ ===
            // Заглавные (A-Z)
            if (c >= 'A' && c <= 'Z') {
                char encrypted = (char) ((c - 'A' + 3) % 26 + 'A');
                result.append(encrypted);
            }
            // Строчные (a-z)
            else if (c >= 'a' && c <= 'z') {
                char encrypted = (char) ((c - 'a' + 3) % 26 + 'a');
                result.append(encrypted);
            }

            // === РУССКИЕ БУКВЫ ===
            // Заглавные (А-Я) - 33 буквы
            else if (c >= 'А' && c <= 'Я') {
                char encrypted = (char) ((c - 'А' + 3) % 33 + 'А');
                result.append(encrypted);
            }
            // Строчные (а-я) - 33 буквы
            else if (c >= 'а' && c <= 'я') {
                char encrypted = (char) ((c - 'а' + 3) % 33 + 'а');
                result.append(encrypted);
            }

            // === БУКВА Ё (отдельный случай) ===
            else if (c == 'Ё') {
                // Ё → Й (сдвиг на 3: Ё=33-я, Й=36-я, но в 33 буквах циклично: Ё→Й)
                result.append('Й');
            }
            else if (c == 'ё') {
                result.append('й');
            }

            // === ОСТАЛЬНЫЕ СИМВОЛЫ ===
            // Цифры, пробелы, знаки препинания остаются без изменений
            else {
                result.append(c);
            }
        }

        return result.toString();
    }
}