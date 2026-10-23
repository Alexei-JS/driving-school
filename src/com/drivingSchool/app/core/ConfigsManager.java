package com.drivingSchool.app.core;

import com.drivingSchool.app.bot.console.ConsoleBot;
import com.drivingSchool.app.core.confings.Config;


public class ConfigsManager {
    private final Config config;

    public ConfigsManager(Config config) {
        this.config = config;
    }

    public void run() {
        switch (this.config.api())
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
    }
}