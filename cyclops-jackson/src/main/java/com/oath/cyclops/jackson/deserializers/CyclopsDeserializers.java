package com.oath.cyclops.jackson.deserializers;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.oath.cyclops.jackson.deserializers.OptionDeserializer;
import com.oath.cyclops.types.persistent.PersistentMap;
import com.oath.cyclops.types.traversable.IterableX;
import cyclops.control.*;

public class CyclopsDeserializers  extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
    Class<?> raw = type.getRawClass();
    if (IterableX.class.isAssignableFrom(type.getRawClass())) {
      return new IterableXDeserializer(raw,type.containedTypeOrUnknown(0).getRawClass());
    }
    if (PersistentMap.class.isAssignableFrom(type.getRawClass())) {
      return new PersistentMapDeserializer(raw);
    }
    if (raw == Maybe.class) {
      return new MaybeDeserializer(type);
    }
    if (raw == Option.class) {
      return new OptionDeserializer(type);
    }
    if (raw == Eval.class) {
      return new EvalDeserializer(type);
    }
    if (raw == Future.class) {
      return new EvalDeserializer(type);
    }
    if (raw == Ior.class) {
      return new IorDeserializer(type);
    }
    if (raw == LazyEither.class) {
      return new LazyEitherDeserializer(type);
    }
    if (raw == LazyEither3.class) {
      return new LazyEither3Deserializer(type);
    }
    if (raw == LazyEither4.class) {
      return new LazyEither4Deserializer(type);
    }

    if (raw == Either.class) {
      return new EitherDeserializer(type);
    }
    if (raw == Trampoline.class) {
      return new TrampolineDeserializer(type);
    }
    if (raw == Unrestricted.class) {
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
