package com.drivingSchool.app.bot.console.handlers;

import java.util.Map;
import java.util.Scanner;
import java.util.function.BooleanSupplier;

import com.drivingSchool.app.forms.EnrollmentForm;
import com.drivingSchool.app.models.Student;
import com.drivingSchool.app.storage.StudentRepository;


public class ConsoleBotCommandHandler implements BotCommandHandler {
	private final StudentRepository repository;
	private final Scanner scanner;

    public ConsoleBotCommandHandler(StudentRepository repository, Scanner scanner) {
        this.repository = repository;
		this.scanner = scanner;
    }
	
	public final Map<String, BooleanSupplier> commands = Map.<String, BooleanSupplier>of(
			"/exit", this::getExit,
			"/aboutMe", this::getAboutMe,
			"/help", this::getHelp,
			"/schedule", this::getSchedule,
			"/completeForm", this::getCompleteForm,
			"/stats", this::getStats
	);
	
	@Override
	public BooleanSupplier handler(String commandName) {
	    return commands.getOrDefault(commandName, this::getPassCommand);
	}
	
	
	@Override
	public boolean getStart() {
		System.out.println("Бот успешно запущен!");
		BooleanSupplier currentCommand;
        boolean isRunning = true;

        do {
            System.out.print("> ");
            String request = scanner.nextLine().trim();

            currentCommand = this.handler(request);
            isRunning = currentCommand.getAsBoolean();
        } while (isRunning);
	    return true;
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
			"/stats         — получить статистику\n" +
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

	@Override
	public boolean getStats() {
		if (repository.isEmpty()) {
			System.out.println("Пока никто не записан. Воспользуйтесь /completeForm, чтобы записаться на курс.");
			return true;
		}

		int total = repository.count();
		int sumAge = 0;
		int withCert = 0;

		for (Student student : repository.getAll()) {
			sumAge += student.getAge();
			if (student.hasMedicalCert()) {
				withCert++;
			}
		}

		double averageAge = (double) sumAge / total;

		System.out.println("Статистика по курсантам:");
		System.out.printf("Всего записано: %d%n", total);
		System.out.printf("Средний возраст: %.1f лет%n", averageAge);
		System.out.printf("Со справкой от медкомиссии: %d из %d%n", withCert, total);

		return true;
	}
}
