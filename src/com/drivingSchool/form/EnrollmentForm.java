package com.drivingSchool.form;

import java.util.Scanner;
import com.drivingSchool.model.Student;
import com.drivingSchool.storage.StudentRepository;

/**
 * Анкета курсанта. Опрашивает пользователя, валидирует ввод,
 * создаёт объект Student и добавляет его в переданный список.
 */
public class EnrollmentForm {

    public static boolean run(Scanner scanner, StudentRepository repository) {
        System.out.println("--- Запись на курс ---");

        String fullName = askFullName(scanner);
        int age = askAge(scanner);
        String category = askCategory(scanner);
        boolean hasMedicalCert = askMedicalCert(scanner);

        Student student = new Student(fullName, age, category, hasMedicalCert);
        repository.add(student);

        System.out.println("Курсант успешно записан:");
        System.out.println(student);
        return true;
    }

    private static String askFullName(Scanner scanner) {
        String fullName;
        while (true) {
            System.out.print("Введите ФИО: ");
            String input = scanner.nextLine().stripTrailing();
            if (!input.isEmpty()) {
                fullName = input;
                break;
            }
            System.out.println("ФИО не может быть пустым. Попробуйте снова.");
        }
        return fullName;
    }

    private static int askAge(Scanner scanner) {
        int age;
        while (true) {
            System.out.print("Введите возраст: ");
            String input = scanner.nextLine().stripTrailing();
            if (input.matches("[0-9]+")) {
                age = Integer.parseInt(input);
                break;
            }
            System.out.println("Это не число. Введите возраст как целое число лет.");
        }
        return age;
    }

    private static String askCategory(Scanner scanner) {
        String category;
        while (true) {
            System.out.print("Введите категорию (A/B/C): ");
            String input = scanner.nextLine().stripTrailing().toUpperCase();
            if (input.equals("A") || input.equals("B") || input.equals("C")) {
                category = input;
                break;
            }
            System.out.println("Допустимые категории: A, B, C. Попробуйте снова.");
        }
        return category;
    }

    private static boolean askMedicalCert(Scanner scanner) {
        System.out.print("Получена ли справка от медкомиссии? (да/нет): ");
        return scanner.nextLine().stripTrailing().equalsIgnoreCase("да");
    }
}