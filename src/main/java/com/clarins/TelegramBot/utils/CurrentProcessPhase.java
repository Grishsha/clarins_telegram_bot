package com.clarins.TelegramBot.utils;

import lombok.NoArgsConstructor;
import com.clarins.TelegramBot.model.BotState;

@NoArgsConstructor
public class CurrentProcessPhase {

    public BotState get(BotState currentBotState) {
        switch (currentBotState) {
            case NOT_AUTHORISED:
            case REG_LAST_NAME:
            case REG_FIRST_NAME:
            case REG_SECOND_NAME:
            case REG_EMAIL:
            case REG_DEPARTMENT:
            case REG_PHONE:
            case REGISTRATION:
                return BotState.REGISTRATION;
            case EDIT_LAST_NAME:
            case EDIT_FIRST_NAME:
            case EDIT_SECOND_NAME:
            case EDIT_EMAIL:
            case EDIT_DEPARTMENT:
            case EDIT_PHONE:
            case CHANGE_USER_PROFILE:
                return BotState.CHANGE_USER_PROFILE;
            case NEW_INC:
            case INC_DESCRIPTION:
            case INC_PHOTO:
            case INC_SERVICE:
            case INC_RESP_GROUP:
            case INC_RESP_PERSON:
            case INC_FILLING:
                return BotState.INC_FILLING;
            case EDIT_INC_DESCRIPTION:
            case EDIT_INC_PHOTO:
            case EDIT_INC_SERVICE:
            case EDIT_INC_RESP_GROUP:
            case EDIT_INC_RESP_PERSON:
            case CHANGE_INCIDENT_DATA:
                return BotState.CHANGE_INCIDENT_DATA;
            case SHOW_HELP_MENU:
                return BotState.SHOW_HELP_MENU;
            case SHOW_USER_PROFILE:
                return BotState.SHOW_USER_PROFILE;
            default:
                return BotState.SHOW_MAIN_MENU;
        }
    }
}
