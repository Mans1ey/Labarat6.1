package quiz;

import java.util.*;

/**
 * Логика одной викторины - ничего не знает о пользователях
 */
public class QuizGame {
    private final Map<String, String> landmarkToCountry;  // достопримечательность → страна
    private final Set<String> askedLandmarks;              // уже заданные вопросы
    private final List<String> allLandmarks;               // список всех достопримечательностей
    private String currentQuestion;                        // текущий вопрос
    private String currentAnswer;                          // правильный ответ на текущий вопрос
    private int score;                                      // счет в текущей сессии
    private int totalAsked;                                // всего задано вопросов

    public QuizGame(Map<String, String> landmarkToCountry) {
        this.landmarkToCountry = new HashMap<>(landmarkToCountry);
        this.askedLandmarks = new HashSet<>();
        this.allLandmarks = new ArrayList<>(landmarkToCountry.keySet());
        this.score = 0;
        this.totalAsked = 0;
    }

    /**
     * Получить следующий случайный вопрос (который еще не задавался)
     * @return текст вопроса или null, если вопросы закончились
     */
    public String getNextQuestion() {
        // Проверяем, остались ли еще не заданные вопросы
        if (askedLandmarks.size() >= allLandmarks.size()) {
            return null; // Все вопросы заданы
        }

        // Выбираем случайный вопрос из еще не заданных
        List<String> remaining = new ArrayList<>(allLandmarks);
        remaining.removeAll(askedLandmarks);

        Random random = new Random();
        currentQuestion = remaining.get(random.nextInt(remaining.size()));
        currentAnswer = landmarkToCountry.get(currentQuestion);

        askedLandmarks.add(currentQuestion);
        totalAsked++;

        return "🏛️ " + currentQuestion + "\n\nВ какой стране находится эта достопримечательность?";
    }

    /**
     * Проверить ответ пользователя
     * @param userAnswer ответ пользователя
     * @return результат проверки с сообщением
     */
    public CheckResult checkAnswer(String userAnswer) {
        if (currentQuestion == null) {
            return new CheckResult(false, "❌ Игра не начата. Напишите /quiz", 0, 0);
        }

        boolean isCorrect = userAnswer.equalsIgnoreCase(currentAnswer.trim());

        if (isCorrect) {
            score++;
            return new CheckResult(true,
                    "✅ Верно! " + currentQuestion + " находится в " + currentAnswer +
                            "! (" + score + "/" + totalAsked + ")",
                    score, totalAsked);
        } else {
            return new CheckResult(false,
                    "❌ Неверно! " + currentQuestion + " находится в " + currentAnswer +
                            ". (" + score + "/" + totalAsked + ")",
                    score, totalAsked);
        }
    }

    /**
     * Получить текущую статистику игры
     */
    public String getGameStats() {
        if (totalAsked == 0) {
            return "📊 Игра еще не началась. Напишите /quiz";
        }
        return "📊 Текущая статистика: " + score + " из " + totalAsked + " правильных ответов (" +
                (score * 100 / totalAsked) + "%)";
    }

    public boolean isGameFinished() {
        return askedLandmarks.size() >= allLandmarks.size();
    }

    public int getScore() { return score; }
    public int getTotalAsked() { return totalAsked; }

    /**
     * Класс для возврата результата проверки
     */
    public static class CheckResult {
        private final boolean correct;
        private final String message;
        private final int score;
        private final int total;

        public CheckResult(boolean correct, String message, int score, int total) {
            this.correct = correct;
            this.message = message;
            this.score = score;
            this.total = total;
        }

        public boolean isCorrect() { return correct; }
        public String getMessage() { return message; }
        public int getScore() { return score; }
        public int getTotal() { return total; }
    }
}