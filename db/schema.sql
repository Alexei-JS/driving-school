-- CREATE DATABASE IF NOT EXISTS DrivingSchool;
-- USE DrivingSchool;
DROP TABLE IF EXISTS Students;

CREATE TABLE IF NOT EXISTS Students (
    id INT PRIMARY KEY,
    fullName VARCHAR(60) NOT NULL,
    age INTEGER NOT NULL,
    category VARCHAR(3) NOT NULL,
    hasMedicalCert BOOLEAN NOT NULL
);
