package com.clarins.TelegramBot.cache;

import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.IncidentData;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;

public interface DataCache {
    void setUserCurrentBotState(long userId, BotState botState);

    void setUserProfileData(long userId, UserProfileData userProfileData);

    void setIncidentData(long userId, IncidentData incidentData);

    void setUserAttachments(long userId, UserAttachments userAttachments);

    BotState getUserCurrentBotState(long userId);

    UserProfileData getUserProfileData(long userId, UserProfileDataRepository repository);

    IncidentData getIncidentData(long userId);

    UserAttachments getUserFilesData(long userId);
    //void saveUserProfileData(int userId, UserProfileData userProfileData);
}
