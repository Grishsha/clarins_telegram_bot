package com.clarins.TelegramBot.utils;

import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@NoArgsConstructor
public class DefineChatID {
    public long get(Update update) {
        if (update.hasMessage())
            return update.getMessage().getChatId();
        if (update.hasCallbackQuery())
            return update.getCallbackQuery().getFrom().getId();
        return 0;
    }
}
