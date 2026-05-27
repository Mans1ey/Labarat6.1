package com.example.labarat5;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TextBotApplication {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("🚀 ЗАПУСК ПРИЛОЖЕНИЯ");
        System.out.println("=========================================");

        // Проверка 1: Существует ли BotConfig?
        System.out.println("📋 Шаг 1: Проверка конфигурации...");
        try {
            System.out.println("   Токен бота: " + maskToken(BotConfig.BOT_TOKEN));
            System.out.println("   Имя бота: @" + BotConfig.BOT_USERNAME);
        } catch (Exception e) {
            System.err.println("❌ ОШИБКА: Не удалось прочитать BotConfig!");
            System.err.println("   Проверьте, что файл BotConfig.java существует");
            System.err.println("   и содержит поля BOT_TOKEN и BOT_USERNAME");
            e.printStackTrace();
            return;
        }

        System.out.println("📋 Шаг 2: Создание экземпляра бота...");
        TextProcessorBot bot = null;
        try {
            bot = new TextProcessorBot();
            System.out.println("   ✅ Бот создан успешно");
        } catch (Exception e) {
            System.err.println("❌ ОШИБКА: Не удалось создать бота!");
            System.err.println("   Проверьте класс TextProcessorBot");
            e.printStackTrace();
            return;
        }

        System.out.println("📋 Шаг 3: Создание LongPolling приложения...");
        TelegramBotsLongPollingApplication botsApplication = null;
        try {
            botsApplication = new TelegramBotsLongPollingApplication();
            System.out.println("   ✅ Приложение создано");
        } catch (Exception e) {
            System.err.println("❌ ОШИБКА: Не удалось создать LongPolling приложение!");
            System.err.println("   Возможно, проблема с библиотеками Telegram");
            e.printStackTrace();
            return;
        }

        System.out.println("📋 Шаг 4: Регистрация бота в Telegram...");
        try {
            botsApplication.registerBot(BotConfig.BOT_TOKEN, bot);
            System.out.println("   ✅ Бот зарегистрирован успешно");
        } catch (TelegramApiException e) {
            System.err.println("❌ ОШИБКА Telegram API: " + e.getMessage());
            System.err.println("   Возможные причины:");
            System.err.println("   1. Неправильный токен бота");
            System.err.println("   2. Бот уже запущен в другом месте");
            System.err.println("   3. Проблемы с интернет-соединением");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.err.println("❌ НЕИЗВЕСТНАЯ ОШИБКА: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("=========================================");
        System.out.println("✅ БОТ УСПЕШНО ЗАПУЩЕН!");
        System.out.println("🤖 Имя бота: @" + BotConfig.BOT_USERNAME);
        System.out.println("📊 Статус: Ожидание сообщений...");
        System.out.println("💡 Отправьте сообщение боту в Telegram!");
        System.out.println("🛑 Для остановки нажмите Ctrl+C");
        System.out.println("=========================================");

        // Бесконечное ожидание
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n=========================================");
            System.out.println("👋 Бот остановлен пользователем");
            System.out.println("=========================================");
        }
    }

    // Вспомогательный метод для маскирования токена (показывает только первые 10 символов)
    private static String maskToken(String token) {
        if (token == null || token.isEmpty()) {
            return "❌ ТОКЕН НЕ ЗАДАН!";
        }
        if (token.length() <= 10) {
            return token + " (⚠️ подозрительно короткий токен)";
        }
        return token.substring(0, 10) + "... [скрыто]";
    }
}