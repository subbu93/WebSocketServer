package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class CustomResponseEncoder implements Encoder.Text<CustomMessage> {

    private ObjectMapper mapper = new ObjectMapper();

    public String encode(CustomMessage customMessage) throws EncodeException {
        String returnVal = "";
        try {
            returnVal = mapper.writeValueAsString(customMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    public void init(EndpointConfig endpointConfig) {

    }

    public void destroy() {

    }
}
