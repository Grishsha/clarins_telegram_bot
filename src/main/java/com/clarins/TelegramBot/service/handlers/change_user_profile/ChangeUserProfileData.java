package com.clarins.TelegramBot.service.handlers.change_user_profile;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.handlers.menu.InlineMenuButtons;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;
import com.clarins.TelegramBot.utils.CheckFormat;

@Slf4j
public class ChangeUserProfileData implements ChangeUserProfileInt {//HandleTextMessageByStage {
    private final MainMenuButtons mainMenuButtons;
    private final InlineMenuButtons inlineMenuButtons;
    private final long chatId;
    private final DataCache dataCache;
    private final Update update;
    private final SendMessage message;
    private final Controller controller;

    public ChangeUserProfileData(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
        mainMenuButtons = new MainMenuButtons();
        inlineMenuButtons = new InlineMenuButtons();
        this.chatId = chatId;
        this.dataCache = dataCache;
        this.update = update;
        this.message = message;
        this.controller = controller;
    }

    @Override
    public SendMessage process() {
        UserProfileDataRepository repository = controller.getRepository();
        BotState currentBotState = dataCache.getUserCurrentBotState(chatId);
        UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        CheckFormat checkFormat = new CheckFormat();

        switch (currentBotState) {
            case EDIT_LAST_NAME:
                userProfileData.setLastName(update.getMessage().getText());
                break;
            case EDIT_FIRST_NAME:
                userProfileData.setFirstName(update.getMessage().getText());
                break;
            case EDIT_SECOND_NAME:
                userProfileData.setSecondName(update.getMessage().getText());
                break;
            case EDIT_EMAIL:
                if (checkFormat.isCorrect(currentBotState, update.getMessage().getText())) {
                    userProfileData.setEMail(update.getMessage().getText());
                    break;
                } else {
                    message.setText("введенный вами текст не похож на адрес почты.\n" +
                            "Попробуйте еще раз.");
                    return message;
                }
            case EDIT_DEPARTMENT:
                userProfileData.setDepartment(update.getMessage().getText());
                break;
            case EDIT_PHONE:
                if (update.getMessage().hasText())
                    if (checkFormat.isCorrect(currentBotState, update.getMessage().getText())) {
                        message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(true));
                        userProfileData.setPhone(update.getMessage().getText());
                        break;
                    } else {
                        message.setText("введенный вами текст не похож на номер телефона.\n" +
                                "Попробуйте еще раз.");
                        return message;
                    }
                if (update.getMessage().hasContact())
                    userProfileData.setPhone(update.getMessage().getContact().getPhoneNumber());
                else
                    userProfileData.setPhone(update.getMessage().getText());
                break;
            default:
                break;
        }
        message.setText(BotState.CHANGE_USER_PROFILE.getDescription());
        message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
        dataCache.setUserProfileData(chatId, userProfileData);

        return message;
    }
}
