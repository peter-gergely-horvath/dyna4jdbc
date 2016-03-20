package com.github.dyna4jdbc.internal.common.typeconverter;


public interface TypeHandlerFactory {

    TypeHandler newTypeHandler(Iterable<String> columnIterable);
}
