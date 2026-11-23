CREATE DATABASE IF NOT EXISTS DrivingSchool;
USE DrivingSchool;

CREATE TABLE IF NOT EXISTS Students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(60) NOT NULL,
    age INTEGER NOT NULL,
    category VARCHAR(3) NOT NULL,
    has_medical_cert BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS Bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    slot VARCHAR(60) NOT NULL,
    created_at BIGINT NOT NULL,
    UNIQUE KEY uq_bookings_slot (slot)
);

CREATE TABLE IF NOT EXISTS TicketResults (
    result_id INT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    ticket_number INT NOT NULL,
    correct_answers INT NOT NULL,
    total_questions INT NOT NULL,
    created_at BIGINT NOT NULL
);