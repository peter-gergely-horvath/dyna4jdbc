package com.github.dyna4jdbc.internal.common.typeconverter;


import java.util.Iterator;

public interface TypeHandlerFactory {

    TypeHandler newTypeHandler(Iterator<String> columnIterable);
}
