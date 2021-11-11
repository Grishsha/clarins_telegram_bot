package com.clarins.TelegramBot.service.handlers.registration;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface NewUserRegistrationInt {
    SendMessage process();
}
