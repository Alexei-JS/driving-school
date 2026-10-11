package com.drivingSchool.UsersInfo;

import java.util.Scanner;


public interface CommandHandler {
	boolean exit(Scanner scanner);
	void getBotInfo();
	void completeForm();
    void getAboutMe();
    void getSchedule();
}
