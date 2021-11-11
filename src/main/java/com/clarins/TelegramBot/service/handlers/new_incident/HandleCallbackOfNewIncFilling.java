package com.clarins.TelegramBot.service.handlers.new_incident;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.handlers.menu.InlineMenuButtons;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;
import com.clarins.TelegramBot.utils.TTRequestFilledHandler;

@Slf4j
@NoArgsConstructor
public class HandleCallbackOfNewIncFilling implements CreateIncidentInt {//HandleMessageByType {
    private long chatId;
    private DataCache dataCache;
    private Update update;
    private SendMessage message;
    private Controller controller;

    public HandleCallbackOfNewIncFilling(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
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
        //PhaseState currentPhaseState = dataCache.getUserCurrentPhaseState(chatId);
        //UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        UserAttachments userAttachments = dataCache.getUserFilesData(chatId);
        MainMenuButtons mainMenuButtons = new MainMenuButtons();
        InlineMenuButtons inlineMenuButtons = new InlineMenuButtons();

        //if (currentPhaseState == PhaseState.DEFAULT) {
        switch (currentBotState) {
            //---------------------------------------------------------------------------------------------------
            case NEW_INC:
                break;
            case INC_DESCRIPTION:
                if (update.getCallbackQuery().getData().equals("buttonYes")) {
                    if (userAttachments.getCount() == 0) {
                        message.setText("Загрузите фото. \n\nПо окончанию загрузки нажмите кнопку ГОТОВО или введите любой текст и нажмите отправить");
                        dataCache.setUserCurrentBotState(chatId, BotState.INC_PHOTO);
                        message.setReplyMarkup(mainMenuButtons.getDoneKeyboard());
                    } else {
                        message.setText(BotState.INC_SERVICE.getDescription());//"Введите название IT-сервиса");
                        //message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                        message.setReplyMarkup(inlineMenuButtons.getNextButton());
                        dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                    }
                }
                if (update.getCallbackQuery().getData().equals("buttonNo")) {
                    /*message.setText("Знаете ли вы название IT-сервиса?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_PHOTO);*/
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);

                }
                break;
            case INC_PHOTO:
                /*if (update.getCallbackQuery().getData().equals("buttonYes")) {
                    message.setText(BotState.INC_SERVICE.getDescription());//"Введите название IT-сервиса");
                    message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                }
                if (update.getCallbackQuery().getData().equals("buttonNo")) {
                    message.setText("Знаете ли вы название рабочей группы?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                }*/
                if (update.getCallbackQuery().getData().equals("buttonNext")) {
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                }
                break;
            case INC_SERVICE:
                /*if (update.getCallbackQuery().getData().equals("buttonYes")) {
                    message.setText(BotState.INC_RESP_GROUP.getDescription());//"Введите название рабочей группы");
                    message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_GROUP);
                }
                if (update.getCallbackQuery().getData().equals("buttonNo")) {
                    message.setText("Знаете ли вы ФИО исполнителя?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_GROUP);
                }*/
                if (update.getCallbackQuery().getData().equals("buttonNext")) {
                    message.setText(BotState.INC_RESP_GROUP.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_GROUP);
                }
                break;
            case INC_RESP_GROUP:
                /*if (update.getCallbackQuery().getData().equals("buttonYes")) {
                    message.setText(BotState.INC_RESP_PERSON.getDescription());//"Введите ФИО исполнителя");
                    message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_PERSON);
                }
                if (update.getCallbackQuery().getData().equals("buttonNo")) {
                    message.setText("Ваша заявка оформлена.");
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_REQUEST_FILLED);
                }*/
                if (update.getCallbackQuery().getData().equals("buttonNext")) {
                    message.setText(BotState.INC_RESP_PERSON.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_RESP_PERSON);
                }
                break;
            case INC_RESP_PERSON:
                if (update.getCallbackQuery().getData().equals("buttonNext")) {
                    message.setText(BotState.INC_REQUEST_FILLED.getDescription());
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_REQUEST_FILLED);
                }
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
