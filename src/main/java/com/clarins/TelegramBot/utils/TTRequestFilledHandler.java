package com.clarins.TelegramBot.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.IncidentData;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;

@Slf4j
public class TTRequestFilledHandler {
    private long chatId;
    private DataCache dataCache;
    private SendMessage message;
    private Controller controller;

    public TTRequestFilledHandler(long chatId, DataCache dataCache, SendMessage message, Controller controller) {
        this.chatId = chatId;
        this.dataCache = dataCache;
        this.message = message;
        this.controller = controller;
    }

    public SendMessage handle() {
        MainMenuButtons mainMenuButtons = new MainMenuButtons();
        UserProfileDataRepository repository = controller.getRepository();
        UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);
        UserAttachments userAttachments = dataCache.getUserFilesData(chatId);
        IncidentData incidentData = dataCache.getIncidentData(chatId);

        String str = incidentData.toString();
        str += userProfileData.toString();

        try {
            controller.sendEmail(chatId, str, userAttachments);
            str += "\n\nОтправлена в ИТ.";
        } catch (Exception e) {// MessagingException | TelegramApiException e){
            str += "\n\nНе отправлена в ИТ." +
                    "\nВ процессе отправки произошла ошибка:\n\n";
            str += e;
        }

        message.setText(str);
        message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());

        dataCache.setUserCurrentBotState(chatId, BotState.SHOW_MAIN_MENU);
        //userAttachments.removeAllPaths();
        incidentData.removeIncidentData();
        return message;
    }
}
