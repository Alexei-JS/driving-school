package com.drivingSchool.app.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramUI implements TelegramBotUI {
    private final TelegramBot bot;
    private long chatId;

    public TelegramUI(TelegramBot bot) {
        this.bot = bot;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }

    @Override
    public void sendMessage(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            bot.execute(message);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}