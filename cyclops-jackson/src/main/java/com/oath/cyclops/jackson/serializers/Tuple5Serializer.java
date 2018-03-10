package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cyclops.data.tuple.Tuple4;
import cyclops.data.tuple.Tuple5;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Tuple5Serializer extends JsonSerializer<Tuple5<?,?,?,?,?>> {

  private static final long serialVersionUID = 1L;


  @AllArgsConstructor
  public static class T5 {
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
    @Getter
    @Setter
    private final Object fifth;
  }

  @Override
  public void serialize(Tuple5<?,?,?,?,?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {


      JsonSerializer<Object> ser = serializers.findValueSerializer(T5.class);
      ser.serialize(new T5(value._1(),value._2(),value._3(),value._4(),value._5()), gen, serializers);

  }
}
