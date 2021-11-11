package com.clarins.TelegramBot.controller;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.clarins.TelegramBot.cache.DataCache;
import com.clarins.TelegramBot.cache.UserDataCache;
import com.clarins.TelegramBot.model.UserAttachments;
import com.clarins.TelegramBot.repository.UserProfileDataRepository;
import com.clarins.TelegramBot.service.ContextProcessor;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Slf4j
@Component
@Configuration
@Getter
public class Controller extends TelegramLongPollingBot {
    @Autowired
    UserProfileDataRepository repository;
    @Value("${spring.mail.host}")
    private String mailHost;
    @Value("${spring.mail.port}")
    private String mailPort;
    @Value("${spring.mail.username}")
    private String mailUserName;
    @Value("${spring.mail.password}")
    private String mailPassword;
    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String mailProtocol;
    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String mailSslTrust;
    @Value("${spring.mail.properties.mail.debug}")
    private String mailDebug;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailSmtpAuth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailTtlsEnable;
    @Value("${spring.mail.sendto}")
    private String mailSendTo;
    @Value("${spring.mail.sendfrom}")
    private String mailSendFrom;
    @Value("${spring.mail.subject}")
    private String mailSubject;
    @Value("${telegram.bot.name}")
    private String botUserName;
    @Value("${telegram.bot.token}")
    private String botToken;
    private final DataCache dataCache;
    @Value("${support.group.enabled}")
    private String supportGroupEnabled;
    @Value("${support.group.chatid}")
    private String supportGroupChaId;
    @Value("${support.use.email.profile.sendfrom.enable}")
    private String supportUseEmailProfileSendfromEnable;
    @Value("${support.use.email.profile.sendto.enable}")
    private String supportUseEmailProfileSendtoEnable;
    private SendMessage message;

    public Controller() {
        dataCache = new UserDataCache();
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        ContextProcessor contextProcessor = new ContextProcessor();
        try {
            message = contextProcessor.process(dataCache, update, this);
        } catch (Exception e) {
            log.error("Exception while sending message {} to user: {}", message, e.getMessage());
        }

        if (message != null) {
            try {
                UserAttachments userAttachments = dataCache.getUserFilesData(Long.parseLong(message.getChatId()));
                SendMessage sm = new SendMessage();
                Random rnd = new Random();
                int rndNumber = rnd.nextInt(200);

                if (message.getText().startsWith("Описание заявки: ")) {

                    if (Objects.equals(getSupportGroupEnabled(), "true") && !getSupportGroupChaId().isEmpty()) {
                        if (userAttachments.getCount() > 0) {
                            sm.setChatId(getSupportGroupChaId());
                            sm.setText(String.format("-----------------------------------" +
                                    "\nСформирована новая заявка № %d" +
                                    "\n-----------------------------------" +
                                    "\nфотографии приложенные к заявке", rndNumber));
                            execute(sm);
                            sendPhoto(userAttachments, Long.parseLong(getSupportGroupChaId()));
                            sm.setText(message.getText());
                            execute(sm);
                        } else {
                            sm.setChatId(getSupportGroupChaId());
                            sm.setText(String.format("-----------------------------------" +
                                    "\nСформирована новая заявка № %d" +
                                    "\n-----------------------------------", rndNumber));
                            execute(sm);
                            sendPhoto(userAttachments, Long.parseLong(getSupportGroupChaId()));
                            sm.setText(message.getText());
                            execute(sm);
                        }
                    }

                    if (userAttachments.getCount() > 0) {
                        sm.setChatId(message.getChatId());
                        sm.setText(String.format("-----------------------------------" +
                                "\nСформирована новая заявка № %d" +
                                "\n-----------------------------------" +
                                "\nфотографии приложенные к заявке", rndNumber));
                        execute(sm);
                        sendPhoto(userAttachments, Long.parseLong(message.getChatId()));
                        userAttachments.removeAllPaths();
                    } else {
                        sm.setChatId(message.getChatId());
                        sm.setText(String.format("-----------------------------------" +
                                "\nСформирована новая заявка № %d" +
                                "\n-----------------------------------", rndNumber));
                        execute(sm);
                        sendPhoto(userAttachments, Long.parseLong(message.getChatId()));
                        userAttachments.removeAllPaths();
                    }
                }
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Exception while sending message {} to user: {}", message, e.getMessage());
            }
        }
    }

    //@SneakyThrows
    public void/*String*/ saveFilePathAndFileId(Update update) throws TelegramApiException, NullPointerException{
        // Check that the update contains a message and the message has a photo
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            UserAttachments userAttachments = dataCache.getUserFilesData(update.getMessage().getChatId());

            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);

            /*String fp = photo.getFilePath();
            if (fp != null) {
                return fp;
            } else {
            String fff = photo.getFileId();*/

            GetFile getFileMethod = new GetFile();
            try {
                getFileMethod.setFileId(photo.getFileId());
                // We execute the method using AbsSender::execute method.
                File file = execute(getFileMethod);
                userAttachments.addNewAttachmentsFileId(photo.getFileId());
                userAttachments.addNewAttachmentPath(file.getFilePath());
                dataCache.setUserAttachments(update.getMessage().getChatId(), userAttachments);
                // We now have the file_path
                //return file.getFilePath();
            } catch (TelegramApiException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        // Return null if not found
        //return null;
    }

    @SneakyThrows
    public void sendEmail(long chatId, String message, UserAttachments userAttachments) throws MessagingException, TelegramApiException {
        List<String> attPath;
        int count = 1;
        String fileName = "File";
        String filePath = "";
        JavaMailSender emailSender = getJavaMailSender();
        final MimeMessage mimeMessage = emailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(getMailSubject());
        if (Objects.equals(getSupportUseEmailProfileSendfromEnable(), "true"))
            helper.setFrom(dataCache.getUserProfileData(chatId, repository).getEMail());
        else
            helper.setFrom(getMailSendFrom());
        if (Objects.equals(getSupportUseEmailProfileSendtoEnable(), "true"))
            helper.setTo(dataCache.getUserProfileData(chatId, repository).getEMail());
        else
            helper.setTo(getMailSendTo());
        helper.setText(message, false);

        if (userAttachments.getAttachmentsPath() != null) {
            attPath = userAttachments.getAttachmentsPath();
            for (String path : attPath) {
                fileName = "Attachment_";
                fileName += count + ".jpg";
                filePath = path;
                helper.addAttachment(fileName, downloadFile(filePath));
                count++;
            }
            //userAttachments.removeAllPaths();
        }
        emailSender.send(mimeMessage);
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(getMailHost());
        mailSender.setPort(Integer.parseInt(getMailPort()));
        mailSender.setUsername(getMailUserName());
        mailSender.setPassword(getMailPassword());
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.ssl.trust", getMailSslTrust());
        props.put("mail.transport.protocol", getMailProtocol());
        props.put("mail.smtp.auth", getMailSmtpAuth());
        props.put("mail.smtp.starttls.enable", getMailTtlsEnable());
        props.put("mail.debug", getMailDebug());

        return mailSender;
    }

    /*public void setUserProfileToRepository(long chatId, UserProfileData userProfileData){
        UserDataEntity entity;
        entity = new UserDataEntity();
        entity.setId(chatId);
        entity.setUserProfileData(userProfileData);
        try {
            repository.deleteById(chatId);
            repository.saveAndFlush(entity);
        } catch (Exception e){
            log.error("Exception while sending message {} to user: {}", message, e.getMessage());
        }
    }*/

    /*private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }*/

    @SneakyThrows
    public void sendPhoto(UserAttachments userAttachments, long chaId) {
        //UserAttachments userAttachments
        String capt = "";
        int count = 1;
        //log.error(String.valueOf(userAttachments.getCount()));
        if (userAttachments.getCount() > 0) {
            List<String> attPath = userAttachments.getAttachmentsFileId();
            SendPhoto photo = new SendPhoto();//.setChatId(message.getChatId()).setPhoto(file_id);
            //log.error("inside photo");
            for (String file_id : attPath) {
                //log.error(file_id);
                photo.setPhoto(new InputFile(file_id));
                photo.setChatId(String.valueOf(chaId));
                //log.error(photo.getChatId());
                capt = "photo_";
                capt += count;
                capt += ".jpg";
                photo.setCaption(capt);
                //log.error(file_id);
                try {
                    execute(photo);
                } catch (Exception e) {
                    log.error("SendPhoto Error: Exception while sending message {} to user: {}", photo, e.getMessage());
                }

                count++;
            }
        }
    }

/*
        @SneakyThrows
        public void sendDocument ( long chatId, String caption, File sendFile){
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setCaption(caption);
        sendDocument.setDocument(sendFile);
        execute(sendDocument);
    }*/
}
