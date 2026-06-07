package quiz;

import java.util.HashMap;
import java.util.Map;

/**
 * Управляет всеми сессиями пользователей
 */
public class SessionManager {
    private final Map<Long, UserSession> sessions = new HashMap<>();
    private final Map<String, String> landmarkToCountry;  // общие данные для всех игр

    public SessionManager(Map<String, String> landmarkToCountry) {
        this.landmarkToCountry = landmarkToCountry;
    }

    /**
     * Получить или создать сессию пользователя
     */
    public UserSession getOrCreateSession(long userId, String userName) {
        if (!sessions.containsKey(userId)) {
            sessions.put(userId, new UserSession(userId, userName));
        }
        return sessions.get(userId);
    }

    /**
     * Начать игру для пользователя
     */
    public String startQuiz(long userId, String userName) {
        UserSession session = getOrCreateSession(userId, userName);

        // Если уже в игре, предлагаем продолжить
        if (session.isInQuiz()) {
            return "🎮 Вы уже в игре! Отвечайте на текущий вопрос или напишите /endquiz чтобы закончить.";
        }

        session.startNewGame(landmarkToCountry);
        String firstQuestion = session.getNextQuestion();

        if (firstQuestion == null) {
            session.finishGame();
            return "❌ Извините, вопросы закончились!";
        }

        return "🎮 НАЧАЛО ВИКТОРИНЫ!\n\n" + firstQuestion;
    }

    /**
     * Обработать ответ пользователя
     */
    public String processAnswer(long userId, String userName, String answer) {
        UserSession session = getOrCreateSession(userId, userName);

        if (!session.isInQuiz()) {
            return "❌ Вы не в игре! Напишите /quiz чтобы начать викторину.";
        }

        QuizGame.CheckResult result = session.checkAnswer(answer);

        if (result == null) {
            return "❌ Ошибка в игре. Попробуйте /quiz заново.";
        }

        // Если игра закончилась, даем итоговое сообщение
        if (!session.isInQuiz()) {
            return result.getMessage() + "\n\n🎉 ИГРА ЗАКОНЧЕНА! 🎉\n" + session.getSessionStats();
        }

        // Иначе задаем следующий вопрос
        String nextQuestion = session.getNextQuestion();

        if (nextQuestion == null) {
            session.finishGame();
            return result.getMessage() + "\n\n🎉 ПОЗДРАВЛЯЮ! Вы ответили на все вопросы! 🎉\n" + session.getSessionStats();
        }

        return result.getMessage() + "\n\n🔜 Следующий вопрос:\n\n" + nextQuestion;
    }

    /**
     * Получить статистику пользователя
     */
    public String getStats(long userId, String userName) {
        UserSession session = getOrCreateSession(userId, userName);
        return session.getSessionStats();
    }

    /**
     * Завершить игру пользователя
     */
    public String endQuiz(long userId, String userName) {
        UserSession session = getOrCreateSession(userId, userName);

        if (!session.isInQuiz()) {
            return "❌ Вы не в игре!";
        }

        session.finishGame();
        return "🏁 Игра завершена!\n" + session.getSessionStats();
    }
}