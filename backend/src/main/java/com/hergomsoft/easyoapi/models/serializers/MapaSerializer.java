package com.hergomsoft.easyoapi.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class MapaSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] b, 
                          JsonGenerator jsonGenerator, 
                          SerializerProvider serializerProvider) 
                          throws IOException, JsonProcessingException {
        jsonGenerator.writeBoolean(b != null);
    }
}