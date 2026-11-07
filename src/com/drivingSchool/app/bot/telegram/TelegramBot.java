package com.drivingSchool.app.bot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.drivingSchool.app.bot.BotCommandHandler;
import com.drivingSchool.app.bot.console.ConsoleBotCommandHandler;
import com.drivingSchool.app.storage.InMemoryStudentRepository;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.forms.EnrollmentForm;

public class TelegramBot extends TelegramLongPollingBot {

    private final String token;
    private final long adminId;
    private final StudentRepository repository;
    private final TelegramUI ui;
    private final BotCommandHandler handler;
    
    private boolean isWaitingForSchedule = false;
    private boolean isWaitingForForm = false;

    public TelegramBot(String token, long adminId) {
        this.token = token;
        this.adminId = adminId;
        this.repository = new InMemoryStudentRepository();
        this.ui = new TelegramUI(this);
        
        // Создаем хэндлер один раз
        this.handler = new ConsoleBotCommandHandler(repository, new Scanner(System.in)) {
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
                    "📝 /completeForm — записаться на курс\n" +
                    "🚙 /drive — записаться на вождение");
                return true;
            }

            @Override
            public boolean getAboutMe() {
                ui.sendMessage("🚘 *Автошкола*\n\n" +
                    "Я — консольный бот, который переехал в Телеграм! Моя цель — сделать обучение вождению удобным и понятным.");
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
                int randomPassed = (int) (Math.random() * 50) + 100;
                int randomHours = (int) (Math.random() * 20) + 30;
                
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
                ui.sendMessage("В Телеграме я не могу выключиться! 😄");
                return true; 
            }

            @Override
            public boolean getSoon() {
                ui.sendMessage("🚀 *Скоро в боте появится:*\n\n" +
                    "📍 Личный кабинет курсанта\n" +
                    "🎟️ Запись на экзамен в ГИБДД\n" +
                    "📚 Онлайн-тесты по билетам ПДД\n" +
                    "💬 Чат с инструктором");
                return true;
            }

            @Override
            public boolean getFeedBack() {
                ui.sendMessage("✍️ *Обратная связь:*\nЧтобы оставить отзыв или задать вопрос, напишите администратору: @n1tr0gen1um");
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
    }

    @Override
    public void onUpdateReceived(Update update) {
        // 1. Обработка нажатий на кнопки
        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackQueryId = update.getCallbackQuery().getId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            ui.setChatId(callbackChatId);

            // Навигация по дням
            if (callData.startsWith("day_")) {
                int page = Integer.parseInt(callData.substring(4));
                handleDriveCommand(callbackChatId, page, messageId);
                
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(callbackQueryId);
                try { execute(answer); } catch (Exception e) { e.printStackTrace(); }
                return;
            }

            // Запись на слот
            if (callData.startsWith("slot_")) {
                String slot = callData.substring(5);
                try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(new java.io.FileOutputStream("appointments.txt", true), java.nio.charset.StandardCharsets.UTF_8)) {
                    fw.write("[" + callbackChatId + "] " + slot + "\n");
                    
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setCallbackQueryId(callbackQueryId);
                    execute(answer);
                    
                    ui.sendMessage("✅ Вы записаны на вождение: " + slot);
                } catch (Exception e) {
                    ui.sendMessage("Ошибка записи: " + e.getMessage());
                }
                return;
            }

            // Отмена записи
            if (callData.startsWith("cancel_")) {
                String slotToCancel = callData.substring(7);
                try {
                    java.nio.file.Path appPath = java.nio.file.Path.of("appointments.txt");
                    if (java.nio.file.Files.exists(appPath)) {
                        List<String> lines = java.nio.file.Files.readAllLines(appPath);
                        List<String> newLines = new ArrayList<>();
                        String expected = "[" + callbackChatId + "] " + slotToCancel;
                        for (String line : lines) {
                            if (!line.trim().equals(expected)) newLines.add(line);
                        }
                        java.nio.file.Files.write(appPath, newLines, java.nio.charset.StandardCharsets.UTF_8);
                        
                        AnswerCallbackQuery answer = new AnswerCallbackQuery();
                        answer.setCallbackQueryId(callbackQueryId);
                        execute(answer);
                        
                        ui.sendMessage("❌ Вы отменили запись на: " + slotToCancel);
                    }
                } catch (Exception e) {
                    ui.sendMessage("Ошибка отмены: " + e.getMessage());
                }
                return;
            }
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            ui.setChatId(chatId);

            if (text.equals("/drive")) {
                handleDriveCommand(chatId, 0, null);
                return;
            }

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
                    String[] parts = text.split(",");
                    if (parts.length != 4) {
                        ui.sendMessage("❌ Ошибка формата. Отправьте данные в формате: ФИО, Возраст, Категория, Справка (да/нет)");
                        return;
                    }
                    String name = parts[0].trim();
                    String age = parts[1].trim();
                    String cat = parts[2].trim();
                    String cert = parts[3].trim();

                    boolean success = EnrollmentForm.saveStudent(name, age, cat, cert, repository);
                    if (success) {
                        try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(new java.io.FileOutputStream("students.txt", true), java.nio.charset.StandardCharsets.UTF_8)) {
                            long timestamp = System.currentTimeMillis();
                            fw.write("[" + timestamp + "] " + text + "\n");
                        } catch (Exception e) { e.printStackTrace(); }
                        isWaitingForForm = false;
                        ui.sendMessage("✅ Вы успешно записаны на курс!");
                    } else {
                        ui.sendMessage("❌ Ошибка валидации. Проверьте данные и попробуйте снова (или введите /cancel).");
                    }
                }
                return;
            }
            
            handler.handler(text).getAsBoolean();
        }
    }

    private void handleDriveCommand(long chatId, int page, Integer messageId) {
        try {
            List<String> allSlots = java.nio.file.Files.readAllLines(java.nio.file.Path.of("slots.txt"));
            List<String> occupiedSlots = new ArrayList<>();
            List<String> myBookings = new ArrayList<>();
            
            if (java.nio.file.Files.exists(java.nio.file.Path.of("appointments.txt"))) {
                List<String> appointments = java.nio.file.Files.readAllLines(java.nio.file.Path.of("appointments.txt"));
                for (String app : appointments) {
                    String[] parts = app.split("\\] ", 2);
                    if (parts.length == 2) {
                        String bookedChatId = parts[0].replace("[", "").trim();
                        String bookedSlot = parts[1].trim();
                        occupiedSlots.add(bookedSlot);
                        if (bookedChatId.equals(String.valueOf(chatId))) myBookings.add(bookedSlot);
                    }
                }
            }

            java.util.Map<String, List<String>> slotsByDay = new java.util.LinkedHashMap<>();
            for (String slot : allSlots) {
                String cleanSlot = slot.trim();
                if (cleanSlot.isEmpty()) continue;
                String day = cleanSlot.split(" ")[0];
                slotsByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(cleanSlot);
            }

            List<String> days = new ArrayList<>(slotsByDay.keySet());
            if (page < 0) page = 0;
            if (page >= days.size()) page = days.size() - 1;

            String currentDay = days.get(page);
            List<String> currentSlots = slotsByDay.get(currentDay);

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            for (String slot : currentSlots) {
                if (occupiedSlots.contains(slot)) continue;
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("🚙 " + slot);
                button.setCallbackData("slot_" + slot);
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(button);
                keyboard.add(row);
            }

            List<InlineKeyboardButton> navRow = new ArrayList<>();
            if (page > 0) {
                InlineKeyboardButton prev = new InlineKeyboardButton();
                prev.setText("<<");
                prev.setCallbackData("day_" + (page - 1));
                navRow.add(prev);
            }
            if (page < days.size() - 1) {
                InlineKeyboardButton next = new InlineKeyboardButton();
                next.setText(">>");
                next.setCallbackData("day_" + (page + 1));
                navRow.add(next);
            }
            if (!navRow.isEmpty()) keyboard.add(navRow);

            for (String mySlot : myBookings) {
                InlineKeyboardButton cancelButton = new InlineKeyboardButton();
                cancelButton.setText("❌ Отказаться от " + mySlot);
                cancelButton.setCallbackData("cancel_" + mySlot);
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(cancelButton);
                keyboard.add(row);
            }

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);

            String text = "📅 *" + currentDay + "*\nВыберите удобное время:";
            if (!myBookings.isEmpty()) text += "\n\n📌 *Ваши записи:*\n" + String.join("\n", myBookings);

            if (messageId != null) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);
                editMessage.setText(text);
                editMessage.setParseMode("Markdown");
                editMessage.setReplyMarkup(markup);
                execute(editMessage);
            } else {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(text);
                message.setParseMode("Markdown");
                message.setReplyMarkup(markup);
                execute(message);
            }

        } catch (Exception e) {
            ui.sendMessage("Свободных окошек пока нет.");
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "a_unique_driving_school_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public long getAdminId() {
        return adminId;
    }
}