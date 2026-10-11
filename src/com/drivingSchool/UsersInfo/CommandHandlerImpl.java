package com.drivingSchool.UsersInfo;

import java.util.Scanner;


public class CommandHandlerImpl implements CommandHandler {
	public boolean exit(Scanner scanner) {
		//scanner.close();
		//System.out.println("Телеграмм бот завершил свою работу.\nДо скорых встреч!");
		return false;
	}
	
	public void getBotInfo() {
		//System.out.println("Доступные команды:\n" + "/help      — показать список команд\n" + "/start     — приветствие и краткая информация о боте\n" + 
		//		"/enroll    — записаться на курс (анкета курсанта)\n" + "/stats     — статистика по записанным курсантам\n" + "/schedule  — расписание занятий\n" + 
		//		"/deleteBot — завершить работу бота\n");
	}
	
	public void completeForm() {
		//
	}

	public void getAboutMe() {
        //
	}

	public void getSchedule() {
        //
	}
}
