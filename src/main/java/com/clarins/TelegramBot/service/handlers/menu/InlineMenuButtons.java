package com.clarins.TelegramBot.service.handlers.menu;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class InlineMenuButtons {

    public InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
        buttonYes.setText("Да");
        InlineKeyboardButton buttonNo = new InlineKeyboardButton();
        buttonNo.setText("Нет");
        /*InlineKeyboardButton buttonIwillThink = new InlineKeyboardButton();
        buttonIwillThink.setText("Я подумаю");
        InlineKeyboardButton buttonIdontKnow = new InlineKeyboardButton();
        buttonIdontKnow.setText("Еще не определился");*/

        //Every button must have callBackData, or else not work !
        buttonYes.setCallbackData("buttonYes");
        buttonNo.setCallbackData("buttonNo");
        //buttonIwillThink.setCallbackData("buttonIwillThink");
        //buttonIdontKnow.setCallbackData("-");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonYes);
        keyboardButtonsRow1.add(buttonNo);

        /*List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonIwillThink);
        keyboardButtonsRow2.add(buttonIdontKnow);*/


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        //rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getNextButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonNext = new InlineKeyboardButton();
        buttonNext.setText("Пропустить");
        buttonNext.setCallbackData("buttonNext");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonNext);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getUserProfileButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();

        InlineKeyboardButton buttonLastName = new InlineKeyboardButton();
        buttonLastName.setText("Фамилия");
        buttonLastName.setCallbackData("buttonLastName");
        keyboardButtonsRow1.add(buttonLastName);

        InlineKeyboardButton buttonFirstName = new InlineKeyboardButton();
        buttonFirstName.setText("Имя");
        buttonFirstName.setCallbackData("buttonFirstName");
        keyboardButtonsRow2.add(buttonFirstName);

        InlineKeyboardButton buttonSecondName = new InlineKeyboardButton();
        buttonSecondName.setText("Отчество");
        buttonSecondName.setCallbackData("buttonSecondName");
        keyboardButtonsRow3.add(buttonSecondName);

        InlineKeyboardButton buttonEMail = new InlineKeyboardButton();
        buttonEMail.setText("e-mail");
        buttonEMail.setCallbackData("buttonEMail");
        keyboardButtonsRow1.add(buttonEMail);

        /*InlineKeyboardButton buttonDepartment = new InlineKeyboardButton();
        buttonDepartment.setText("Подразделение");
        buttonDepartment.setCallbackData("buttonDepartment");
        keyboardButtonsRow2.add(buttonDepartment);*/

        InlineKeyboardButton buttonDept = new InlineKeyboardButton();
        buttonDept.setText("Подразделение");
        buttonDept.setCallbackData("buttonDept");
        keyboardButtonsRow2.add(buttonDept);

        InlineKeyboardButton buttonPhone = new InlineKeyboardButton();
        buttonPhone.setText("Телефон");
        buttonPhone.setCallbackData("buttonPhone");
        keyboardButtonsRow3.add(buttonPhone);

        /*InlineKeyboardButton buttonDone = new InlineKeyboardButton();
        buttonDone.setText("Завершить редактирование");
        buttonDone.setCallbackData("buttonDone");
        keyboardButtonsRow4.add(buttonDone);*/

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        //rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
