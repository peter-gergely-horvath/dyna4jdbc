package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.EmptyResultSet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertTrue;

/**
 * @author Peter Horvath
 */
public class AbstractReadOnlyResultSetTest {

    private static final Map<Class<?>, Object> TYPE_DEFAULT_VALUES_MAP;

    static {

        Map<Class<?>, Object> map = new HashMap<>();

        map.put(boolean.class, false);
        map.put(byte.class, (byte)0 );
        map.put(short.class, (short)0 );
        map.put(int.class, 0 );
        map.put(long.class, 0 );
        map.put(float.class, 0.0f );
        map.put(double.class, 0.0d );

        TYPE_DEFAULT_VALUES_MAP = Collections.unmodifiableMap(map);
    }


    private AbstractReadOnlyResultSet abstractReadOnlyResultSet;

    @BeforeMethod
    public void beforeMethod()  {

        // we know that EmptyResultSet extends AbstractReadOnlyResultSet,
        // hence, we can use it as a concrete placeholder to test methods in
        // AbstractReadOnlyResultSet
        abstractReadOnlyResultSet = new EmptyResultSet();
    }

    @Test
    public void dataProviderTest() throws Exception {
        // TestNG fails silently if the data provider methods throws any exception
        // ensure we get something meaningful in such cases
        AbstractReadOnlyResultSetTest.updateMethodsAbstractReadOnlyResultSet();
    }

    private static MethodReference mapMethod(Method method) {
        return new MethodReference(method);
    }

    @DataProvider(name = "updateMethodsAbstractReadOnlyResultSet")
    public static Iterator<Object[]> updateMethodsAbstractReadOnlyResultSet() throws Exception {

        return Stream.of(ResultSet.class.getMethods())
                .filter(method -> method.getName().startsWith("update"))
                .map(method -> mapMethod(method) )
                .map(m -> new Object[] { m} )
                .collect(Collectors.toList())
                .iterator();
    }

    public static class MethodReference {
        private final Method method;

        private MethodReference(Method method) {
            this.method = method;
        }

        @Override
        public String toString() {

            String methodArgumentTypes = Stream.of(method.getParameterTypes())
                    .map(clazz -> clazz.getSimpleName())
                    .collect(Collectors.joining(", "));

            return String.format("%s(%s)", method.getName(), methodArgumentTypes);
        }
    }

    @Test(dataProvider = "updateMethodsAbstractReadOnlyResultSet")
    public void testMethodCall(MethodReference methodReference) throws Throwable {

        Object[] parameters = Stream.of(methodReference.method.getParameterTypes())
                .map(clazz -> {
                    Object defaultValue = TYPE_DEFAULT_VALUES_MAP.get(clazz);
                    if(clazz.isPrimitive()
                            && defaultValue == null) {
                        throw new IllegalStateException(
                                "Default value cannot be null / unmapped for primite type: " + clazz);
                    }

                    return defaultValue;
                })
                .toArray();

        try {
            methodReference.method.invoke(abstractReadOnlyResultSet, parameters);

        } catch (InvocationTargetException ite) {
            Throwable realException = ite.getCause();

            assertTrue(realException instanceof SQLException);

            String message = realException.getMessage();
            
            assertTrue(message.contains(JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.name()),
                    String.format("Invalid message: '%s'", message));
        }
    }
}

