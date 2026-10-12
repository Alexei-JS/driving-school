package com.drivingSchool.UsersInfo;

import java.util.Map;

import java.util.function.BooleanSupplier;


public class BotCommandHandlerImpl implements BotCommandHandler {
	private final Map<String, BooleanSupplier> commands = Map.<String, BooleanSupplier>of(
			"/exit", this::getExit,
			"/aboutMe", this::getAboutMe,
			"/help", this::getHelp,
			"/shedule", this::getSchedule,
			"/completeForm", this::getCompleteForm
	);
	
	@Override
	public BooleanSupplier handler(String commandName) {
	    return commands.get(commandName);
	}
	
	@Override
	public boolean getExit() {
		System.out.println("Это метод exit");
	    //
		return false;
	}
	
	@Override
	public boolean getHelp() {
		System.out.println("Это метод help");
		//System.out.println("Доступные команды:\n" + "/help      — показать список команд\n" + "/start     — приветствие и краткая информация о боте\n" + 
		//		"/enroll    — записаться на курс (анкета курсанта)\n" + "/stats     — статистика по записанным курсантам\n" + "/schedule  — расписание занятий\n" + 
		//		"/deleteBot — завершить работу бота\n");
        return true;
	}
	
	@Override
	public boolean getCompleteForm() {
		System.out.println("Это метод completeForm");
		//
        return true;
	}

	@Override
	public boolean getAboutMe() {
		System.out.println("Это метод aboutMe");
        //
        return true;
	}

	@Override
	public boolean getSchedule() {
		System.out.println("Это метод shedule");
        //
        return true;
	}
	
	@Override
	public boolean getPassCommand() {
		return true; // Функция заглушка, когда введена неверная команда
	}
}
