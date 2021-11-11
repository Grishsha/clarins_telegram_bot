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
                        message.setText("Вас приветствует чат бот поддержки Clarins.\nЯ помогу вам оформить заявку в службу ИТ.");
                        currentBotState = BotState.NOT_AUTHORISED;
                    } else {
                        message.setText("Здравствуйте " + userProfileData.getFirstName() + " " + userProfileData.getSecondName() + "!");
                        currentBotState = BotState.SHOW_MAIN_MENU;
                    }
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "/new_incident":
                case "Оформить заявку":
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
                case "Мои регистрационные данные":
                    if (userProfileData.getLastName().isEmpty() || userProfileData.getFirstName().isEmpty()
                            || userProfileData.getSecondName().isEmpty() || userProfileData.getEMail().isEmpty()
                            || userProfileData.getDepartment().isEmpty() || userProfileData.getPhone().isEmpty()) {
                        currentBotState = BotState.NOT_AUTHORISED;
                        dataCache.setUserCurrentBotState(chatId, currentBotState);
                        break;
                        //message = new ContextProcessor(chatId, dataCache, update, message, controller).process();
                    } else {
                        String str = "Фамилия: " + userProfileData.getLastName() +
                                "\nИмя: " + userProfileData.getFirstName() +
                                "\nОтчество: " + userProfileData.getSecondName() +
                                "\ne-mail: " + userProfileData.getEMail() +
                                "\nПодразделение: " + userProfileData.getDepartment() +
                                "\nТелефон: " + userProfileData.getPhone();
                        message.setText(str);
                        message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(false));
                        currentBotState = BotState.SHOW_USER_PROFILE;
                        dataCache.setUserCurrentBotState(chatId, currentBotState);
                        return message;
                    }
                    //return message;
                case "Статус моих заявок":
                    message.setText("У вас в статусе:" +
                            "\nЗАРЕГИСТРИРОВАНО\t5   заявок" +
                            "\nВ РАБОТЕ\t\t\t10  заявок" +
                            "\nВЫПОЛНЕНО\t\t8   заявок");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    return message;
                case "Изменить":
                    message.setText("Что вы хотите изменить?");
                    dataCache.setUserCurrentBotState(chatId, BotState.CHANGE_USER_PROFILE);
                    message.setReplyMarkup(inlineMenuButtons.getUserProfileButtons());
                    return message;
                case "Помощь":
                    message.setText("Здесь будет краткое описание того, как оформить заявку и т.д.");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    currentBotState = BotState.SHOW_HELP_MENU;
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "/main_menu":
                case "В Главное Меню":
                    message.setText("Вы находитесь в главном меню.");
                    message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                    currentBotState = BotState.SHOW_MAIN_MENU;
                    dataCache.setUserCurrentBotState(chatId, currentBotState);
                    return message;
                case "Готово":
                    /*message.setText("Знаете ли вы название IT-сервиса?\n" +
                            "нажмите ДА или НЕТ");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());*/
                    message.setText(BotState.INC_SERVICE.getDescription().concat("" +
                            "\n\nЕсли вы не знаете, то нажмите ПРОПУСТИТЬ"));
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