package com.drivingSchool.app.forms;

import com.drivingSchool.app.models.Student;
import com.drivingSchool.app.storage.StudentRepository;

public class EnrollmentForm {

    public static boolean saveStudent(String fullName, String ageStr, String category, String hasCertStr, StudentRepository repository) {
        System.out.println("--- Запись на курс ---");

        if (fullName == null || fullName.trim().isEmpty() || fullName.matches(".*\\d.*")) {
            System.out.println("Ошибка: ФИО не должно быть пустым или содержать цифры.");
            return false;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
            if (age < 16 || age > 100) {
                System.out.println("Ошибка: Возраст должен быть от 16 до 100 лет.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Возраст должен быть числом.");
            return false;
        }

        String cat = category.trim().toUpperCase();
        if (!cat.equals("A") && !cat.equals("B") && !cat.equals("C")) {
            System.out.println("Ошибка: Категория должна быть A, B или C.");
            return false;
        }

        boolean hasCert;
        if (hasCertStr.trim().equalsIgnoreCase("да")) {
            hasCert = true;
        } else if (hasCertStr.trim().equalsIgnoreCase("нет")) {
            hasCert = false;
        } else {
            System.out.println("Ошибка: Ответ про справку должен быть 'да' или 'нет'.");
            return false;
        }

        Student student = new Student(fullName.trim(), age, cat, hasCert);
        repository.add(student);
        System.out.println("Курсант успешно записан: " + student);
        return true;
    }
}