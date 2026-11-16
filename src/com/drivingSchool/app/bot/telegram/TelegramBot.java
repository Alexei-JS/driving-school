package com.drivingSchool.app.bot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.drivingSchool.app.bot.BotCommandHandler;
import com.drivingSchool.app.bot.console.ConsoleBotCommandHandler;
import com.drivingSchool.app.storage.StudentRepository;
import com.drivingSchool.app.forms.EnrollmentForm;
import com.drivingSchool.app.tickets.Question;
import com.drivingSchool.app.tickets.TicketService;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String HELP_TEXT = "📋 *Доступные команды:*\n\n"
            + "🚗 /start — перезапустить бота\n"
            + "ℹ️ /aboutMe — информация об этом боте\n"
            + "📅 /schedule — расписание занятий\n"
            + "📊 /stats — статистика\n"
            + "👤 /profile — личный кабинет\n"
            + "📚 /tickets — решать экзаменационные билеты\n"
            + "📢 /feedback — оставить отзыв/пожелание\n"
            + "🔜 /soon — ознакомиться с планируемыми нововведениями\n"
            + "📝 /completeForm — записаться на курс\n"
            + "🚙 /drive — записаться на вождение";

    private final String token;
    private final long adminId;
    private final StudentRepository repository;
    private final TelegramUI ui;
    private final BotCommandHandler handler;

    private final java.util.Map<Long, Boolean> isWaitingForSchedule = new java.util.HashMap<>();
    private final java.util.Map<Long, Boolean> isWaitingForForm = new java.util.HashMap<>();

    private final TicketService tickets =
            new TicketService(java.nio.file.Path.of("data", "tickets"));

    private final List<String> slots;

    public TelegramBot(String token, long adminId, StudentRepository repository) {
        this.token = token;
        this.adminId = adminId;
        this.repository = repository;
        this.ui = new TelegramUI(this);
        this.slots = this.loadSlots();

        // Создаем хэндлер один раз
        this.handler = new ConsoleBotCommandHandler(repository, new Scanner(System.in)) {
            @Override
            public boolean getStart() {
                showMainMenu(ui.getChatId());
                return true;
            }

            @Override
            public boolean getHelp() {
                ui.sendMessage(HELP_TEXT);
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
                    String content;

                    try {
                        content = java.nio.file.Files.readString(java.nio.file.Path.of("schedule.txt"));
                    } catch (Exception e) {
                        content = "пока не задано";
                    }

                    isWaitingForSchedule.put(ui.getChatId(), true);
                    sendWithKeyboard(ui.getChatId(), "📅 *Текущее расписание:*\n\n" + content + "\n\n" +
                        "👑 Вы администратор. Отправьте новое расписание одним сообщением\n" +
                        "или нажмите «❌ Отмена».", cancelKeyboard());
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
                String existing = findEnrollment(ui.getChatId());

                if (existing != null) {
                    ui.sendMessage("✅ Вы уже записаны на курс.\n\n"
                        + "📝 Ваша анкета: " + existing + "\n\n"
                        + "Если нужно изменить данные — обратитесь к администратору @n1tr0gen1um.\n"
                        + "Посмотреть кабинет: /profile");
                    return true;
                }

                isWaitingForForm.put(ui.getChatId(), true);
                sendWithKeyboard(ui.getChatId(), "📝 *Запись на курс*\n\n" +
                    "Отправьте данные одним сообщением в формате:\n" +
                    "`ФИО, Возраст, Категория (A/B/C), Справка (да/нет)`\n\n" +
                    "Пример: Иван Иванов, 25, B, да\n" +
                    "Или нажмите «❌ Отмена».", cancelKeyboard());
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

            if (callData.equals("main_menu")) {
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(callbackQueryId);
                try { execute(answer); } catch (Exception ignored) {}

                isWaitingForForm.put(callbackChatId, false);
                isWaitingForSchedule.put(callbackChatId, false);
                this.showMainMenu(callbackChatId);
                return;
            }

            if (callData.startsWith("ticket_") || callData.startsWith("again_")
                    || callData.startsWith("answer_") || callData.startsWith("next")
                    || callData.equals("tickets_menu")) {
                this.handleTicketCallback(callbackChatId, callData, callbackQueryId);
                return;
            }

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

            if (text.equals("/tickets")) {
                this.showTicketMenu(chatId);
                return;
            }

            if (text.equals("/profile")) {
                this.showProfile(chatId);
                return;
            }

            if (text.equals("🏠 В главное меню")) {
                isWaitingForForm.put(chatId, false);
                isWaitingForSchedule.put(chatId, false);
                this.showMainMenu(chatId);
                return;
            }

            if (text.equals("❌ Отмена")) {
                this.cancelCurrent(chatId);
                return;
            }

            if (text.equals("👤 Личный кабинет")) {
                this.showProfile(chatId);
                return;
            }

            if (isWaitingForSchedule.getOrDefault(chatId, false) && chatId == adminId) {
                if (text.equals("/cancel")) {
                    isWaitingForSchedule.put(chatId, false);
                    ui.sendMessage("❌ Отмена. Расписание не изменено.");
                } else {
                    try {
                        java.nio.file.Files.writeString(java.nio.file.Path.of("schedule.txt"), text, java.nio.charset.StandardCharsets.UTF_8);
                        isWaitingForSchedule.put(chatId, false);
                        ui.sendMessage("✅ Расписание успешно обновлено!");
                    } catch (Exception e) {
                        ui.sendMessage("Ошибка сохранения: " + e.getMessage());
                    }
                }
                return;
            }

            if (isWaitingForForm.getOrDefault(chatId, false)) {
                if (text.equals("/cancel")) {
                    isWaitingForForm.put(chatId, false);
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

                    String error = EnrollmentForm.saveStudent(name, age, cat, cert, repository);
                    if (error == null) {
                        // Успех
                        try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(new java.io.FileOutputStream("students.txt", true), java.nio.charset.StandardCharsets.UTF_8)) {
                            long timestamp = System.currentTimeMillis();
                            fw.write("[" + timestamp + "] [" + chatId + "] " + text + "\n");
                        } catch (Exception e) { e.printStackTrace(); }
                        isWaitingForForm.put(chatId, false);
                        ui.sendMessage("✅ Вы успешно записаны на курс!");
                    } else {
                        // Ошибка
                        ui.sendMessage(error + "\nПопробуйте снова или введите /cancel.");
                    }
                }
                return;
            }
            
            handler.handler(text).getAsBoolean();
        }
    }

    //Навигация под полем ввода
     private ReplyKeyboardMarkup mainKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("👤 Личный кабинет"));
        row.add(new KeyboardButton("🏠 В главное меню"));

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private ReplyKeyboardMarkup cancelKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("❌ Отмена"));
        row.add(new KeyboardButton("🏠 В главное меню"));

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void sendWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void showMainMenu(long chatId) {
        this.sendWithKeyboard(chatId, HELP_TEXT, this.mainKeyboard());
    }

    private void cancelCurrent(long chatId) {
        isWaitingForForm.put(chatId, false);
        isWaitingForSchedule.put(chatId, false);
        this.tickets.finish(chatId);
        this.sendWithKeyboard(chatId, "❌ Действие отменено.", this.mainKeyboard());
    }
    

    //Билеты

    private void handleTicketCallback(long chatId, String data, String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        try {
            execute(answer);
        } catch (Exception ignored) {
        }

        if (data.equals("tickets_menu")) {
            this.showTicketMenu(chatId);
            return;
        }

        String[] parts = data.split("_");

        if (parts[0].equals("ticket") || parts[0].equals("again")) {
            this.startTicket(chatId, Integer.parseInt(parts[1]));
            return;
        }

        if (parts[0].equals("answer")) {
            this.answerQuestion(chatId, Integer.parseInt(parts[1]));
            return;
        }

        if (parts[0].equals("next")) {
            this.nextQuestion(chatId);
        }
    }

    private void showTicketMenu(long chatId) {
        if (this.tickets.isEmpty()) {
            this.send(chatId, "📚 Билеты пока не загружены.\nПоложите файлы в data/tickets.", null);
            return;
        }

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int number : this.tickets.numbers()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Билет " + number);
            button.setCallbackData("ticket_" + number);
            row.add(button);

            if (row.size() == 4) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        this.send(chatId, "📚 Выберите билет:", markup);
    }

    private void startTicket(long chatId, int number) {
        TicketService.Run run = this.tickets.start(chatId, number);

        if (run == null) {
            this.send(chatId, "Билет " + number + " не найден.", null);
            return;
        }

        this.showQuestion(chatId, run);
    }

    private void showQuestion(long chatId, TicketService.Run run) {
        Question question = run.current();

        StringBuilder text = new StringBuilder();
        text.append("📚 Билет ").append(run.ticket().number())
                .append(", вопрос ").append(run.index() + 1)
                .append(" из ").append(run.ticket().size()).append("\n\n");
        text.append(question.text()).append("\n\n");

        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < question.options().size(); i++) {
            text.append(i + 1).append(") ").append(question.options().get(i)).append("\n");

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1));
            button.setCallbackData("answer_" + i);
            row.add(button);
        }

        if (question.imageUrl() != null) {
            text.append("\n🖼 Картинка: ").append(question.imageUrl());
        }

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        this.send(chatId, text.toString(), markup);
    }

    private void answerQuestion(long chatId, int option) {
        TicketService.Run run = this.tickets.run(chatId);

        if (run == null || run.current() == null) {
            this.showTicketMenu(chatId);
            return;
        }

        Question question = run.current();
        boolean isCorrect = option == question.correctIndex();

        run.answer(isCorrect);

        StringBuilder text = new StringBuilder();

        if (isCorrect) {
            text.append("✅ Верно!");
        } else {
            text.append("❌ Неверно. Правильный ответ: ")
                    .append(question.correctIndex() + 1).append(" — ")
                    .append(question.options().get(question.correctIndex()));
        }

        if (question.explanation() != null && !question.explanation().isBlank()) {
            text.append("\n\n").append(question.explanation());
        }

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(run.finished() ? "📊 Результат" : "Далее ▶");
        next.setCallbackData("next");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(next);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        this.send(chatId, text.toString(), markup);
    }

    private void nextQuestion(long chatId) {
        TicketService.Run run = this.tickets.run(chatId);

        if (run == null) {
            this.showTicketMenu(chatId);
            return;
        }

        if (run.finished()) {
            this.showResult(chatId, run);
            return;
        }

        this.showQuestion(chatId, run);
    }

    private void showResult(long chatId, TicketService.Run run) {
        int number = run.ticket().number();
        int total = run.ticket().size();
        int correct = run.correct();

        String verdict;

        if (correct == total) {
            verdict = "🏆 Идеально, все ответы верные!";
        } else if (correct * 100 / total >= 90) {
            verdict = "✅ Экзамен сдан.";
        } else {
            verdict = "📖 Стоит повторить материал и попробовать ещё.";
        }

        String text = "📊 Билет " + number + "\n\n"
                + "Правильных ответов: " + correct + " из " + total + "\n"
                + "Ошибок: " + run.wrong() + "\n\n"
                + verdict;

        InlineKeyboardButton again = new InlineKeyboardButton();
        again.setText("🔁 Заново");
        again.setCallbackData("again_" + number);

        InlineKeyboardButton menu = new InlineKeyboardButton();
        menu.setText("📚 К билетам");
        menu.setCallbackData("tickets_menu");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(again);
        row.add(menu);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        this.tickets.rememberResult(chatId, "Билет " + number + ": " + correct + " из " + total);
        this.tickets.finish(chatId);
        this.send(chatId, text, markup);
    }

    private void send(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        if (markup != null) {
            message.setReplyMarkup(markup);
        }

        try {
            this.execute(message);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    // ---------- Личный кабинет ----------

    private void showProfile(long chatId) {
        StringBuilder text = new StringBuilder();
        text.append("👤 Личный кабинет\n\n");

        String enrollment = this.findEnrollment(chatId);

        if (enrollment == null) {
            text.append("📝 Заявка на курс: не подана\nОтправьте /completeForm\n\n");
        } else {
            text.append("📝 Анкета: ").append(enrollment).append("\n\n");
        }

        List<String> bookings = this.findBookings(chatId);

        if (bookings.isEmpty()) {
            text.append("🚙 Записи на вождение: нет\nЗаписаться: /drive\n\n");
        } else {
            text.append("🚙 Записи на вождение:\n");

            for (String booking : bookings) {
                text.append("• ").append(booking).append("\n");
            }

            text.append("\n");
        }

        String result = this.tickets.lastResult(chatId);
        text.append("📚 Билеты: ").append(result == null ? "ещё не решали" : result);

        this.send(chatId, text.toString(), null);
    }

    private String findEnrollment(long chatId) {
        java.nio.file.Path path = java.nio.file.Path.of("students.txt");

        if (!java.nio.file.Files.exists(path)) {
            return null;
        }

        String marker = "[" + chatId + "]";

        try {
            List<String> lines = java.nio.file.Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

            for (int i = lines.size() - 1; i >= 0; i--) {
                String line = lines.get(i).trim();

                if (!line.startsWith("[")) {
                    continue;
                }

                int end = line.indexOf(']');

                if (end < 0) {
                    continue;
                }

                String rest = line.substring(end + 1).trim();

                if (rest.startsWith(marker)) {
                    return rest.substring(marker.length()).trim();
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private List<String> findBookings(long chatId) {
        List<String> result = new ArrayList<>();
        java.nio.file.Path path = java.nio.file.Path.of("appointments.txt");

        if (!java.nio.file.Files.exists(path)) {
            return result;
        }

        try {
            for (String line : java.nio.file.Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\] ", 2);

                if (parts.length == 2 && parts[0].replace("[", "").trim().equals(String.valueOf(chatId))) {
                    result.add(parts[1].trim());
                }
            }
        } catch (Exception ignored) {
        }

        return result;
    }

    private List<String> loadSlots() {
        try {
            return List.copyOf(java.nio.file.Files.readAllLines(java.nio.file.Path.of("slots.txt")));
        } catch (Exception error) {
            System.out.println("slots.txt не найден — запись на вождение недоступна.");
            return List.of();
        }
    }

    private void handleDriveCommand(long chatId, int page, Integer messageId) {
        try {
            if (this.slots.isEmpty()) {
                this.send(chatId, "Запись на вождение временно недоступна.", null);
                return;
            }

            List<String> allSlots = this.slots;
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

            List<InlineKeyboardButton> menuRow = new ArrayList<>();
            InlineKeyboardButton menuButton = new InlineKeyboardButton();
            menuButton.setText("🏠 В меню");
            menuButton.setCallbackData("main_menu");
            menuRow.add(menuButton);
            keyboard.add(menuRow);

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