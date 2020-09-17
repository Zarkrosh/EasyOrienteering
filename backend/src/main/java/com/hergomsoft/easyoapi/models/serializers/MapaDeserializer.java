package com.hergomsoft.easyoapi.models.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Base64;

public class MapaDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser jsonparser, DeserializationContext context) 
                          throws IOException, JsonProcessingException {
        
        String b64 = jsonparser.getText().split("base64,")[1];
        return Base64.getDecoder().decode(b64);
    }
}