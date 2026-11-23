package com.drivingSchool.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "slot", nullable = false, unique = true)
    private String slot;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    protected Booking() {
    }

    public Booking(long chatId, String slot) {
        this.chatId = chatId;
        this.slot = slot;
        this.createdAt = System.currentTimeMillis();
    }

    public int getBookingId() {
        return this.bookingId;
    }

    public long getChatId() {
        return this.chatId;
    }

    public String getSlot() {
        return this.slot;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }
}