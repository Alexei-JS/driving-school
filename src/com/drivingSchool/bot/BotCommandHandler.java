package com.drivingSchool.bot;

import java.util.function.BooleanSupplier;

public interface BotCommandHandler {
	BooleanSupplier handler(String commandName); // Основная функция отвечающая за получение методов из класса
	
	boolean getExit();
	boolean getHelp();
	boolean getCompleteForm();
    boolean getAboutMe();
    boolean getSchedule();
    boolean getPassCommand(); // Функция заглушка, когда введена неверная команда
}
