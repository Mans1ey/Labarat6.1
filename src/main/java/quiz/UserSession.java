package quiz;

import java.util.Map;  // ← ДОБАВЬТЕ ЭТУ СТРОКУ
import java.util.HashMap;

/**
 * Связывает пользователя с игрой и хранит статистику
 */
public class UserSession {
    private final long userId;
    private final String userName;
    private QuizGame currentGame;
    private int totalCorrect;      // всего правильных ответов за все игры
    private int totalQuestions;    // всего отвеченных вопросов за все игры
    private boolean inQuiz;        // находится ли пользователь в игре

    public UserSession(long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.totalCorrect = 0;
        this.totalQuestions = 0;
        this.inQuiz = false;
    }

    /**
     * Начать новую игру
     */
    public void startNewGame(Map<String, String> landmarkToCountry) {
        this.currentGame = new QuizGame(landmarkToCountry);
        this.inQuiz = true;
    }

    /**
     * Завершить текущую игру и сохранить статистику
     */
    public void finishGame() {
        if (currentGame != null && inQuiz) {
            totalCorrect += currentGame.getScore();
            totalQuestions += currentGame.getTotalAsked();
            currentGame = null;
            inQuiz = false;
        }
    }

    /**
     * Получить следующий вопрос
     */
    public String getNextQuestion() {
        if (currentGame == null || !inQuiz) {
            return null;
        }
        return currentGame.getNextQuestion();
    }

    /**
     * Проверить ответ
     */
    public QuizGame.CheckResult checkAnswer(String answer) {
        if (currentGame == null || !inQuiz) {
            return null;
        }
        QuizGame.CheckResult result = currentGame.checkAnswer(answer);

        // Если игра закончилась, автоматически завершаем
        if (currentGame.isGameFinished()) {
            finishGame();
        }

        return result;
    }

    /**
     * Получить статистику сессии
     */
    public String getSessionStats() {
        if (currentGame != null && inQuiz) {
            return currentGame.getGameStats();
        } else {
            if (totalQuestions == 0) {
                return "📊 Статистика: пока нет сыгранных игр. Напишите /quiz чтобы начать!";
            }
            return "📊 Общая статистика: " + totalCorrect + " из " + totalQuestions +
                    " правильных ответов (" + (totalCorrect * 100 / totalQuestions) + "%)";
        }
    }

    public boolean isInQuiz() { return inQuiz; }
    public long getUserId() { return userId; }
    public String getUserName() { return userName; }
}