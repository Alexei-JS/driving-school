package com.drivingSchool.app.tickets;

import java.util.List;

/* Экзаменационный билет: номер и вопросы в порядке датасета. */
public record Ticket(int number, List<Question> questions) {

    public int size() {
        return this.questions.size();
    }

    public Question questionAt(int index) {
        return this.questions.get(index);
    }
}