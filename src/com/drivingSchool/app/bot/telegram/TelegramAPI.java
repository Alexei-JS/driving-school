package com.drivingSchool.app.bot.telegram;

import com.drivingSchool.app.bot.console.handlers.BotCommandHandler;
import com.drivingSchool.app.bot.console.handlers.ConsoleBotCommandHandler;
import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Scanner;

public class TelegramAPI extends TelegramLongPollingBot {

    private static final java.util.Properties props = new java.util.Properties();
    static {
        try (java.io.FileInputStream fis = new java.io.FileInputStream("bot.properties")) {
            props.load(fis);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки bot.properties");
        }
    }

    private final StudentRepository repository = new InMemoryStudentRepository();
    private final long adminId = Long.parseLong(props.getProperty("bot.admin_id"));

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            TelegramUI ui = new TelegramUI(this, chatId);
            
            BotCommandHandler handler = new ConsoleBotCommandHandler(repository, new Scanner(System.in)) {
                
                @Override
                public boolean getStart() {
                    ui.sendMessage("👋 Привет! Я — бот автошколы.\n\n" +
                        "Я помогу тебе записаться на курсы, узнать расписание и посмотреть статистику.\n\n" +
                        "👇 Нажми /help, чтобы увидеть все команды.");
                    return true;
                }

                @Override
                public boolean getHelp() {
                    ui.sendMessage("📋 *Доступные команды:*\n\n" +
                        "🚗 /start — перезапустить бота\n" +
                        "ℹ️ /aboutMe — информация об этом боте\n" +
                        "📅 /schedule — расписание занятий\n" +
                        "📊 /stats — статистика\n" +
                        "📢 /feedback — оставить отзыв/пожелание\n" +
                        "🔜 /soon — ознакомиться с планируемыми нововведениями\n" +
                        "📝 /completeForm — записаться на курс (скоро)");
                    return true;
                }

                @Override
                public boolean getAboutMe() {
                    ui.sendMessage("🚘 *Автошкола*\n\n" +
                        "Я — консольный бот, который переехал в Телеграм! Моя цель — сделать обучение вождению удобным и понятным.\n" +
                        "Задай мне вопрос или выбери команду из списка.");
                    return true;
                }

                @Override
                public boolean getSchedule() {
                    ui.sendMessage("📅 *Расписание занятий:*\n\n" +
                        "📚 *Теория:* Пн, Ср, Пт — 18:00–20:00\n" +
                        "🚙 *Практика:* Вт, Чт — по записи с инструктором\n" +
                        "📝 *Экзамен:* Сб — 10:00 (по готовности группы)");
                    return true;
                }

                @Override
                public boolean getStats() {
                    int total = repository.count();
                    int randomPassed = (int) (Math.random() * 50) + 100; // Имитация пройденных билетов
                    int randomHours = (int) (Math.random() * 20) + 30;   // Имитация часов практики
                    
                    ui.sendMessage("📊 *Статистика автошколы:*\n\n" +
                        "👥 Записано курсантов: *" + total + "*\n" +
                        "📖 Сдано билетов: *" + randomPassed + "*\n" +
                        "🛣️ Откатано часов: *" + randomHours + "*\n\n" +
                        "_Это общая статистика по школе._");
                    return true;
                }

                @Override
                public boolean getPassCommand() {
                    ui.sendMessage("🤔 Я не знаю такой команды.\nПопробуй /help, чтобы увидеть, что я умею.");
                    return true;
                }

                @Override
                public boolean getExit() {
                    ui.sendMessage("В Телеграме я не могу выключиться! 😄 Но ты всегда можешь закрыть этот чат.");
                    return true; 
                }

                @Override
                public boolean getSoon() {
                    ui.sendMessage("🚀 *Скоро в боте появится:*\n\n" +
                        "📍 Личный кабинет курсанта (успеваемость, долги по оплате)\n" +
                        "🎟️ Запись на экзамен в ГИБДД\n" +
                        "📚 Онлайн-тесты по билетам ПДД\n" +
                        "💬 Чат с инструктором\n\n" +
                        "Оставайтесь на связи!");
                    return true;
                }

                @Override
                public boolean getFeedBack() {
                    ui.sendMessage("✍️ *Обратная связь:*\nЧтобы оставить отзыв или задать вопрос, напишите нашему администратору: @n1tr0gen1um");
                    return true;
                }
            };

            handler.handler(text).getAsBoolean();
        }
    }

    @Override
    public String getBotUsername() {
        return "a_unique_driving_school_bot";
    }

    @Override
    public String getBotToken() {
        return props.getProperty("bot.token");
    }

    public long getAdminId() {
        return Long.parseLong(props.getProperty("bot.admin_id"));
    }
}