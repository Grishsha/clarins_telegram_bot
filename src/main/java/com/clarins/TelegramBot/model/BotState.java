package com.clarins.TelegramBot.model;

import lombok.Getter;

@Getter
public enum BotState {
    NOT_AUTHORISED("Пользователь не авторизован"),
    REG_FIRST_NAME("Введите ваше имя"),
    REG_SECOND_NAME("Введите ваше отчество"),
    REG_LAST_NAME("Введите вашу фамилию"),
    REG_EMAIL("Введите адрес вашей рабочий e-mail"),
    REG_DEPARTMENT("Введите ваше подразделенеие"),
    REG_PHONE("Введите номер вашего мобильного телефона"),
    REGISTRATION,       //-----------------------------------------

    EDIT_LAST_NAME("Введите вашу фамилию"),
    EDIT_FIRST_NAME("Введите ваше имя"),
    EDIT_SECOND_NAME("Введите ваше отчетство"),
    EDIT_EMAIL("Введите адрес вашей рабочий e-mail"),
    EDIT_DEPARTMENT("Введите ваше подразделение"),
    EDIT_PHONE("Введите номер вашего мобильного телефона"),
    CHANGE_USER_PROFILE("Хотите поменять что-то еще?"),

    NEW_INC,
    INC_DESCRIPTION("Введите описание вашей заявки"),
    INC_PHOTO("Добавьте фото к вашей заявке"),
    INC_SERVICE("Введите название IT-сервиса"),//Знаете ли вы название IT-сервиса?"),
    INC_RESP_GROUP("Введите название рабочей группы"),//Знаете ли вы название рабочей группы?"),
    INC_RESP_PERSON("Введите ФИО исполнителя"),//Знаете ли вы ФИО исполнителя?"),
    INC_FILLING,        //-----------------------------------------

    EDIT_INC_DESCRIPTION("Введите описание вашей заявки"),
    EDIT_INC_PHOTO("Добавьте фото к вашей заявке"),
    EDIT_INC_SERVICE("Введите название IT-сервиса"),
    EDIT_INC_RESP_GROUP("Введите название рабочей группы"),
    EDIT_INC_RESP_PERSON("Введите ФИО исполнителя"),
    CHANGE_INCIDENT_DATA("Хотите поменять что-то еще?"),

    INC_REQUEST_FILLED("Оформление заявки завершено"),
    USER_PROFILE_FILLED("Регистрация завершена"),
    SHOW_HELP_MENU("Выбирите один из пунктов меню."),
    SHOW_MAIN_MENU("Выбирите один из пунктов меню."),
    SHOW_USER_PROFILE("Выбирите один из пунктов меню.");

    private String description = null;

    BotState() {
    }

    BotState(String description) {
        this.description = description;
    }
}