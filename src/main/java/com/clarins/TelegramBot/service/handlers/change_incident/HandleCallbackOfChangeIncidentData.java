package com.clarins.TelegramBot.service.handlers.change_incident;

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
import com.clarins.TelegramBot.utils.TTRequestFilledHandler;

@Slf4j
@NoArgsConstructor
public class HandleCallbackOfChangeIncidentData implements ChangeIncidentInt {//HandleMessageByType {
    private long chatId;
    private DataCache dataCache;
    private Update update;
    private SendMessage message;
    private Controller controller;

    public HandleCallbackOfChangeIncidentData(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
        this.chatId = chatId;
        this.dataCache = dataCache;
        this.update = update;
        this.message = message;
        this.controller = controller;
    }

    @Override
    public SendMessage process() {
        UserProfileDataRepository repository = controller.getRepository();
        //BotState currentBotState = dataCache.getUserCurrentBotState(chatId);
        //PhaseState currentPhaseState = dataCache.getUserCurrentPhaseState(chatId);
        UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        //UserAttachments userAttachments = dataCache.getUserFilesData(chatId);
        MainMenuButtons mainMenuButtons = new MainMenuButtons();
        InlineMenuButtons inlineMenuButtons = new InlineMenuButtons();

        //if (currentPhaseState == PhaseState.CHANGE_INCIDENT_DATA) {
        switch (update.getCallbackQuery().getData()) {
            case "buttonDescription":
                message.setText("Введите вашу фамилию");
                dataCache.setUserCurrentBotState(chatId, BotState.INC_DESCRIPTION);
                break;
            case "buttonService":
                message.setText("Введите название IT сервиса");
                dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                break;
            case "buttonRespGroup":
                message.setText("Введите название Рабочей группы");
                dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_GROUP);
                break;
            case "buttonRespPerson":
                message.setText("Введите ФИО испонителя");
                dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_PERSON);
                break;
            case "buttonYes":
                message.setText("Выберите что вы хотите изменить");
                dataCache.setUserCurrentBotState(chatId, BotState.SHOW_MAIN_MENU);
                message.setReplyMarkup(inlineMenuButtons.getUserProfileButtons());
                break;
            case "buttonNo":
                message.setText("Изменения внесены");
                dataCache.setUserCurrentBotState(chatId, BotState.SHOW_MAIN_MENU);

                UserDataEntity entity = new UserDataEntity();
                entity.setId(chatId);
                entity.setUserProfileData(userProfileData);

                if (repository.existsById(chatId)) {
                    log.error("delete from change incident");
                    repository.deleteById(chatId);
                    repository.save(entity);
                } else
                    repository.save(entity);
                message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                break;
            default:
                break;
        }

        if (dataCache.getUserCurrentBotState(chatId) == BotState.INC_REQUEST_FILLED) {
            TTRequestFilledHandler ttRequestFilledHandler = new TTRequestFilledHandler(chatId, dataCache, message, controller);
            ttRequestFilledHandler.handle();
        }

        return message;//replyMessagesService.createReplyMessage(message, update, dataCache, chatId, controller);
    }
}
