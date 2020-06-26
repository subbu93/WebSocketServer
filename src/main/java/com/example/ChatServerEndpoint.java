package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ServerEndpoint(value = "/endpoint",
        encoders = CustomResponseEncoder.class,
        decoders = CustomResponseDecoder.class)
public class ChatServerEndpoint {

    ChatServerSingleton chatServerSingleton = ChatServerSingleton.getInstance();

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        chatServerSingleton.sessionIdSessionMap.put(session.getId(), session);
        CustomMessage temp = new CustomMessage();
        temp.setMessage("(Server): Welcome to the chat room. Please state your username to begin.");
        temp.setUsers(getUserList());
        temp.setCurrentUser(getCurrentUserName(session.getId()));
        session.getBasicRemote().sendObject(temp);
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        String userId = session.getId();
        chatServerSingleton.sessionIdSessionMap.remove(userId);
        if (chatServerSingleton.userSessionIdMap.containsKey(userId)) {
            String userName = chatServerSingleton.userSessionIdMap.get(userId);
            chatServerSingleton.userSessionIdMap.remove(userId);
            for (Session peer : session.getOpenSessions()) {
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(Server): " + userName + " left the chat room.");
                temp.setUsers(getUserList());
                temp.setCurrentUser(getCurrentUserName(peer.getId()));
                peer.getBasicRemote().sendObject(temp);
            }
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException, EncodeException {
        ObjectMapper mapper = new ObjectMapper();
        CustomMessage msg = mapper.readValue(message, CustomMessage.class);
        String userId = session.getId();
        if (msg.getSendMessageUser() != null) {
            sendPersonalMessage(msg, chatServerSingleton.userSessionIdMap.get(userId), session);
        } else {
            if (chatServerSingleton.userSessionIdMap.containsKey(userId)) {
                String userName = chatServerSingleton.userSessionIdMap.get(userId);
                for (Session peer : session.getOpenSessions()) {
                    CustomMessage temp = new CustomMessage();
                    temp.setMessage("(" + userName + "): " + msg.getMessage());
                    temp.setUsers(getUserList());
                    temp.setCurrentUser(getCurrentUserName(peer.getId()));
                    peer.getBasicRemote().sendObject(temp);
                }
            } else {
                if (chatServerSingleton.userSessionIdMap.containsValue(msg.getMessage()) || msg.getMessage().equals("server")) {
                    CustomMessage temp = new CustomMessage();
                    temp.setMessage("(Server): That username is already in use. Please try again.");
                    temp.setUsers(getUserList());
                    temp.setCurrentUser(getCurrentUserName(session.getId()));
                    session.getBasicRemote().sendObject(temp);
                } else {
                    chatServerSingleton.userSessionIdMap.put(userId, msg.getMessage());
                    CustomMessage temp = new CustomMessage();
                    temp.setMessage("(Server): Welcome, " + msg.getMessage() + "!");
                    temp.setUsers(getUserList());
                    temp.setCurrentUser(getCurrentUserName(session.getId()));
                    session.getBasicRemote().sendObject(temp);
                    for (Session peer : session.getOpenSessions()) {
                        if (!peer.getId().equals(userId)) {
                            CustomMessage temp1 = new CustomMessage();
                            temp1.setMessage("(Server): " + msg.getMessage() + " joined the chat room.");
                            temp1.setUsers(getUserList());
                            temp1.setCurrentUser(getCurrentUserName(peer.getId()));
                            peer.getBasicRemote().sendObject(temp1);
                        }
                    }
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: ");
        System.out.println(throwable.getMessage());
    }

    private List<String> getUserList() {
        return new ArrayList<String>(chatServerSingleton.userSessionIdMap.values());
    }

    private String getCurrentUserName(String sessionId) {
        return chatServerSingleton.userSessionIdMap.get(sessionId);
    }

    private void sendPersonalMessage(CustomMessage msg, String sender, Session session) throws IOException, EncodeException {
        for (Map.Entry<String, String> entry : chatServerSingleton.userSessionIdMap.entrySet()) {
            if (entry.getValue().equals(msg.getSendMessageUser())) {
                String userId = entry.getKey();
                Session receiveUserSession = chatServerSingleton.sessionIdSessionMap.get(userId);
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(" + sender + "): " + msg.getMessage());
                temp.setUsers(getUserList());
                temp.setCurrentUser(getCurrentUserName(chatServerSingleton.userSessionIdMap.get(userId)));
                receiveUserSession.getBasicRemote().sendObject(temp);
                session.getBasicRemote().sendObject(temp);
            }
        }
    }
}
