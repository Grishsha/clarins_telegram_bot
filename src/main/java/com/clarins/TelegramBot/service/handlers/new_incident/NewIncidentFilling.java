package com.clarins.TelegramBot.service.handlers.new_incident;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.IncidentData;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.service.handlers.menu.InlineMenuButtons;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;
import com.clarins.TelegramBot.utils.TTRequestFilledHandler;

@Slf4j
public class NewIncidentFilling implements CreateIncidentInt {//HandleTextMessageByStage {
    private BotState currentBotState;
    private MainMenuButtons mainMenuButtons;
    private UserProfileData userProfileData;
    private IncidentData incidentData;
    private UserAttachments userAttachments;
    private InlineMenuButtons inlineMenuButtons;
    private TTRequestFilledHandler requestFilledHandler;
    private long chatId;
    private DataCache dataCache;
    private Update update;
    private SendMessage message;
    private Controller controller;

    public NewIncidentFilling(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
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
        //UserProfileDataRepository repository = controller.getRepository();
        //UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        userAttachments = dataCache.getUserFilesData(chatId);
        currentBotState = dataCache.getUserCurrentBotState(chatId);
        incidentData = dataCache.getIncidentData(chatId);

        switch (currentBotState) {
            //---------------------------------------------------------------------------------------------------
            case NEW_INC:
                message.setText("Приступим к оформлению заявки.\n\n" +
                        "Пожалуйста, введите описание вашей заявки. Обратите внимание на то, что описание может содержать " +
                        "текст и фотографии (картинки).");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                dataCache.setUserCurrentBotState(chatId, BotState.INC_DESCRIPTION);
                break;
            case INC_DESCRIPTION:
                incidentData.setDescription(update.getMessage().getText());
                dataCache.setIncidentData(chatId, incidentData);

                if (userAttachments.getCount() == 0) {
                    /*message.setText("Хотите к описанию добавить фото?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                    message.setText(BotState.INC_PHOTO.getDescription().concat("" +
                            "\n\nЛибо нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_PHOTO);
                } else {
                    /*message.setText("Знаете ли вы название IT-сервиса?\n" +
                            "нажмите ДА или НЕТ");
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_PHOTO);*/
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                }
                //message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                break;
            case INC_PHOTO:
                if (userAttachments.getCount() > 0) {
                    /*message.setText("Знаете ли вы название IT-сервиса?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                }
                break;
            case INC_SERVICE:
                incidentData.setService(update.getMessage().getText());
                dataCache.setIncidentData(chatId, incidentData);
                /*message.setText("Знаете ли вы название Рабочей группы?\n" +
                        "нажмите ДА или НЕТ");
                message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                message.setText(BotState.INC_RESP_GROUP.getDescription().concat("" +
                        "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                message.setReplyMarkup(inlineMenuButtons.getNextButton());
                dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_GROUP);
                break;
            case INC_RESP_GROUP:
                incidentData.setRespGroup(update.getMessage().getText());
                dataCache.setIncidentData(chatId, incidentData);
                /*message.setText("Знаете ли вы ФИО исполнителя?\n" +
                        "нажмите ДА или НЕТ");
                message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                message.setText(BotState.INC_RESP_PERSON.getDescription().concat("" +
                        "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                message.setReplyMarkup(inlineMenuButtons.getNextButton());
                dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_PERSON);
                break;
            case INC_RESP_PERSON:
                incidentData.setRespPerson(update.getMessage().getText());
                dataCache.setIncidentData(chatId, incidentData);
                message.setText("Ваша заявка оформлена.");
                message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                dataCache.setUserCurrentBotState(chatId, BotState.INC_REQUEST_FILLED);
                break;
            default:
                break;
        }

        if (dataCache.getUserCurrentBotState(chatId) == BotState.INC_REQUEST_FILLED) {
            requestFilledHandler = new TTRequestFilledHandler(chatId, dataCache, message, controller);
            requestFilledHandler.handle();
        }

        return message;
    }
}