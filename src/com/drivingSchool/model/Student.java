package com.drivingSchool.model;

public class Student {

    private String fullName;
    private int age;
    private String category;
    private boolean hasMedicalCert;

    public Student(String fullName, int age, String category, boolean hasMedicalCert) {
        this.fullName = fullName;
        this.age = age;
        this.category = category;
        this.hasMedicalCert = hasMedicalCert;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public String getCategory() {
        return category;
    }

    public boolean hasMedicalCert() {
        return hasMedicalCert;
    }

    public String toString() {
        return String.format("%s, %d лет, категория %s, справка: %s",
                fullName, age, category, hasMedicalCert ? "есть" : "нет");
    }
}