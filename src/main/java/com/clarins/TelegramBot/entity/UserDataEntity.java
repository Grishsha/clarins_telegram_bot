package com.clarins.TelegramBot.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.clarins.TelegramBot.model.UserProfileData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "user_data")
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class UserDataEntity {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long id;

    @Type(type = "jsonb")
    @Column(name = "user_profile_data", columnDefinition = "jsonb")
    private UserProfileData userProfileData;
}