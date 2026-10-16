package com.drivingSchool.app;

import com.drivingSchool.app.core.ConfigLoader;
import com.drivingSchool.app.bot.console.ConsoleAPI;


class Main
{
    public static void main(String[] args) {
    	ConfigLoader config = new ConfigLoader("src/com/drivingSchool/config.properties");
    	
    	switch (config.getAPI())
    	{
    	    case "console" -> {
    	    	if (!ConsoleAPI.run())
    	    	    System.out.println("!!! БОТ АВАРИЙНО ЗАВЕРШИЛ РАБОТУ !!!");
    	    }
    	    case "telegram" -> {
    	    	// Вызов логики телеграма
    	    }
    	    default -> {
    	    	// Логика NoOperations
    	    }
    	}
    	System.out.println("Бот отработал в штатном режиме.");
    }
}