package com.clarins.TelegramBot.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.controller.Controller;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.handlers.menu.InlineMenuButtons;
import com.clarins.TelegramBot.service.handlers.menu.MainMenuButtons;
import com.clarins.TelegramBot.service.impl.*;
import com.clarins.TelegramBot.utils.CurrentProcessPhase;
import com.clarins.TelegramBot.utils.DefineChatID;

@Slf4j
@Component
@NoArgsConstructor
public class ContextProcessor {

    public SendMessage process(DataCache dataCache, Update update, Controller controller) {
        long chatId = new DefineChatID().get(update);
        SendMessage message = new SendMessage();
        UserProfileDataRepository repository = controller.getRepository();
        MainMenuButtons mainMenuButtons = new MainMenuButtons();
        InlineMenuButtons inlineMenuButtons = new InlineMenuButtons();
        CurrentProcessPhase currentProcessPhase = new CurrentProcessPhase();
        BotState currentBotState = dataCache.getUserCurrentBotState(chatId);
        UserProfileData userProfileData = dataCache.getUserProfileData(chatId, repository);

        message.setChatId(String.valueOf(chatId));

        if (chatId == 0 || chatId < 0) return null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (update.getMessage().getText()) {
                case "/start":
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    if (userProfileData.getLastName().isEmpty() || userProfileData.getFirstName().isEmpty()
                            || userProfileData.getSecondName().isEmpty() || userProfileData.getEMail().isEmpty()
                            || userProfileData.getDepartment().isEmpty() || userProfileData.getPhone().isEmpty()) {
                        message.setText("?????? ???????????????????????? ?????? ?????? ?????????????????? Clarins.\n?? ???????????? ?????? ???????????????? ???????????? ?? ???????????? ????.");
                        currentBotState = BotState.NOT_AUTHORISED;
                    } else {
                        message.setText("???????????????????????? " + userProfileData.getFirstName() + " " + userProfileData.getSecondName() + "!");
                        currentBotState = BotState.SHOW_MAIN_MENU;
                    }
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "/new_incident":
                case "???????????????? ????????????":
                    if (userProfileData.getLastName().isEmpty() || userProfileData.getFirstName().isEmpty()
                            || userProfileData.getSecondName().isEmpty() || userProfileData.getEMail().isEmpty()
                            || userProfileData.getDepartment().isEmpty() || userProfileData.getPhone().isEmpty()) {
                        currentBotState = BotState.NOT_AUTHORISED;
                        //message = new ContextProcessor(chatId, dataCache, update, message, controller).process();
                    } else {
                        currentBotState = BotState.NEW_INC;
                        //message = new ContextProcessor(chatId, dataCache, update, message, controller).process();
                    }
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    break;//return message;
                case "/my_profile":
                case "?????? ?????????????????????????????? ????????????":
                    if (userProfileData.getLastName().isEmpty() || userProfileData.getFirstName().isEmpty()
                            || userProfileData.getSecondName().isEmpty() || userProfileData.getEMail().isEmpty()
                            || userProfileData.getDepartment().isEmpty() || userProfileData.getPhone().isEmpty()) {
                        currentBotState = BotState.NOT_AUTHORISED;
                        dataCache.setUserCurrentBotState(chatId, currentBotState);
                        break;
                        //message = new ContextProcessor(chatId, dataCache, update, message, controller).process();
                    } else {
                        String str = "??????????????: " + userProfileData.getLastName() +
                                "\n??????: " + userProfileData.getFirstName() +
                                "\n????????????????: " + userProfileData.getSecondName() +
                                "\ne-mail: " + userProfileData.getEMail() +
                                "\n??????????????????????????: " + userProfileData.getDepartment() +
                                "\n??????????????: " + userProfileData.getPhone();
                        message.setText(str);
                        message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(false));
                        currentBotState = BotState.SHOW_USER_PROFILE;
                        dataCache.setUserCurrentBotState(chatId, currentBotState);
                        return message;
                    }
                    //return message;
                case "???????????? ???????? ????????????":
                    message.setText("?? ?????? ?? ??????????????:" +
                            "\n????????????????????????????????\t5   ????????????" +
                            "\n?? ????????????\t\t\t10  ????????????" +
                            "\n??????????????????\t\t8   ????????????");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    return message;
                case "????????????????":
                    message.setText("?????? ???? ???????????? ?????????????????");
                    dataCache.setUserCurrentBotState(chatId, BotState.CHANGE_USER_PROFILE);
                    message.setReplyMarkup(inlineMenuButtons.getUserProfileButtons());
                    return message;
                case "????????????":
                    message.setText("?????????? ?????????? ?????????????? ???????????????? ????????, ?????? ???????????????? ???????????? ?? ??.??.");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    currentBotState = BotState.SHOW_HELP_MENU;
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "/main_menu":
                case "?? ?????????????? ????????":
                    message.setText("???? ???????????????????? ?? ?????????????? ????????.");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    currentBotState = BotState.SHOW_MAIN_MENU;
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "????????????":
                    /*message.setText("???????????? ???? ???? ???????????????? IT-???????????????\n" +
                            "?????????????? ???? ?????? ??????");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\n???????? ???? ???? ????????????, ???? ?????????????? ????????????????????"));
                    message.setReplyMarkup(inlineMenuButtons.getNextButton());
                    dataCache.setUserCurrentBotState(chatId, BotState.INC_SERVICE);
                    return message;
            }
        }

        InputMessage InputMessage = null;

        switch (currentProcessPhase.get(currentBotState)) {
            case REGISTRATION:
                InputMessage = new NewUserRegistration();
                break;
            case CHANGE_USER_PROFILE:
                InputMessage = new ChangeUserProfile();
                break;
            case INC_FILLING:
                InputMessage = new NewIncident();
                break;
            case CHANGE_INCIDENT_DATA:
                InputMessage = new ChangeIncident();
                break;
            case SHOW_HELP_MENU:
                InputMessage = new ShowHelpMenu();
                break;
            case SHOW_USER_PROFILE:
                InputMessage = new ShowUserProfile();
                break;
            case SHOW_MAIN_MENU:
                InputMessage = new ShowMainMenu();
                break;
        }
        return InputMessage.handle(chatId, dataCache, update, message, controller);
    }
}