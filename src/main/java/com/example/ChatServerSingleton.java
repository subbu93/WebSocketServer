package com.example;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class ChatServerSingleton {
    private static ChatServerSingleton instance = null;

    Map<String, String> userSessionIdMap;
    Map<String, Session> sessionIdSessionMap;

    private ChatServerSingleton() {
        userSessionIdMap = new HashMap<>();
        sessionIdSessionMap = new HashMap<>();
    }

    public static ChatServerSingleton getInstance() {
        if (instance == null) {
            instance = new ChatServerSingleton();
        }
        return instance;
    }
}
