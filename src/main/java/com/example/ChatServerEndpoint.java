package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/endpoint",
        encoders = CustomResponseEncoder.class,
        decoders = CustomResponseDecoder.class)
public class ChatServerEndpoint {

    ChatServerSingleton chatServerSingleton = ChatServerSingleton.getInstance();

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        CustomMessage temp = new CustomMessage();
        temp.setMessage("(Server): Welcome to the chat room. Please state your username to begin.");
        temp.setUsers(getUserList());
        session.getBasicRemote().sendObject(temp);
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        String userId = session.getId();
        if (chatServerSingleton.userNames.containsKey(userId)) {
            String userName = chatServerSingleton.userNames.get(userId);
            chatServerSingleton.userNames.remove(userId);
            for (Session peer : session.getOpenSessions()) {
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(Server): " + userName + " left the chat room.");
                temp.setUsers(getUserList());
                peer.getBasicRemote().sendObject(temp);
            }
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException, EncodeException {
        ObjectMapper mapper = new ObjectMapper();
        CustomMessage msg = mapper.readValue(message, CustomMessage.class);
        String userId = session.getId();
        if (chatServerSingleton.userNames.containsKey(userId)) {
            String userName = chatServerSingleton.userNames.get(userId);
            for (Session peer : session.getOpenSessions()) {
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(" + userName + "): " + msg.getMessage());
                temp.setUsers(getUserList());
                peer.getBasicRemote().sendObject(temp);
            }
        } else {
            if (chatServerSingleton.userNames.containsValue(msg.getMessage()) || msg.getMessage().equals("server")) {
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(Server): That username is already in use. Please try again.");
                temp.setUsers(getUserList());
                session.getBasicRemote().sendObject(temp);
            } else {
                chatServerSingleton.userNames.put(userId, msg.getMessage());
                CustomMessage temp = new CustomMessage();
                temp.setMessage("(Server): Welcome, " + msg.getMessage() + "!");
                temp.setUsers(getUserList());
                session.getBasicRemote().sendObject(temp);
                for (Session peer : session.getOpenSessions()) {
                    if (!peer.getId().equals(userId)) {
                        CustomMessage temp1 = new CustomMessage();
                        temp1.setMessage("(Server): " + msg.getMessage() + " joined the chat room.");
                        temp1.setUsers(getUserList());
                        peer.getBasicRemote().sendObject(temp1);
                    }
                }
            }
        }
    }

    private List<String> getUserList() {
        return new ArrayList<String>(chatServerSingleton.userNames.values());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: ");
        System.out.println(throwable.getMessage());
    }
}
