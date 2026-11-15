package com.drivingSchool.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "Students")
public class Student {
    @Id @Column(name = "student_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Для AUTO_INCREMENT
    private int studentId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "age")
    private int age;

    @Column(name = "category")
    private String category;

    @Column(name = "has_medical_cert")
    private boolean hasMedicalCert;

    protected Student() {
        // Пустой конструктор для Hibernate (изменение через рефлексию)
    }

    public Student(String fullName, int age, String category, boolean hasMedicalCert) {
        // Для заполнения поля studentId нужно вызвать save() в репозиторий.
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