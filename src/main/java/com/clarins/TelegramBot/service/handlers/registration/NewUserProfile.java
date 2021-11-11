package com.clarins.TelegramBot.service.handlers.registration;

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
import com.clarins.TelegramBot.utils.CheckFormat;

@Slf4j
public class NewUserProfile implements NewUserRegistrationInt {//HandleTextMessageByStage {
    private MainMenuButtons mainMenuButtons;
    private InlineMenuButtons inlineMenuButtons;
    private long chatId;
    private DataCache dataCache;
    private Update update;
    private SendMessage message;
    private Controller controller;

    public NewUserProfile(long chatId, DataCache dataCache, Update update, SendMessage message, Controller controller) {
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
            case NOT_AUTHORISED:
                message.setText("Для оформления заявки в техническую поддержку вам требуется пройти процедуру регистрации.\n" +
                        "\nОбратите внимание на то, что все поля обязательны для заполнения" +
                        "\nВведите вашу фамилию");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                dataCache.setUserCurrentBotState(chatId, BotState.REG_LAST_NAME);
                break;
            case REG_LAST_NAME:
                userProfileData.setLastName(update.getMessage().getText());
                if (update.getMessage().getFrom().getUserName() != null)
                    userProfileData.setUserName("@".concat(update.getMessage().getFrom().getUserName()));
                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText("Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/
                message.setText("Введите ваше имя");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.REG_FIRST_NAME);
                break;
            case REG_FIRST_NAME:
                userProfileData.setFirstName(update.getMessage().getText());
                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText("Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/
                message.setText("Введите ваше отчество");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.REG_SECOND_NAME);
                break;
            case REG_SECOND_NAME:
                userProfileData.setSecondName(update.getMessage().getText());

                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText(BotState.CHANGE_USER_PROFILE.getDescription());//"Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/

                message.setText(BotState.REG_EMAIL.getDescription());//"Введите ваш работчий e-mail");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.REG_EMAIL);
                break;
            case REG_EMAIL:
                if (!checkFormat.isCorrect(currentBotState, update.getMessage().getText())) {
                    message.setText("введенный вами текст не похож на адрес почты.\n" +
                            "Попробуйте еще раз.");
                    break;
                }
                userProfileData.setEMail(update.getMessage().getText());
                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText("Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/
                message.setText("Введите ваше подразделение в Интех");
                message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.REG_DEPARTMENT);
                break;
            case REG_DEPARTMENT:
                userProfileData.setDepartment(update.getMessage().getText());
                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText("Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/
                message.setText("Введите ваш контактный телефон.\n" +
                        "Потребуется для связи с вами если у поддержки возникнут вопросы");
                //message.setReplyMarkup(mainMenuButtons.getSimpleMenuKeyboard());
                message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(true));
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.REG_PHONE);
                break;
            case REG_PHONE:
                if (update.getMessage().hasText())
                    if (!checkFormat.isCorrect(currentBotState, update.getMessage().getText())) {
                        message.setText("введенный вами текст не похож на номер телефона.\n" +
                                "Попробуйте еще раз.");
                        message.setReplyMarkup(mainMenuButtons.getChangeMenuKeyboard(true));
                        break;
                    }
                if (update.getMessage().hasContact())
                    userProfileData.setPhone(update.getMessage().getContact().getPhoneNumber());
                else
                    userProfileData.setPhone(update.getMessage().getText());

                /*if (currentPhaseState == PhaseState.CHANGE_USER_PROFILE) {
                    message.setText("Хотите поменять что-то еще?");
                    message.setReplyMarkup(inlineMenuButtons.getInlineMessageButtons());
                    dataCache.setUserProfileData(chatId, userProfileData);
                    break;
                }*/
                message.setText("Процесс регистрации завершен. Спасибо.\n" +
                        "Теперь вы можете оформлять заявки в техническую поддержку.");
                message.setReplyMarkup(mainMenuButtons.getMainMenuKeyboard());
                saveUserProfileData(chatId, dataCache, controller, userProfileData, BotState.USER_PROFILE_FILLED);
                break;
            default:
                break;
        }

        return message;
    }

    private void saveUserProfileData(long chatId, DataCache dataCache, Controller controller, UserProfileData userProfileData, BotState nextState) {
        UserProfileDataRepository repository = controller.getRepository();
        dataCache.setUserProfileData(chatId, userProfileData);

        if (nextState == BotState.USER_PROFILE_FILLED) {
            UserDataEntity entity = new UserDataEntity();
            entity.setId(chatId);
            entity.setUserProfileData(userProfileData);

            if (repository.existsById(chatId)) {
                //log.error("delete from registration");
                repository.deleteById(chatId);
                repository.save(entity);
            } else
                repository.save(entity);
        }
        //log.info(userProfileData.toString());
        dataCache.setUserCurrentBotState(chatId, nextState);
    }
}
