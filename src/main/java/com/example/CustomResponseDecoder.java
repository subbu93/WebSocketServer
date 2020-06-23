package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CustomResponseDecoder implements Decoder.Text<CustomMessage> {

    private ObjectMapper mapper = new ObjectMapper();

    public CustomMessage decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, CustomMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CustomMessage();
    }

    public boolean willDecode(String s) {
        return false;
    }

    public void init(EndpointConfig endpointConfig) {

    }

    public void destroy() {

    }
}
