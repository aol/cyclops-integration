package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cyclops.data.tuple.Tuple3;
import cyclops.data.tuple.Tuple4;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Tuple4Serializer extends JsonSerializer<Tuple4<?,?,?,?>> {

  private static final long serialVersionUID = 1L;


  @AllArgsConstructor
  public static class T4 {
    @Getter
    @Setter
    private final Object first;
    @Getter
    @Setter
    private final Object second;
    @Getter
    @Setter
    private final Object third;
    @Getter
    @Setter
    private final Object fourth;
  }

  @Override
  public void serialize(Tuple4<?,?,?,?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {


      JsonSerializer<Object> ser = serializers.findValueSerializer(T4.class);
      ser.serialize(new T4(value._1(),value._2(),value._3(),value._4()), gen, serializers);

  }
}
