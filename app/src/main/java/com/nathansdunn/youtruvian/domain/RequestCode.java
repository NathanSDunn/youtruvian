package com.nathansdunn.youtruvian.domain;

public enum RequestCode {
    NONE(0),
    CAMERA_PERMS(1),
    TAKE_PHOTO(2),
    WRITE_SD_PERMS(3);

    private int value;
    RequestCode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
