package com.oath.cyclops.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import cyclops.control.Either;
import cyclops.control.Eval;
import cyclops.control.Option;

import java.lang.reflect.Type;

public class CyclopsTypeModifier extends TypeModifier {

  @Override
  public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory)
  {
    if (type.isReferenceType() || type.isContainerType()) {
      return type;
    }
    final Class<?> raw = type.getRawClass();

    if (raw==Option.class)
      return  ReferenceType.upgradeFrom(type,type.containedTypeOrUnknown(0));

    if (raw==Eval.class)
      return  ReferenceType.upgradeFrom(type,type.containedTypeOrUnknown(0));
    if (raw==Either.class)
      return  ReferenceType.upgradeFrom(type,type.containedTypeOrUnknown(0));

    return type;
  }
}
