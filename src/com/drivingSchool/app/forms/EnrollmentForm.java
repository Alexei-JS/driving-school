package com.drivingSchool.app.forms;

import com.drivingSchool.app.models.Student;
import com.drivingSchool.app.storage.StudentRepository;

public class EnrollmentForm {
    public static String saveStudent(String fullName, String ageStr, String category, String hasCertStr, StudentRepository repository) {
        if (fullName == null || fullName.trim().isEmpty() || fullName.matches(".*\\d.*")) {
            return "❌ ФИО не должно быть пустым или содержать цифры.";
        }

        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
            if (age < 16 || age > 100) {
                return "❌ Возраст должен быть от 16 до 100 лет.";
            }
        } catch (NumberFormatException e) {
            return "❌ Возраст должен быть числом.";
        }

        String cat = category.trim().toUpperCase();
        if (!cat.equals("A") && !cat.equals("B") && !cat.equals("C")) {
            return "❌ Категория должна быть A, B или C.";
        }

        boolean hasCert;
        String certAnswer = hasCertStr.trim().toLowerCase();
        if (certAnswer.equals("да") || certAnswer.equals("yes") || certAnswer.equals("y")) {
            hasCert = true;
        } else if (certAnswer.equals("нет") || certAnswer.equals("no") || certAnswer.equals("n")) {
            hasCert = false;
        } else {
            return "❌ Ответ про справку должен быть 'да' или 'нет'.";
        }

        Student student = new Student(fullName.trim(), age, cat, hasCert);
        repository.add(student);
        System.out.println("Курсант успешно записан: " + student); // Лог для консолиsaveStudent
        return null;
    }
}