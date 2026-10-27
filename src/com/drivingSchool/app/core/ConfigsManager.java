package com.drivingSchool.app.core;

import com.drivingSchool.app.bot.console.ConsoleBot;
import com.drivingSchool.app.bot.telegram.TelegramBot;
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
                try {
                    org.telegram.telegrambots.meta.TelegramBotsApi api = new org.telegram.telegrambots.meta.TelegramBotsApi(org.telegram.telegrambots.updatesreceivers.DefaultBotSession.class);
                    TelegramBot bot = new TelegramBot();
                    api.registerBot(bot);
                    System.out.println("Telegram бот успешно запущен!");
                    
                    // ожидание ctrl+c для выключения
                    Thread.currentThread().join(); 
                } catch (Exception e) {
                    System.out.println("Ошибка запуска ТГ: " + e.getMessage());
                    e.printStackTrace(); // для логов
                }
            }
            default -> {
                throw new RuntimeException("Такой тип интерфейса не поддерживается! (Укажите другой config)");
            }
        }
    }
}