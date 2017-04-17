/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConverter;

final class TypeConverterRegistry {

    private TypeConverterRegistry() {
        throw new AssertionError("static utility class");
    }

    @SuppressWarnings("unchecked")
    static <T> TypeConverter<T> getTypeConverterForClass(Class<T> clazz) {
        return ((TypeConverter<T>) TYPE_CONVERTERS.get(clazz));
    }
    
    private abstract static class NullHandlerTypeConverter<T> implements TypeConverter<T> {

        @Override
        public final T convert(String input) throws TypeConversionException {
            if (input != null) {
                return convertNotNullValue(input);
            } else {
                return null;
            }
        }
        
        protected abstract T convertNotNullValue(String notNullValue) throws TypeConversionException;
        
    }

    private abstract static class NullNumberHandlerTypeConverter<T> implements TypeConverter<T> {

        @Override
        public final T convert(String input) throws TypeConversionException {
            if (input != null) {
                return convertNotNullValue(input);
            } else {
                return null;
            }
        }

        protected final T convertNotNullValue(String notNullValue) throws TypeConversionException {
            return convertNotNullNumberValue(notNullValue.trim());
        }

        protected abstract T convertNotNullNumberValue(String notNullValue) throws TypeConversionException;
    }

    static final TypeConverter<String> STRING = new TypeConverter<String>() {

        @Override
        public String convert(String input) {
            return input;
        }
    };

    static final TypeConverter<Boolean> BOOLEAN = new NullHandlerTypeConverter<Boolean>() {

        @Override
        public Boolean convertNotNullValue(String input) {

            switch (input.trim()) {
                case "0":
                    return Boolean.FALSE;

                case "1":
                    return Boolean.TRUE;

                default:
                    return Boolean.valueOf(input);
            }
        }
    };

    static final TypeConverter<Byte> BYTE = new NullNumberHandlerTypeConverter<Byte>() {

        @Override
        public Byte convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                if (input == null) {
                    return null;
                }

                return Byte.decode(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }
    };

    static final TypeConverter<Short> SHORT = new NullNumberHandlerTypeConverter<Short>() {

        @Override
        public Short convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return Short.decode(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }

    };

    static final TypeConverter<Integer> INTEGER = new NullNumberHandlerTypeConverter<Integer>() {

        @Override
        public Integer convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return Integer.decode(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }
    };
    static final TypeConverter<Long> LONG = new NullNumberHandlerTypeConverter<Long>() {

        @Override
        public Long convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return Long.decode(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }
    };

    static final TypeConverter<Float> FLOAT = new NullNumberHandlerTypeConverter<Float>() {

        @Override
        public Float convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return Float.valueOf(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }
    };

    static final TypeConverter<Double> DOUBLE = new NullNumberHandlerTypeConverter<Double>() {

        @Override
        public Double convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return Double.valueOf(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }
    };

    static final TypeConverter<BigDecimal> BIGDECIMAL = new NullNumberHandlerTypeConverter<BigDecimal>() {

        @Override
        public BigDecimal convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return new BigDecimal(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }

    };

    static final TypeConverter<BigInteger> BIGINTEGER = new NullNumberHandlerTypeConverter<BigInteger>() {

        @Override
        public BigInteger convertNotNullNumberValue(String input) throws TypeConversionException {
            try {
                return new BigInteger(input);

            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }

    };

    static final TypeConverter<byte[]> BYTE_ARRAY = new NullHandlerTypeConverter<byte[]>() {

        @Override
        public byte[] convertNotNullValue(String input) throws TypeConversionException {
            try {
                return input.getBytes("UTF-8");

            } catch (UnsupportedEncodingException e) {
                // should not happen as the JVM should always support UTF-8
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE
                        .raiseUncheckedException(e);
            }
        }

    };

    static final TypeConverter<Date> DATE = new NullHandlerTypeConverter<Date>() {

        @Override
        public Date convertNotNullValue(String input) throws TypeConversionException {
            try {
                return Date.valueOf(input);

            } catch (IllegalArgumentException e) {
                throw new TypeConversionException(e);
            }
        }

    };
    static final TypeConverter<Time> TIME = new NullHandlerTypeConverter<Time>() {

        @Override
        public Time convertNotNullValue(String input) throws TypeConversionException {

            try {
                return Time.valueOf(input);

            } catch (IllegalArgumentException e) {
                throw new TypeConversionException(e);
            }
        }

    };
    static final TypeConverter<Timestamp> TIMESTAMP = new NullHandlerTypeConverter<Timestamp>() {

        @Override
        public Timestamp convertNotNullValue(String input) throws TypeConversionException {
            try {
                return Timestamp.valueOf(input);

            } catch (IllegalArgumentException e) {
                throw new TypeConversionException(e);
            }
        }
    };

    static final TypeConverter<URL> URL = new NullHandlerTypeConverter<URL>() {

        @Override
        public URL convertNotNullValue(String input) throws TypeConversionException {
            try {
                return new URL(input);

            } catch (MalformedURLException e) {
                throw new TypeConversionException(e);
            }
        }
    };

    private static final Map<Class<?>, TypeConverter<?>> TYPE_CONVERTERS;

    static {

        HashMap<Class<?>, TypeConverter<?>> map = new HashMap<>();

        map.put(String.class, STRING);
        map.put(Boolean.class, BOOLEAN);
        map.put(Byte.class, BYTE);
        map.put(Short.class, SHORT);
        map.put(Integer.class, INTEGER);
        map.put(Long.class, LONG);
        map.put(Float.class, FLOAT);
        map.put(Double.class, DOUBLE);
        map.put(BigDecimal.class, BIGDECIMAL);
        map.put(BigInteger.class, BIGINTEGER);
        map.put(byte[].class, BYTE_ARRAY);
        map.put(Date.class, DATE);
        map.put(Time.class, TIME);
        map.put(Timestamp.class, TIMESTAMP);
        map.put(URL.class, URL);

        TYPE_CONVERTERS = Collections.unmodifiableMap(map);

    }

}
