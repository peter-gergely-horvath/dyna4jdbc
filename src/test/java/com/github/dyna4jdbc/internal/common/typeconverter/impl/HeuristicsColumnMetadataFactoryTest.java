package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;

import static org.testng.Assert.*;

public class HeuristicsColumnMetadataFactoryTest {
    
    private static final String COLUMN_INDEX = "1";
    
    private ColumnMetadataFactory heuristicsColumnMetadataFactory;

    private enum TestConfig {
        
        VARIABLE_LENGTH_STRINGS("Strings with variable length", 
                varcharColumnMetadata(6, COLUMN_INDEX, Nullability.NOT_NULLABLE), 
                "Mary", "had", "a", "little", "lamb"),
        VARIABLE_LENGTH_STRINGS_AND_NULLS("Strings with variable length and nulls", 
                varcharColumnMetadata(6, COLUMN_INDEX, Nullability.NULLABLE), 
                "Mary", "had", null, "a", "little", null, "lamb"),
        VARIABLE_LENGTH_STRINGS_AND_EMPTY_STRINGS("Strings with variable length and empty strings", 
                varcharColumnMetadata(6, COLUMN_INDEX, Nullability.NOT_NULLABLE), 
                "Mary", "had", "", "a", "little", "", "lamb"),
        INTEGERS("Integers", 
                integerColumnMetadata(4, COLUMN_INDEX, Nullability.NOT_NULLABLE), 
                "13", "42", "123", "561", "1984"),
        INTEGERS_AND_NULLS("Integers and nulls", 
                integerColumnMetadata(4, COLUMN_INDEX, Nullability.NULLABLE), 
                "13", "42", null, "123", "561", null, "1984"),
        INTEGERS_AND_STRINGS("Integers and strings", 
                varcharColumnMetadata(6, COLUMN_INDEX, Nullability.NULLABLE), 
                "13", "42", null, "123", "561", null, "1984", "Mary", "had", "a", "little", "lamb"),
        DOUBLES("Doubles", 
                doubleColumnMetadata(15, 9, COLUMN_INDEX, Nullability.NOT_NULLABLE), 
                "0.1234", "1.234", "12.34", "123.4", "1984.123456789"),
        DOUBLES_AND_INTEGERS("Doubles and integers", 
                doubleColumnMetadata(14, 4, COLUMN_INDEX, Nullability.NOT_NULLABLE), 
                "0.1234", "1.234", "12.34", "123.4", "123456789");
        
        private final String description;
        private final ColumnMetadata expectedColumnMetadata;
        private final List<String> inputValues;

        TestConfig(String description, ColumnMetadata expectedColumnMetadata, String... inputValues) {
            this.description = description;
            this.expectedColumnMetadata = expectedColumnMetadata;
            this.inputValues = Arrays.asList(inputValues);
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    

    @DataProvider(name = "columnMetadata")
    public static Iterator<Object[]> getTestParameters() throws Exception {
        
        return Stream.of(TestConfig.values())
                .map(tc -> new Object[] { tc })
                .collect(Collectors.toList())
                .iterator();
    }
    
    
    @BeforeMethod
    public void beforeMethod() {
        
        heuristicsColumnMetadataFactory = new HeuristicsColumnMetadataFactory();
    }
    
    
    @Test(dataProvider = "columnMetadata", enabled=false)
    public void testInput(TestConfig testConfig) {
        
        ColumnMetadata expectedColumnMetadata = testConfig.expectedColumnMetadata;
        
        ColumnMetadata actualColumnMetadata = 
                heuristicsColumnMetadataFactory.getColumnMetadata(0, testConfig.inputValues);
        
        
        assertNoDifferences(expectedColumnMetadata, actualColumnMetadata);
    }


    private void assertNoDifferences(ColumnMetadata expectedColumnMetadata, ColumnMetadata actualColumnMetadata) {
        
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(DefaultColumnMetadata.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            
            DiffBuilder diffBuilder = 
                    new DiffBuilder(expectedColumnMetadata, actualColumnMetadata, 
                            ToStringStyle.DEFAULT_STYLE);
            
            for(PropertyDescriptor pd : propertyDescriptors) {
                String name = pd.getName();
                
                Object expectedValue = pd.getReadMethod().invoke(expectedColumnMetadata, (Object[])null);
                Object actualValue = pd.getReadMethod().invoke(actualColumnMetadata, (Object[])null);
                
                
                diffBuilder.append(name, expectedValue, actualValue);
                
            }
            
            DiffResult diffResult = diffBuilder.build();
            if(diffResult.getNumberOfDiffs() > 0) {
                
                List<Diff<?>> diffs = diffResult.getDiffs();
                
                String diffResultString = String.join("\n", diffs.stream()
                            .map(diff -> String.format("%s: expected: %s, actual: %s", 
                                    diff.getFieldName(), diff.getLeft(), diff.getRight() ) )
                            .collect(Collectors.toList()));
                
                fail(diffResultString);
            }
        } catch (IllegalAccessException | IntrospectionException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } 
    }
    
    private static ColumnMetadata varcharColumnMetadata(int scale, String header, Nullability nullability) {
        
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();
        
        metadata.setConsumesFirstRowValue(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.VARCHAR);
        metadata.setFormatString(null);
        
        return metadata;
    }
    
    private static ColumnMetadata integerColumnMetadata(int scale, String header, Nullability nullability) {
        
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();
        
        metadata.setConsumesFirstRowValue(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.INTEGER);
        metadata.setFormatString(null);
        
        return metadata;
    }
    
    private static ColumnMetadata doubleColumnMetadata(int scale, int precision, String header, Nullability nullability) {
        
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();
        
        metadata.setConsumesFirstRowValue(false);
        
        if(precision == 0) {
            metadata.setColumnDisplaySize(scale); 
        } else {
            // include decimal point in ColumnDisplaySize
            metadata.setColumnDisplaySize(scale + 1);   
        }
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(precision);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.DOUBLE);
        metadata.setFormatString(null);
        
        return metadata;
    }
}
