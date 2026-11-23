package com.drivingSchool.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TicketResults")
public class TicketResult {
    @Id
    @Column(name = "result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resultId;

    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "ticket_number", nullable = false)
    private int ticketNumber;

    @Column(name = "correct_answers", nullable = false)
    private int correct;

    @Column(name = "total_questions", nullable = false)
    private int total;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    protected TicketResult() {
    }

    public TicketResult(long chatId, int ticketNumber, int correct, int total) {
        this.chatId = chatId;
        this.ticketNumber = ticketNumber;
        this.correct = correct;
        this.total = total;
        this.createdAt = System.currentTimeMillis();
    }

    public int getResultId() {
        return this.resultId;
    }

    public long getChatId() {
        return this.chatId;
    }

    public int getTicketNumber() {
        return this.ticketNumber;
    }

    public int getCorrect() {
        return this.correct;
    }

    public int getTotal() {
        return this.total;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }
}