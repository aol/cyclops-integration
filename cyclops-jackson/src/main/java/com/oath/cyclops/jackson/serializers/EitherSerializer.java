package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import cyclops.control.Either;
import cyclops.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class EitherSerializer extends JsonSerializer<Either<?,?>> {

  private static final long serialVersionUID = 1L;


  @AllArgsConstructor
  private static class LeftBean {
    @Getter
    @Setter
    private final Object left;
  }
  @AllArgsConstructor
  private static class RightBean {
    @Getter @Setter
    private final Object right;

  }
  @Override
  public void serialize(Either<?, ?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    if(value.isLeft()) {
      JsonSerializer<Object> ser = serializers.findValueSerializer(LeftBean.class);
      ser.serialize(new LeftBean(value.leftOrElse(null)), gen, serializers);
    }
    else {
      JsonSerializer<Object> ser = serializers.findValueSerializer(RightBean.class);
        ser.serialize(new RightBean(value.orElse(null)), gen, serializers);
    }

  }
}
