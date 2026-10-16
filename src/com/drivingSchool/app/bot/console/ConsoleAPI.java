package com.drivingSchool.app.bot.console;

import java.util.Scanner;
import java.util.function.BooleanSupplier;

import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.bot.console.handlers.BotCommandHandlerImpl;
import com.drivingSchool.app.bot.console.handlers.BotCommandHandler;

public class ConsoleAPI
{
    private static String introduction = "Здравствуйте! Я — консольный бот автошколы.\n" +
            "Я помогу вам записаться на курс, узнать расписание и посмотреть статистику.\n" +
            "Введите `/start`, чтобы начать работать со мной.";
    
    private static final StudentRepository repository = new InMemoryStudentRepository();

    
    public static boolean run() {
    	Scanner scanner = new Scanner(System.in);
    	System.out.println(introduction);
		String request;

		do {
			System.out.print("> ");
			request = scanner.nextLine().trim();
		} while (!request.equals("/start"));
    	
        BotCommandHandler botCommandHandler = new BotCommandHandlerImpl(repository, scanner);
        botCommandHandler.getStart();

        scanner.close();
        return true;
    }
}