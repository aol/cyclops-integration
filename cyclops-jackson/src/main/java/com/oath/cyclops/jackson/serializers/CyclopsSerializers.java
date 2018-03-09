package com.oath.cyclops.jackson.serializers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.oath.cyclops.matching.Sealed2;
import com.oath.cyclops.matching.Sealed3;
import com.oath.cyclops.matching.Sealed4;
import com.oath.cyclops.matching.Sealed5;
import com.oath.cyclops.types.Value;
import cyclops.control.*;

public class CyclopsSerializers extends Serializers.Base {


  @Override
  public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType type, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
    if (Option.class.isAssignableFrom(type.getRawClass())) {
      return new OptionSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }
    if (Eval.class.isAssignableFrom(type.getRawClass())) {
      return new EvalSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }
    if (Trampoline.class.isAssignableFrom(type.getRawClass())) {
      return new TrampolineSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }
    if (Ior.class.isAssignableFrom(type.getRawClass())) {
      return new IorSerializer();
    }
    if (Sealed2.class.isAssignableFrom(type.getRawClass())) {
      return new Sealed2Serializer();
    }
    if (Sealed3.class.isAssignableFrom(type.getRawClass())) {
      return new Sealed3Serializer();
    }
    if (Sealed4.class.isAssignableFrom(type.getRawClass())) {
      return new Sealed4Serializer();
    }
    if (Sealed5.class.isAssignableFrom(type.getRawClass())) {
      return new Sealed5Serializer();
    }
    if (Value.class.isAssignableFrom(type.getRawClass())) {
      return new ValueSerializer(type,true,contentTypeSerializer,contentValueSerializer);
    }

    return super.findReferenceSerializer(config, type, beanDesc, contentTypeSerializer, contentValueSerializer);
  }

  @Override
  public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    if (Either.class.isAssignableFrom(type.getRawClass())) {
      return new Sealed2Serializer();
    }
    return super.findSerializer(config, type, beanDesc);
  }
}
