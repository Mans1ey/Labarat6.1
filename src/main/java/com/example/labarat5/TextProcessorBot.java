package com.example.labarat5;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import quiz.*;
import okhttp3.OkHttpClient;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TextProcessorBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final SessionManager sessionManager;
    private final CaesarCipher caesarCipher;

    public TextProcessorBot() {
        System.out.println("   🔧 Конструктор TextProcessorBot вызван");

        // Настройка клиента
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        this.telegramClient = new OkHttpTelegramClient(okHttpClient, BotConfig.BOT_TOKEN);

        // Загрузка данных для викторины
        String dataPath = "src/main/java/com/example/labarat5/data/landmarks.txt";
        Map<String, String> landmarkData = DataLoader.loadFromFile(dataPath);
        this.sessionManager = new SessionManager(landmarkData);
        this.caesarCipher = new CaesarCipher();

        System.out.println("   ✅ TelegramClient инициализирован");
        System.out.println("   ✅ Викторина загружена с " + landmarkData.size() + " вопросами");
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String userMessage = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();

        System.out.println("📨 Сообщение от " + userName + ": " + userMessage);

        String answerText = processCommand(chatId, userName, userMessage);

        if (answerText != null) {
            sendText(chatId, answerText);
        }
    }

    private String processCommand(long chatId, String userName, String message) {
        // Команды викторины
        if (message.equalsIgnoreCase("/quiz")) {
            return sessionManager.startQuiz(chatId, userName);
        }

        if (message.equalsIgnoreCase("/stats")) {
            return sessionManager.getStats(chatId, userName);
        }

        if (message.equalsIgnoreCase("/endquiz")) {
            return sessionManager.endQuiz(chatId, userName);
        }

        // Команды шифра Цезаря
        if (message.equals("/start")) {
            return getWelcomeMessage(userName);
        }

        if (message.equals("/help")) {
            return getHelpMessage();
        }

        if (message.equals("/about")) {
            return getAboutMessage();
        }

        // Если пользователь в викторине - обрабатываем как ответ на вопрос
        UserSession session = sessionManager.getOrCreateSession(chatId, userName);
        if (session.isInQuiz()) {
            return sessionManager.processAnswer(chatId, userName, message);
        }

        // Иначе шифруем текст шифром Цезаря
        String encrypted = caesarCipher.encrypt(message);
        return "🔐 Зашифрованный текст:\n\n«" + encrypted + "»";
    }

    private String getWelcomeMessage(String userName) {
        return "Привет, " + userName + "! 👋\n\n" +
                "Я бот с двумя режимами работы:\n\n" +
                "🔐 1. Шифр Цезаря\n" +
                "   Просто отправь любой текст, и я зашифрую его (сдвиг +3)\n\n" +
                "🎮 2. Викторина 'Угадай страну'\n" +
                "   /quiz - начать викторину\n" +
                "   /stats - показать статистику\n" +
                "   /endquiz - завершить игру\n\n" +
                "📖 /help - подробная справка";
    }

    private String getHelpMessage() {
        return "📖 Справка по командам:\n\n" +
                "🔐 ШИФР ЦЕЗАРЯ:\n" +
                "   Просто отправьте любой текст на русском или английском\n\n" +
                "🎮 ВИКТОРИНА:\n" +
                "   /quiz - начать новую викторину\n" +
                "   /stats - показать статистику ваших игр\n" +
                "   /endquiz - завершить текущую игру\n\n" +
                "ℹ️ ДРУГИЕ КОМАНДЫ:\n" +
                "   /start - приветствие\n" +
                "   /help - эта справка\n" +
                "   /about - о шифре Цезаря";
    }

    private String getAboutMessage() {
        return "🔐 Шифр Цезаря — один из древнейших шифров.\n\n" +
                "Каждая буква заменяется на букву через 3 позиции.\n\n" +
                "Примеры:\n" +
                "Hello → Khoor\n" +
                "Привет → Тулзгу\n\n" +
                "Буквы Ё и ё также поддерживаются!";
    }

    private void sendText(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка отправки: " + e.getMessage());
        }
    }
}