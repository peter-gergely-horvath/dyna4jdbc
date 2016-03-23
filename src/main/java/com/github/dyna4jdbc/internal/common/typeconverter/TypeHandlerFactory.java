package com.github.dyna4jdbc.internal.common.typeconverter;


public interface TypeHandlerFactory {

    TypeHandler newTypeHandler(int columnIndex, Iterable<String> columnIterable);
}
