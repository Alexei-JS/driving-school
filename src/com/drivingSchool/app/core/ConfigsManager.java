package com.drivingSchool.app.core;

import com.drivingSchool.app.bot.console.ConsoleBot;
import com.drivingSchool.app.bot.telegram.TelegramBot;
import com.drivingSchool.app.core.confings.Config;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.storage.DataBaseStudents;
import com.drivingSchool.app.storage.InMemoryStudentRepository;


public class ConfigsManager {
    private final Config config;

    public ConfigsManager(Config config) {
        this.config = config;
    }

    public void run() {
        StudentRepository repository = this.createRepository();

        // Выбираем нужный API
        switch (this.config.api())
        {
            case "console" -> {
                if (!ConsoleBot.run(repository))
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
                throw new UnsupportedOperationException("Проблема в config.properties, неверный api");
            }
        }
    }

    private StudentRepository createRepository() {
        switch (this.config.saveMode())
        {
            case "inMemory" -> {
                return new InMemoryStudentRepository();
            }
            case "inDataBase" -> {
                HibernateConnection hibernateConnection = new HibernateConnection();
                return new DataBaseStudents(hibernateConnection.getSessionFactory());
            }
            default -> {
                throw new UnsupportedOperationException("Проблема в config.properties, неверный saveMode");
            }
        }
    }
}