package com.drivingSchool.app.bot.console;

import java.util.Scanner;

import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.bot.BotCommandHandler;

public class ConsoleBot
{
    private static String introduction = "Здравствуйте! Я — консольный бот автошколы.\n" +
            "Я помогу вам записаться на курс, узнать расписание и посмотреть статистику.\n" +
            "Введите `/start`, чтобы начать работать со мной.";
    
    public static boolean run(StudentRepository repository) {
    	Scanner scanner = new Scanner(System.in, "Cp866");
    	System.out.println(introduction);
		String request;

		do {
			System.out.print("> ");
			request = scanner.nextLine().trim();
		} while (!request.equals("/start"));
    	
        BotCommandHandler botCommandHandler = new ConsoleBotCommandHandler(repository, scanner);
        botCommandHandler.getStart();

        scanner.close();
        return true;
    }
}