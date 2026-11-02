package com.drivingSchool.app.models;

public class Student {
    private static int studentId = 1;

    private String fullName;
    private int age;
    private String category;
    private boolean hasMedicalCert;

    public Student(String fullName, int age, String category, boolean hasMedicalCert) {
        this.studentId = Student.studentId++; // отсчет идет с 1.

        this.fullName = fullName;
        this.age = age;
        this.category = category;
        this.hasMedicalCert = hasMedicalCert;
    }

    public int getStudentId() { return this.studentId; }

    public String getFullName() {
        return this.fullName;
    }

    public int getAge() {
        return this.age;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean hasMedicalCert() {
        return this.hasMedicalCert;
    }

    public String toString() {
        return String.format("%s, %d лет, категория %s, справка: %s",
                fullName, age, category, hasMedicalCert ? "есть" : "нет");
    }
}