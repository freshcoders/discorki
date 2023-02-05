package com.alistats.discorki.notification.result;

import java.net.URL;
import java.util.HashMap;

import com.alistats.discorki.notification.Notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Result {
    private Notification notification;
    private String title;
    private URL image;
    private URL thumbnail;
    private HashMap<String, Object> extraArguments = new HashMap<>();

    public void addExtraArgument(String key, Object value) {
        extraArguments.put(key, value);
    }
}
