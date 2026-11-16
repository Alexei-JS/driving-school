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

        // Выбираем API
        switch (this.config.api()) {
            case "console" -> this.runConsole(repository);
            case "telegram" -> this.runTelegram(repository);
            default -> throw new UnsupportedOperationException("Проблема в config.properties, неверный api");
        }
    }

    private void runConsole(StudentRepository repository) {
        if (!ConsoleBot.run(repository)) {
            System.out.println("!!! БОТ АВАРИЙНО ЗАВЕРШИЛ РАБОТУ !!!");
        }
    }

    private void runTelegram(StudentRepository repository) {
        String token = this.config.botToken();
        long adminId = this.config.adminId();

        if (token == null || token.isBlank() || adminId == 0L) {
            System.out.println("Telegram-режим недоступен: в bot.properties не заданы bot.token и bot.admin_id.");
            return;
        }

        try {
            org.telegram.telegrambots.meta.TelegramBotsApi api = new org.telegram.telegrambots.meta.TelegramBotsApi(
                    org.telegram.telegrambots.updatesreceivers.DefaultBotSession.class);

            TelegramBot bot = new TelegramBot(token, adminId, repository);
            api.registerBot(bot);
            System.out.println("Telegram бот успешно запущен!");

            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("Ошибка запуска ТГ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private StudentRepository createRepository() {
        switch (this.config.saveMode()) {
            case "inMemory" -> {
                return new InMemoryStudentRepository();
            }
            case "inDataBase" -> {
                HibernateConnection hibernateConnection = new HibernateConnection();
                return new DataBaseStudents(hibernateConnection.getSessionFactory());
            }
            default -> throw new UnsupportedOperationException("Проблема в config.properties, неверный saveMode");
        }
    }
}