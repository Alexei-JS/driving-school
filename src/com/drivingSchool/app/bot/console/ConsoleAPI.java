package com.drivingSchool.app.bot.console;

import java.util.Scanner;
import java.util.function.BooleanSupplier;

import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.bot.console.handlers.BotCommandHandlerImpl;
import com.drivingSchool.app.bot.console.handlers.BotCommandHandler;

public class ConsoleAPI
{
    private static final StudentRepository repository = new InMemoryStudentRepository();

    public static boolean start(Scanner scanner) {
        System.out.println("Бот успешно запущен!");
        BotCommandHandler botCommandHandler = new BotCommandHandlerImpl(repository, scanner);

        BooleanSupplier currentCommand;
        boolean isRunning = true;

        do {
            System.out.print("> ");
            String request = scanner.nextLine().trim();

            currentCommand = botCommandHandler.handler(request);
            isRunning = currentCommand.getAsBoolean();
        } while (isRunning);

        scanner.close();
        return true;
    }
}