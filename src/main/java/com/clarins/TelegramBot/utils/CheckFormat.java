package com.clarins.TelegramBot.utils;

import lombok.NoArgsConstructor;
import com.clarins.TelegramBot.model.BotState;

@NoArgsConstructor
public class CheckFormat {
    public Boolean isCorrect(BotState state, String text) {

        switch (state) {
            case REG_EMAIL:
            case EDIT_EMAIL:
                return text.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
            case REG_PHONE:
            case EDIT_PHONE:
                return text.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
        }
        return false;
    }
}
