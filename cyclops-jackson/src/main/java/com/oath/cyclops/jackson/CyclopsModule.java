package com.oath.cyclops.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.oath.cyclops.jackson.deserializers.CyclopsDeserializers;
import com.oath.cyclops.jackson.serializers.CyclopsSerializers;
import com.oath.cyclops.jackson.serializers.EitherSerializer;
import cyclops.control.Either;

public class CyclopsModule extends SimpleModule {




  @Override
  public void setupModule(SetupContext context) {
    context.addDeserializers(new CyclopsDeserializers());
    context.addSerializers(new CyclopsSerializers());
    context.addTypeModifier(new CyclopsTypeModifier());

  }
}
