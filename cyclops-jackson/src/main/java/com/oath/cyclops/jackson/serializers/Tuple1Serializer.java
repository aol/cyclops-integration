package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.oath.cyclops.matching.Sealed2;
import com.oath.cyclops.util.ExceptionSoftener;
import cyclops.data.tuple.Tuple1;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Tuple1Serializer extends JsonSerializer<Tuple1<?>> {

  private static final long serialVersionUID = 1L;


  @AllArgsConstructor
  public static class T1 {
    @Getter
    @Setter
    private final Object first;
  }

  @Override
  public void serialize(Tuple1<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {


      JsonSerializer<Object> ser = serializers.findValueSerializer(T1.class);
      ser.serialize(new T1(value._1()), gen, serializers);

  }
}
