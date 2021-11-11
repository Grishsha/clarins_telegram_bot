package com.clarins.TelegramBot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;

public interface InputMessage {
    SendMessage handle(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller);

    BotState getHandlerName();
}
