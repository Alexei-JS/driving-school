package com.drivingSchool.API;

import java.util.Scanner;
import java.util.function.BooleanSupplier;

import com.drivingSchool.UsersInfo.BotCommandHandler;
import com.drivingSchool.UsersInfo.BotCommandHandlerImpl;


public class ConsoleAPI
{
	public static final BotCommandHandler botCommandHandler = new BotCommandHandlerImpl();
	
	public static boolean start(Scanner scanner) {
		System.out.println("Бот успешно запущен!");
	    BooleanSupplier currentCommand;
		boolean isRunning = true;
		
		do {
			System.out.print("> ");
			String request = scanner.nextLine().trim();
            System.out.print(request);
            
            currentCommand = botCommandHandler.handler(request);
            isRunning = currentCommand.getAsBoolean();
		} while (isRunning);
		
		scanner.close();
		return true;
	}
}
