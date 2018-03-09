package com.oath.cyclops.jackson.deserializers;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.oath.cyclops.jackson.deserializers.OptionDeserializer;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.control.Option;
import cyclops.control.Trampoline;

public class CyclopsDeserializers  extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
    Class<?> raw = type.getRawClass();
    if (raw == Maybe.class) {
      return new MaybeDeserializer(type);
    }
    if (raw == Option.class) {
      return new OptionDeserializer(type);
    }
    if (raw == Eval.class) {
      return new EvalDeserializer(type);
    }
    if (raw == Trampoline.class) {
      return new TrampolineDeserializer(type);
    }
    return super.findBeanDeserializer(type, config, beanDesc);
  }

  @Override
  public JsonDeserializer<?> findReferenceDeserializer(ReferenceType type,
                                                       DeserializationConfig config, BeanDescription bean,
                                                       TypeDeserializer typeDeserializer, JsonDeserializer<?> jsonDeserializer) throws JsonMappingException {

    return super.findReferenceDeserializer(type, config, bean, typeDeserializer, jsonDeserializer);
  }

}
