package com.clarins.TelegramBot.service.handlers.new_incident;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CreateIncidentInt {
    SendMessage process();
}
