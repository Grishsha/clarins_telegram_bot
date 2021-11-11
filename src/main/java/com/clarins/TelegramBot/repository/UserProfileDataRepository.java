package com.clarins.TelegramBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clarins.TelegramBot.entity.UserDataEntity;

@Repository
public interface UserProfileDataRepository extends JpaRepository<UserDataEntity, Long> {
    /*@Query(value = "select id, curs_date, request_date, correlation_id, status from " +
                "public.curs_request where request_date = " +
                "(select max (request_date) from public.curs_request where curs_date = ?1)",
                nativeQuery = true)*/

    //UserProfileDataEntity findByChatId(long chatId);
    UserDataEntity findById(long chatId);
    //void Update(UserProfileDataEntity userProfileDataEntity);
    //void Save(UserProfileDataEntity userProfileDataEntity);
    //void Delete(long chatId);
}