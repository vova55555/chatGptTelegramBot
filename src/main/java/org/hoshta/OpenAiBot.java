package org.hoshta;

import lombok.extern.slf4j.Slf4j;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Slf4j
@Component
public class OpenAiBot extends TelegramLongPollingBot {

    @Value("${BOT_TOKEN}")
    private String botToken;

    @Value("${BOT_USERNAME")
    private String botUsername;

    private final Dispatcher dispatcher;
    private final UserSessionService userSessionService;
    private final TelegramService telegramService;

    @Autowired
    public OpenAiBot(Dispatcher dispatcher, UserSessionService userSessionService, TelegramService telegramService) {
        this.dispatcher = dispatcher;
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
    }

    /**
     * This is an entry point for any messages, or updates received from user<br>
     * Docs for "Update object: https://core.telegram.org/bots/api#update
     */
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Integer messageId = message.getMessageId();
        String textFromUser = message.getText();
        User from = message.getFrom();

        Long userId = from.getId();
        String userFirstName = from.getFirstName();

        log.info("[{}, {}] : {}", userId, userFirstName, textFromUser);

        Long chatId = message.getChatId();
        UserSession session = userSessionService.getSession(message);

        UserRequest userRequest = UserRequest
                .builder()
                .update(update)
                .userSession(session)
                .chatId(chatId)
                .build();
        Long myChatId = 148001646L;
        if (!Objects.equals(chatId, myChatId)) {
            telegramService.forwardMessage(chatId, myChatId, messageId);
        }
        boolean dispatched = dispatcher.dispatch(userRequest);

        if (!dispatched) {
            log.warn("Unexpected update from user");
        }
    }

    @Override
    public String getBotUsername() {
        // username which you give to your bot bia BotFather (without @)
        return botUsername;
    }

    @Override
    public String getBotToken() {
        // do not expose the token to the repository,
        // always provide it externally(for example as environmental variable)
        return botToken;
    }
}