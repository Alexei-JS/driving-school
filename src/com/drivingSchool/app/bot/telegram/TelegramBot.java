package com.drivingSchool.app.bot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Scanner;

import com.drivingSchool.app.bot.BotCommandHandler;
import com.drivingSchool.app.bot.console.ConsoleBotCommandHandler;
import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;

public class TelegramBot extends TelegramLongPollingBot {

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
    private boolean isWaitingForSchedule = false;
    private boolean isWaitingForForm = false;

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
                    if (ui.getChatId() == adminId) {
                        isWaitingForSchedule = true;
                        ui.sendMessage("👑 Вы вошли как администратор.\n\n" +
                            "Отправьте новый текст расписания одним сообщением.\n" +
                            "Или напишите /cancel для отмены.");
                    } else {
                        try {
                            String content = java.nio.file.Files.readString(java.nio.file.Path.of("schedule.txt"));
                            ui.sendMessage("📅 *Расписание занятий:*\n\n" + content);
                        } catch (Exception e) {
                            ui.sendMessage("📅 Расписание пока не задано.");
                        }
                    }
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

                @Override
                public boolean getCompleteForm() {
                    isWaitingForForm = true;
                    ui.sendMessage("📝 *Запись на курс*\n\n" +
                        "Отправьте данные одним сообщением в формате:\n" +
                        "`ФИО, Возраст, Категория (A/B/C), Справка (да/нет)`\n\n" +
                        "Пример: Иван Иванов, 25, B, да\n" +
                        "Или напишите /cancel для отмены.");
                    return true;
                }
            };

            if (isWaitingForSchedule && chatId == adminId) {
                if (text.equals("/cancel")) {
                    isWaitingForSchedule = false;
                    ui.sendMessage("❌ Отмена. Расписание не изменено.");
                } else {
                    try {
                        java.nio.file.Files.writeString(java.nio.file.Path.of("schedule.txt"), text, java.nio.charset.StandardCharsets.UTF_8);
                        isWaitingForSchedule = false;
                        ui.sendMessage("✅ Расписание успешно обновлено!");
                    } catch (Exception e) {
                        ui.sendMessage("Ошибка сохранения: " + e.getMessage());
                    }
                }
                return;
            }

            if (isWaitingForForm) {
                if (text.equals("/cancel")) {
                    isWaitingForForm = false;
                    ui.sendMessage("❌ Отмена. Вы не записаны.");
                } else {
                    try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(new java.io.FileOutputStream("students.txt", true), java.nio.charset.StandardCharsets.UTF_8)) {
                        // Добавляем ID пользователя и время регистрации
                        long timestamp = System.currentTimeMillis();
                        fw.write("[" + timestamp + "] " + text + "\n");
                        isWaitingForForm = false;
                        ui.sendMessage("✅ Вы успешно записаны на курс!\nМы свяжемся с вами в ближайшее время.");
                    } catch (Exception e) {
                        ui.sendMessage("Ошибка записи: " + e.getMessage());
                    }
                }
                return;
            }
            
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