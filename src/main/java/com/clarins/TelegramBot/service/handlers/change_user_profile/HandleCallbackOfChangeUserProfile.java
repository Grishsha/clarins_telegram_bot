package com.clarins.TelegramBot.service.handlers.change_user_profile;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.entity.UserDataEntity;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.handlers.menu.InlineMenuButtons;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;

@Slf4j
@NoArgsConstructor
public class HandleCallbackOfChangeUserProfile implements ChangeUserProfileInt {//HandleMessageByType {
    private long chatId;
    private DataCache dataCache;
    private Update update;
    private SendMessage message;
    private Controller controller;

    public HandleCallbackOfChangeUserProfile(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
        this.chatId = chatId;
        this.dataCache = dataCache;
        this.update = update;
        this.message = message;
        this.controller = controller;
    }

    @Override
    public SendMessage process() {
        UserProfileDataRepository repository = controller.getRepository();
        UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        MainMenuButtons mainMenuButtons = new MainMenuButtons();
        InlineMenuButtons inlineMenuButtons = new InlineMenuButtons();

        //if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
        switch (update.getCallbackQuery().getData()) {
            case "buttonLastName":
                message.setText(BotState.EDIT_LAST_NAME.getDescription());
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_LAST_NAME);
                break;
            case "buttonFirstName":
                message.setText(BotState.EDIT_FIRST_NAME.getDescription());
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_FIRST_NAME);
                break;
            case "buttonSecondName":
                message.setText(BotState.EDIT_SECOND_NAME.getDescription());
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_SECOND_NAME);
                break;
            case "buttonEMail":
                message.setText(BotState.EDIT_EMAIL.getDescription());
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_EMAIL);
                break;
            case "buttonDept":
                //log.error("buttonDept");
                //log.error(BotState.EDIT_DEPARTMENT.getDescription());
                message.setText(BotState.EDIT_DEPARTMENT.getDescription());
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_DEPARTMENT);
                break;
            case "buttonPhone":
                message.setText(BotState.EDIT_PHONE.getDescription());
                message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(true));
                dataCache.setUserCurrentBotState(chatId, BotState.EDIT_PHONE);
                break;
            case "buttonYes":
                message.setText("Выберите что вы хотите изменить");
                //dataCache.setUserCurrentBotState(chatId, BotState.CHANGE_USER_PROFILE);
                message.setReplyMarkup(inlineMenuButtons.getUserProfileButtons());
                break;
            case "buttonNo":
                message.setText("Изменения внесены");
                dataCache.setUserCurrentBotState(chatId, BotState.SHOW_MAIN_MENU);

                UserDataEntity entity = new UserDataEntity();
                entity.setId(chatId);
                entity.setUserProfileData(userProfileData);

                if (repository.existsById(chatId)) {
                    //log.error("delete from change profile");
                    repository.deleteById(chatId);
                    repository.save(entity);
                } else
                    repository.save(entity);

                message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                break;
            default:
                break;
        }

        return message;
    }

    //@Override
    public BotState getHandlerName() {
        return BotState.CHANGE_USER_PROFILE;
    }
}
