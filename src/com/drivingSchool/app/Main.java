package com.drivingSchool.app;

import com.drivingSchool.app.core.PropertyConfigLoader;
import com.drivingSchool.app.bot.console.ConsoleBot;
import com.drivingSchool.app.core.Config;


class Main
{
    static void main(String[] args) {
    	PropertyConfigLoader config = new PropertyConfigLoader("src/com/drivingSchool/config.properties");
		Config currentConfig = config.getConfig();
    	
    	switch (currentConfig.api())
    	{
    	    case "console" -> {
    	    	if (!ConsoleBot.run())
    	    	    System.out.println("!!! БОТ АВАРИЙНО ЗАВЕРШИЛ РАБОТУ !!!");
    	    }
    	    case "telegram" -> {
    	    	// Вызов логики телеграма
    	    }
    	    default -> {
    	    	// Логика NoOperations
				throw new RuntimeException("Такой тип интерфейса не поддерживается! (Укажите другой config)");
    	    }
    	}
    	System.out.print("Бот отработал в штатном режиме.");
    }
}