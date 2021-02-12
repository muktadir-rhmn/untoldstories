package me.untoldstories.be.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String mapToJson(Map<String, Object> map) {
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
