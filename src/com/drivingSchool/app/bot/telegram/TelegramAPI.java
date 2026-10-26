package com.drivingSchool.app.bot.telegram;

import com.drivingSchool.app.bot.console.handlers.BotCommandHandler;
import com.drivingSchool.app.bot.console.handlers.ConsoleBotCommandHandler;
import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Scanner;

public class TelegramAPI extends TelegramLongPollingBot {

    private final StudentRepository repository = new InMemoryStudentRepository();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Создаем UI для отправки ответов в этот чат
            TelegramUI ui = new TelegramUI(this, chatId);
            
            // Мы используем консольный хэндлер, но передаем ему наш TelegramUI 
            // (чтобы он не писал в консоль, а отправлял в ТГ).
            // ВАЖНО: Конструктор ConsoleBotCommandHandler принимает Scanner. 
            // Передадим пустой сканер, так как анкета /completeForm в ТГ пока не работает.
            BotCommandHandler handler = new ConsoleBotCommandHandler(repository, new Scanner(System.in)) {
                @Override
                public boolean getHelp() {
                    ui.sendMessage("Доступные команды:\n/help\n/aboutMe\n/schedule\n/stats\n/exit");
                    return true;
                }
                @Override
                public boolean getAboutMe() {
                    ui.sendMessage("Здравствуйте! Я — бот автошколы.");
                    return true;
                }
                @Override
                public boolean getSchedule() {
                    ui.sendMessage("Расписание: Пн, Ср, Пт — 18:00 (теория).");
                    return true;
                }
                @Override
                public boolean getStats() {
                    ui.sendMessage("Всего записано: " + repository.count() + " чел.");
                    return true;
                }
                @Override
                public boolean getPassCommand() {
                    ui.sendMessage("Неизвестная команда. Введите /help");
                    return true;
                }
            };

            // Выполняем команду
            handler.handler(text).getAsBoolean();
        }
    }

    @Override
    public String getBotUsername() {
        return "a_unique_driving_school_bot";
    }

    @Override
    public String getBotToken() {
        return "ТОКЕН";
    }

    @Override
    public void onRegister() {
        // Этот метод переопределяет стандартную настройку, чтобы бот не падал из-за вебхука
        try {
            super.onRegister();
        } catch (Exception e) {
            System.out.println("Предупреждение: вебхук не сброшен, но продолжаем работу.");
        }
    }
}