-- CREATE DATABASE IF NOT EXISTS DrivingSchool;
USE DrivingSchool;
DROP TABLE IF EXISTS Students;

CREATE TABLE IF NOT EXISTS Students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(60) NOT NULL,
    age INTEGER NOT NULL,
    category VARCHAR(3) NOT NULL,
    has_medical_cert BOOLEAN NOT NULL
);
