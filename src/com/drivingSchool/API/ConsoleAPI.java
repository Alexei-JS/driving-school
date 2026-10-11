package com.drivingSchool.API;

import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.drivingSchool.UsersInfo.EnrollmentForm;
import com.drivingSchool.UsersInfo.Student;


public class ConsoleAPI
{
	public static boolean start(Scanner scanner) {
		System.out.println("Бот успешно запущен!");
		boolean isRunning = true;
		
		while (isRunning) {
			System.out.print("> ");
			String request = scanner.nextLine().trim();
            System.out.print(request);
			/* Тут проверка команд на существование и исполнение в предсказуемом варианте */
		}
		return true;
	}
}
