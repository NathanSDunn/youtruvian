package com.nathansdunn.blendify.domain;

public enum RequestCode {
    NONE(0),
    CAMERA_PERMS(1),
    TAKE_PHOTO(2);

    private int value;
    RequestCode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
