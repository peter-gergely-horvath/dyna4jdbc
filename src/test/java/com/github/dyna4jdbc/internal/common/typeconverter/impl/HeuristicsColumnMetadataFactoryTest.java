package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import static org.testng.Assert.fail;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Collections2;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;

public class HeuristicsColumnMetadataFactoryTest {

    private ColumnMetadataFactory heuristicsColumnMetadataFactory;

    @DataProvider(name = "columnMetadata")
    public static Iterator<Object[]> getTestParameters() throws Exception {
        
        return Stream.of(HeuristicsColumnMetadataFactoryTestParameters.values())
                .map(tc -> new Object[] { tc })
                .collect(Collectors.toList())
                .iterator();
    }
    
    
    @BeforeMethod
    public void beforeMethod() {
        
        heuristicsColumnMetadataFactory = new HeuristicsColumnMetadataFactory();
    }
    
    
    @Test(dataProvider = "columnMetadata")
    public void testWithParameter(HeuristicsColumnMetadataFactoryTestParameters testParameters) {

        ColumnMetadata expectedColumnMetadata = testParameters.getExpectedColumnMetadata();

        Collections2.permutations(testParameters.getInputValues())
                .stream()
                .map(testValueList ->
                    testValueList.stream()
                            .map(value -> "<null>".equals(value) ? null : value)
                            .collect(Collectors.toList())
                )
                .forEach(testValues -> {

                    ColumnMetadata actualColumnMetadata =
                            heuristicsColumnMetadataFactory.getColumnMetadata(0, testValues);

                    assertNoDifferences(expectedColumnMetadata, actualColumnMetadata, testValues);
                });

    }


    private void assertNoDifferences(
            ColumnMetadata expectedColumnMetadata, ColumnMetadata actualColumnMetadata, List<String> testValues) {
        
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
                            .map(diff -> String.format("\t%s: expected: %s, actual: %s",
                                    diff.getFieldName(), diff.getLeft(), diff.getRight() ) )
                            .collect(Collectors.toList()));
                
                fail(String.format("For input values: %s\nThe following mismatches were detected:\n%s",
                        String.join(", ", testValues),
                        diffResultString));
            }
        } catch (IllegalAccessException
                | IntrospectionException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        } 
    }
    

}
