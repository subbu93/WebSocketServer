package com.exanple;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/endpoint",
        encoders = CustomResponseEncoder.class,
        decoders = CustomResponseDecoder.class)

public class ChatServer {
    private Map<String, String> userNames = new HashMap<String, String>();

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendText("(Server): Welcome to the chat room. Please state your username to begin.");
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        String userId = session.getId();
        if(userNames.containsKey(userId)) {
            String userName = userNames.get(userId);
            userNames.remove(userId);
            for(Session peer: session.getOpenSessions()) {
                peer.getBasicRemote().sendText("(Server): " + userName + " left the chat room.");
            }
        }
    }

    @OnMessage
    public void handleMessage(CustomMessage message, Session session) throws IOException, EncodeException {
        String userId = session.getId();
        if(userNames.containsKey(userId)) {
            String userName = userNames.get(userId);
            for (Session peer : session.getOpenSessions())
                peer.getBasicRemote().sendText("(" + userName + "): " + message.getMessage());
        } else {
            if (userNames.containsValue(message.getMessage()) || message.getMessage().equals("server"))
                session.getBasicRemote().sendText("(Server): That username is already in use. Please try again.");
            else {
                userNames.put(userId, message.getMessage());
                session.getBasicRemote().sendText("(Server): Welcome, " + message + "!");
                for (Session peer : session.getOpenSessions())
                    if (!peer.getId().equals(userId))
                        peer.getBasicRemote().sendText("(Server): " + message + " joined the chat room.");
            }
        }
    }
}
