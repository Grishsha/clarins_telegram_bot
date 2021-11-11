package com.clarins.TelegramBot.model;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class IncidentData {
    private String description;
    private String service;
    private String respGroup;
    private String respPerson;

    public void removeIncidentData() {
        description = null;
        service = null;
        respGroup = null;
        respPerson = null;
    }

    public String getDescription() {
        if (description == null)
            return "";
        else
            return description;
    }

    public String getService() {
        if (service == null)
            return "";
        else
            return service;
    }

    public String getRespGroup() {
        if (respGroup == null)
            return "";
        else
            return respGroup;
    }

    public String getRespPerson() {
        if (respPerson == null)
            return "";
        else
            return respPerson;
    }

    public String toString() {
        String resultString = "";

        resultString += String.format("Описание заявки: \n\n%s" +
                        "\n\nУслуга: %s\nОтветственная группа: %s" +
                        "\nИмя исполнителя: %s", getDescription(), getService()
                , getRespGroup(), getRespPerson());
        return resultString;
    }
}