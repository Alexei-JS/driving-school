package com.drivingSchool.bot;

import java.util.Scanner;
import java.util.function.BooleanSupplier;
import com.drivingSchool.storage.InMemoryStudentRepository;
import com.drivingSchool.storage.StudentRepository;

public class ConsoleAPI
{
    private static final StudentRepository repository = new InMemoryStudentRepository();
    public static final BotCommandHandler botCommandHandler = new BotCommandHandlerImpl(repository);

    public static boolean start(Scanner scanner) {
        System.out.println("Бот успешно запущен!");
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