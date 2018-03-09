package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import cyclops.control.Either;
import cyclops.control.Eval;
import cyclops.control.Option;

public class CyclopsSerializers extends Serializers.Base {


  @Override
  public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType type, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
    if (Option.class.isAssignableFrom(type.getRawClass())) {
      return new OptionSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }
    if (Eval.class.isAssignableFrom(type.getRawClass())) {
      return new EvalSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }

    return super.findReferenceSerializer(config, type, beanDesc, contentTypeSerializer, contentValueSerializer);
  }

  @Override
  public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    if (Either.class.isAssignableFrom(type.getRawClass())) {
      return new EitherSerializer();
    }
    return super.findSerializer(config, type, beanDesc);
  }
}
