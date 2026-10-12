package com.drivingSchool.app;

import java.util.Scanner;

import com.drivingSchool.bot.ConsoleAPI;


class Main {
    private static String introduction = "Здравствуйте! Я — консольный бот автошколы.\n" +
			                             "Я помогу вам записаться на курс, узнать расписание и посмотреть статистику.\n" +
			                             "Введите `/start`, чтобы начать работать со мной.";

    public static void main(String[] args) {
    	System.out.println(introduction);
    	Scanner scanner = new Scanner(System.in);
		String request;

		do {
			System.out.print("> ");
			request = scanner.nextLine().trim();
		} while (!request.equals("/start"));

		
		if (!ConsoleAPI.start(scanner)) {
			System.out.println("!!! БОТ АВАРИЙНО ЗАВЕРШИЛ РАБОТУ !!!");
		}
	}
}