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
                    // Загружаем bot.properties прямо здесь
                    java.util.Properties props = new java.util.Properties();
                    try (java.io.FileInputStream fis = new java.io.FileInputStream("bot.properties")) {
                        props.load(fis);
                    } catch (Exception e) {
                        System.out.println("Ошибка загрузки bot.properties");
                    }

                    String token = props.getProperty("bot.token");
                    long adminId = Long.parseLong(props.getProperty("bot.admin_id"));

                    org.telegram.telegrambots.meta.TelegramBotsApi api = new org.telegram.telegrambots.meta.TelegramBotsApi(org.telegram.telegrambots.updatesreceivers.DefaultBotSession.class);
                    
                    // Передаем токен и ID в конструктор
                    TelegramBot bot = new TelegramBot(token, adminId);
                    api.registerBot(bot);
                    System.out.println("Telegram бот успешно запущен!");
                    
                    Thread.currentThread().join(); 
                } catch (Exception e) {
                    System.out.println("Ошибка запуска ТГ: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> {
                throw new RuntimeException("Такой тип интерфейса не поддерживается! (Укажите другой config)");
            }
        }
    }
}