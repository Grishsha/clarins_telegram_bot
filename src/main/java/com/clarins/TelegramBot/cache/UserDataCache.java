package com.clarins.TelegramBot.cache;

import lombok.NoArgsConstructor;
import com.clarins.TelegramBot.entity.UserDataEntity;
import com.clarins.TelegramBot.model.BotState;
import com.clarins.TelegramBot.model.IncidentData;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.model.UserProfileData;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;

import java.util.HashMap;
import java.util.Map;

//@Component
@NoArgsConstructor
public class UserDataCache implements DataCache {
    UserProfileDataRepository repository;
    private Map<Long, BotState> botStateMap = new HashMap<>();
    private Map<Long, UserProfileData> userProfileDataMap = new HashMap<>();
    private Map<Long, UserAttachments> userAttachmentsMap = new HashMap<>();
    private Map<Long, IncidentData> incidentDataMap = new HashMap<>();

    @Override
    public void setUserCurrentBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
    }

    @Override
    public BotState getUserCurrentBotState(long userId) {
        BotState botState = botStateMap.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }
        return botState;
        //hibernate.
    }

    @Override
    public UserProfileData getUserProfileData(long userId, UserProfileDataRepository repository) {
        UserProfileData userProfileData = userProfileDataMap.get(userId);
        if (userProfileData == null) {
            UserDataEntity entity;
            if (repository.existsById(userId)) {
                entity = repository.findById(userId);
                return entity.getUserProfileData();
            } else
                userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    @Override
    public IncidentData getIncidentData(long userId) {
        IncidentData incidentData = incidentDataMap.get(userId);
        if (incidentData == null) {
            incidentData = new IncidentData();
        }
        return incidentData;
    }

    @Override
    public UserAttachments getUserFilesData(long userId) {
        UserAttachments userAttachments = userAttachmentsMap.get(userId);
        if (userAttachments == null) {
            userAttachments = new UserAttachments();
        }
        return userAttachments;
    }

    @Override
    public void setIncidentData(long userId, IncidentData incidentData) {
        incidentDataMap.put(userId, incidentData);
    }

    @Override
    public void setUserProfileData(long userId, UserProfileData userProfileData) {
        userProfileDataMap.put(userId, userProfileData);
        //}
    }

    @Override
    public void setUserAttachments(long userId, UserAttachments userAttachments) {
        userAttachmentsMap.put(userId, userAttachments);
    }


    /*    @Override
    public void saveUserProfileData(int userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }*/
}
