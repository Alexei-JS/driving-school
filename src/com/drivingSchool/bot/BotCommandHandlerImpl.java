package com.drivingSchool.bot;

import java.util.Map;
import java.util.Scanner;
import java.util.function.BooleanSupplier;

import com.drivingSchool.form.EnrollmentForm;
import com.drivingSchool.storage.StudentRepository;


public class BotCommandHandlerImpl implements BotCommandHandler {
	private final StudentRepository repository;
	private final Scanner scanner;

    public BotCommandHandlerImpl(StudentRepository repository, Scanner scanner) {
        this.repository = repository;
		this.scanner = scanner;
    }
	
	public final Map<String, BooleanSupplier> commands = Map.<String, BooleanSupplier>of(
			"/exit", this::getExit,
			"/aboutMe", this::getAboutMe,
			"/help", this::getHelp,
			"/schedule", this::getSchedule,
			"/completeForm", this::getCompleteForm
	);
	
	@Override
	public BooleanSupplier handler(String commandName) {
	    return commands.getOrDefault(commandName, this::getPassCommand);
	}
	
	@Override
	public boolean getExit() {
		System.out.println("Бот завершил свою работу. До скорых встреч!");
		return false;
	}
	
	@Override
	public boolean getHelp() {
		System.out.println("Доступные команды:\n" +
			"/help          — показать список команд\n" +
			"/aboutMe       — информация о боте\n" +
			"/completeForm  — записаться на курс (анкета курсанта)\n" +
			"/schedule      — расписание занятий\n" +
			"/exit          — завершить работу бота");
		return true;
	}
	
	@Override
	public boolean getCompleteForm() {
		return EnrollmentForm.run(scanner, repository);
	}

	@Override
	public boolean getAboutMe() {
		System.out.println("Здравствуйте! Я — консольный бот автошколы.\n" +
			"Я помогу вам записаться на курс, узнать расписание и посмотреть статистику.\n" +
			"Введите /help, чтобы увидеть список доступных команд.");
		return true;
	}

	@Override
	public boolean getSchedule() {
		System.out.println("Расписание занятий:\n" +
			"Теория:    понедельник, среда, пятница — 18:00–20:00\n" +
			"Практика:  вторник, четверг — по записи с инструктором\n" +
			"Экзамен:   суббота — 10:00 (по готовности группы)");
		return true;
	}
	
	@Override
	public boolean getPassCommand() {
		System.out.println("Неизвестная команда. Введите /help, чтобы увидеть список доступных команд.");
		return true;
	}
}
