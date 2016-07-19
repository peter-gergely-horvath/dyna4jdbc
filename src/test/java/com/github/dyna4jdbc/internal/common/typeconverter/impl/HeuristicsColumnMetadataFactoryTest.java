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
        
        ColumnMetadata actualColumnMetadata = 
                heuristicsColumnMetadataFactory.getColumnMetadata(0, testParameters.getInputValues());
        
        
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
                
                fail(String.format("\n%s", diffResultString));
            }
        } catch (IllegalAccessException
                | IntrospectionException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        } 
    }
    

}
