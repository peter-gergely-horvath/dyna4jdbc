package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import org.testng.annotations.BeforeMethod;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;

public class HeuristicsColumnMetadataFactoryTest {
    
    private ColumnMetadataFactory heuristicsColumnMetadataFactory;
    
    @BeforeMethod
    public void beforeMethod() {
        
        heuristicsColumnMetadataFactory = new HeuristicsColumnMetadataFactory();
    }
    
    public void testInput() {
        
        ColumnMetadata columnMetadata = heuristicsColumnMetadataFactory.getColumnMetadata(1, null);
        
        
        
    }
    
    

}
