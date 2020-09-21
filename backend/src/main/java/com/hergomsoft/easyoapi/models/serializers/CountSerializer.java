package com.hergomsoft.easyoapi.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;

public class CountSerializer extends JsonSerializer<List> {

    @Override
    public void serialize(List o, 
                          JsonGenerator jsonGenerator, 
                          SerializerProvider serializerProvider) 
                          throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(o.size());
    }
}