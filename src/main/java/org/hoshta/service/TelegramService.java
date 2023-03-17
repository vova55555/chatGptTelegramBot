package org.hoshta.service;

import lombok.extern.slf4j.Slf4j;
import org.hoshta.sender.BotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This service allows to communicate with Telegram API
 */
@Slf4j
@Service
public class TelegramService {

    private final BotSender botSender;
    @Autowired
    public TelegramService(BotSender botSender) {
        this.botSender = botSender;
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(String.valueOf(chatId))
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logErrorAdnInformUser(e, chatId);
        }
    }

    public void forwardMessage(Long fromChatId, Long toChatId, Integer messageId) {
        ForwardMessage forwardMessage = ForwardMessage.builder()
                .fromChatId(fromChatId.toString())
                .chatId(toChatId.toString())
                .messageId(messageId)
                .build();
        try {
            botSender.execute(forwardMessage);
        } catch (TelegramApiException e) {
            logErrorAdnInformUser(e, toChatId);
        }
    }

    public void sendPhoto(Long chatId, InputFile photo, ReplyKeyboard replyKeyboard) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(String.valueOf(chatId))
                .photo(photo)
                .replyMarkup(replyKeyboard)
                .build();
        try {
            botSender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            logErrorAdnInformUser(e, chatId);
        }
    }

    public void logErrorAdnInformUser(Exception e, Long chatId) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);
        e.printStackTrace();
        sendMessage(chatId, errorMessage);
    }
}
