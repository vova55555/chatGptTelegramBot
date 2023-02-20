package org.hoshta.enums;

public enum SupportedLanguages {
    UK("\uD83C\uDDFA\uD83C\uDDE6Українська"),
    EN("\uD83C\uDDEC\uD83C\uDDE7English"),
    ES("\uD83C\uDDEA\uD83C\uDDF8Español"),
    FR("\uD83C\uDDEB\uD83C\uDDF7Francés"),
    PL("\uD83C\uDDF5\uD83C\uDDF1Polski");

    private String buttonText;

    SupportedLanguages(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonText() {
        return buttonText;
    }

}

