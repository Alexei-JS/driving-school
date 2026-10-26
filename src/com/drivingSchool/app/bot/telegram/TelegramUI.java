package com.drivingSchool.app.bot.telegram;

import com.drivingSchool.app.core.BotUI;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramUI implements BotUI {
    private final TelegramAPI bot;
    private final long chatId;

    public TelegramUI(TelegramAPI bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void sendMessage(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        message.setParseMode("Markdown");

        try {
            bot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}