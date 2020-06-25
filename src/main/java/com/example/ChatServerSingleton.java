package com.example;

import java.util.HashMap;
import java.util.Map;

public class ChatServerSingleton {
    private static ChatServerSingleton instance = null;

    Map<String, String> userNames;

    private ChatServerSingleton() {
        userNames = new HashMap<String, String>();
    }

    public static ChatServerSingleton getInstance() {
        if (instance == null) {
            instance = new ChatServerSingleton();
        }
        return instance;
    }
}
