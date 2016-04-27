package com.github.dyna4jdbc.internal.common.typeconverter.impl;

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

import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConverter;

class TypeConverterRegistry {

	@SuppressWarnings("unchecked")
	static <T> TypeConverter<T> getTypeConverterForClass(Class<T> clazz) {
		return ((TypeConverter<T>) TYPE_CONVERTERS.get(clazz));
	}

	static final TypeConverter<String> STRING = new TypeConverter<String>() {

		@Override
		public String convert(String input) {
			return input;
		}
	};

	static final TypeConverter<Boolean> BOOLEAN = new TypeConverter<Boolean>() {

		@Override
		public Boolean convert(String input) {
			if (input == null) {
				return null;
			}

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

	static final TypeConverter<Byte> BYTE = new TypeConverter<Byte>() {

		@Override
		public Byte convert(String input) throws TypeConversionException {
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

	static final TypeConverter<Short> SHORT = new TypeConverter<Short>() {

		@Override
		public Short convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Short.decode(input);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

	};

	static final TypeConverter<Integer> INTEGER = new TypeConverter<Integer>() {

		@Override
		public Integer convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Integer.decode(input);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}
	};
	static final TypeConverter<Long> LONG = new TypeConverter<Long>() {

		@Override
		public Long convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Long.decode(input);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}
	};

	static final TypeConverter<Float> FLOAT = new TypeConverter<Float>() {

		@Override
		public Float convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Float.valueOf(input);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}
	};

	static final TypeConverter<Double> DOUBLE = new TypeConverter<Double>() {

		@Override
		public Double convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Double.valueOf(input);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}
	};

	static final TypeConverter<BigDecimal> BIGDECIMAL = new TypeConverter<BigDecimal>() {

		@Override
		public BigDecimal convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return new BigDecimal(input);
			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

	};

    static final TypeConverter<BigInteger> BIGINTEGER = new TypeConverter<BigInteger>() {

        @Override
        public BigInteger convert(String input) throws TypeConversionException {
            try {
                if (input == null) {
                    return null;
                }

                return new BigInteger(input);
            } catch (NumberFormatException nfe) {
                throw new TypeConversionException(nfe);
            }
        }

    };

	static final TypeConverter<byte[]> BYTE_ARRAY = new TypeConverter<byte[]>() {

		@Override
		public byte[] convert(String input) throws TypeConversionException {
			if (input == null) {
				return null;
			}

			return input.getBytes();
		}

	};

	static final TypeConverter<Date> DATE = new TypeConverter<Date>() {

		@Override
		public Date convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Date.valueOf(input);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}

	};
	static final TypeConverter<Time> TIME = new TypeConverter<Time>() {

		@Override
		public Time convert(String input) throws TypeConversionException {

			try {
				if (input == null) {
					return null;
				}

				return Time.valueOf(input);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}

	};
	static final TypeConverter<Timestamp> TIMESTAMP = new TypeConverter<Timestamp>() {

		@Override
		public Timestamp convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

				return Timestamp.valueOf(input);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}
	};

	static final TypeConverter<URL> URL = new TypeConverter<URL>() {

		@Override
		public URL convert(String input) throws TypeConversionException {
			try {
				if (input == null) {
					return null;
				}

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
