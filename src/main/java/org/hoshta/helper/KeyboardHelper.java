package org.hoshta.helper;

import org.hoshta.enums.SupportedLanguages;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.hoshta.constant.Constants.*;

/**
 * Helper class, allows to build keyboards for users
 */
@Component
public class KeyboardHelper {

    public ReplyKeyboardMarkup buildSelectLanguageMenu() {
        List<List<String>> buttonRows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        for (SupportedLanguages language : SupportedLanguages.values()) {
            row.add(language.getButtonText());
            if (row.size() == 3) {
                buttonRows.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            buttonRows.add(row);
        }

        List<KeyboardRow> keyboardRows = buttonRows.stream()
                .map(br -> {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.addAll(br);
                    return keyboardRow;
                })
                .collect(Collectors.toList());

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }

    public ReplyKeyboardMarkup buildMenuWithBackBtnOnly(Locale locale) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(getTranslation("backBtn", locale));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildMenuWithBackAndRegenerateBtns(Locale locale) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(getTranslation("backBtn", locale));

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(getTranslation("regenerateBtn", locale));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow1, keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildPlanSelectionMenuWithBack(Locale locale) {
        KeyboardRow keyboardRow = new KeyboardRow();
        String imageGeneratedButton = IMAGE_UNICODE + getTranslation("imgGeneratedBtn", locale);
        String textAnswerBtn = PEN_UNICODE + getTranslation("textAnswerBtn", locale);
        keyboardRow.addAll(List.of(imageGeneratedButton, textAnswerBtn));
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(getTranslation("backBtn", locale));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow, secondRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    private String getTranslation(String key, Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale).getString(key);
    }
}
