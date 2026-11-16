package com.drivingSchool.app.tickets;

import java.util.List;

/**
 * Один вопрос экзаменационного билета.
 *
 * @param text         текст вопроса
 * @param options      варианты ответа в порядке показа
 * @param correctIndex индекс правильного варианта в {@code options} (с нуля)
 * @param explanation  пояснение к ответу (может быть пустым)
 * @param imageUrl     ссылка на картинку к вопросу или null, если картинки нет
 */
public record Question(String text, List<String> options, int correctIndex, String explanation, String imageUrl) {
}