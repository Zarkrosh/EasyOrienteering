package com.hergomsoft.easyoapi.models.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Base64;

public class EmptyDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser jsonparser, DeserializationContext context) 
                          throws IOException, JsonProcessingException {
        
        return null;
    }
}