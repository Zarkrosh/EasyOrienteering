package com.hergomsoft.easyoapi.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hergomsoft.easyoapi.models.Control;
import java.io.IOException;

public class ControlSoloCodigoSerializer extends JsonSerializer<Control> {

    @Override
    public void serialize(Control o, 
                          JsonGenerator jsonGenerator, 
                          SerializerProvider serializerProvider) 
                          throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(o.getCodigo());
    }
}