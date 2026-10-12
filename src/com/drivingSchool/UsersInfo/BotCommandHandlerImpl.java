package com.drivingSchool.UsersInfo;

import java.util.Map;

import java.util.function.BooleanSupplier;


public class BotCommandHandlerImpl implements BotCommandHandler {
	public final Map<String, BooleanSupplier> commands = Map.<String, BooleanSupplier>of(
			"/exit", this::getExit,
			"/aboutMe", this::getAboutMe,
			"/help", this::getHelp,
			"/shedule", this::getSchedule,
			"/completeForm", this::getEnroll
	);
	
	@Override
	public BooleanSupplier handler(String commandName) {
	    return commands.getOrDefault(commandName, this::getPassCommand);
	}
	
	@Override
	public boolean getExit() {
	    //
		return false;
	}
	
	@Override
	public boolean getHelp() {
		System.out.println(
			"Доступные команды:\n" +
		    "/help      — показать список команд\n" +
		    "/start     — приветствие и краткая информация о боте\n" + 
		    "/enroll    — записаться на курс (анкета курсанта)\n" +
		    "/aboutMe     — статистика по записанным курсантам\n" +
		    "/schedule  — расписание занятий\n" + 
			"/exit — завершить работу боsта\n");
        return true;
	}
	
	@Override
	public boolean getEnroll() {
		//
        return true;
	}

	@Override
	public boolean getAboutMe() {
        //
        return true;
	}

	@Override
	public boolean getSchedule() {
        //
        return true;
	}
	
	@Override
	public boolean getPassCommand() {
		return true; // Функция заглушка, когда введена неверная команда
	}
}
