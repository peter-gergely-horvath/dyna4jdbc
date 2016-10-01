package com.github.dyna4jdbc.internal.common.jdbc.generic;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.DriverInfo;
import com.google.common.base.Strings;

public class GenericDatabaseMetaDataTest {

    private static final String VERSION_STRING = "1.2.3";
    private static final String DATABASE_PRODUCT_NAME = "FooBar";

    private Connection mockConnection;
    private GenericDatabaseMetaData genericDatabaseMetaData;


    @BeforeMethod
    public void beforeMethod() {

        mockConnection = createStrictMock(Connection.class);
        replay(mockConnection);

        genericDatabaseMetaData = new GenericDatabaseMetaData(
                mockConnection, DATABASE_PRODUCT_NAME, VERSION_STRING);

    }

    @AfterMethod
    public void afterMethod() {
        verify(mockConnection);
    }

    @Test
    public void testDriverVersion() throws SQLException {

        assertEquals(VERSION_STRING, genericDatabaseMetaData.getDatabaseProductVersion());
    }

    @Test
    public void testGetDatabaseProductName() throws SQLException {

        assertEquals(DATABASE_PRODUCT_NAME, genericDatabaseMetaData.getDatabaseProductName());
    }

    @Test
    public void testGetConnection() throws SQLException {

        assertEquals(mockConnection, genericDatabaseMetaData.getConnection());
    }

    @Test
    public void testGetDriverName() throws SQLException {

        assertEquals(DriverInfo.DRIVER_NAME, genericDatabaseMetaData.getDriverName());
    }

    @Test
    public void testGetDriverVersion() throws SQLException {

        String expectedDriverVersion = String.format("%s.%s",
                DriverInfo.DRIVER_VERSION_MAJOR, DriverInfo.DRIVER_VERSION_MINOR);

        assertEquals(expectedDriverVersion, genericDatabaseMetaData.getDriverVersion());
    }

    @Test
    public void testGetDriverMajorVersion() throws SQLException {

        assertEquals(DriverInfo.DRIVER_VERSION_MAJOR, genericDatabaseMetaData.getDriverMajorVersion());
    }

    @Test
    public void testGetDriverMinorVersion() throws SQLException {

        assertEquals(DriverInfo.DRIVER_VERSION_MINOR, genericDatabaseMetaData.getDriverMinorVersion());
    }

    @Test
    public void testAllProceduresAreCallable() throws SQLException {

        assertFalse(genericDatabaseMetaData.allProceduresAreCallable());
    }

    @Test
    public void testGetURL() throws SQLException {

        assertNull(genericDatabaseMetaData.getURL());
    }

    @Test
    public void testGetUserName() throws SQLException {

        assertNotNull(Strings.emptyToNull(genericDatabaseMetaData.getUserName()));
    }


    @Test
    public void testAllTablesAreSelectable() throws SQLException {

        assertFalse(genericDatabaseMetaData.allTablesAreSelectable());
    }

    @Test
    public void testNullsAreSortedHigh() throws SQLException {

        assertFalse(genericDatabaseMetaData.nullsAreSortedHigh());
    }

    @Test
    public void testNullsAreSortedLow() throws SQLException {

        assertFalse(genericDatabaseMetaData.nullsAreSortedLow());
    }

    @Test
    public void testNullsAreSortedAtStart() throws SQLException {

        assertFalse(genericDatabaseMetaData.nullsAreSortedAtStart());
    }

    @Test
    public void testNullsAreSortedAtEnd() throws SQLException {

        assertFalse(genericDatabaseMetaData.nullsAreSortedAtEnd());
    }

    @Test
    public void testUsesLocalFiles() throws SQLException {

        assertFalse(genericDatabaseMetaData.usesLocalFiles());
    }

    @Test
    public void testUsesLocalFilePerTable() throws SQLException {

        assertFalse(genericDatabaseMetaData.usesLocalFilePerTable());
    }

    @Test
    public void testSupportsMixedCaseIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsMixedCaseIdentifiers());
    }

    @Test
    public void testStoresUpperCaseIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesUpperCaseIdentifiers());
    }

    @Test
    public void testStoresLowerCaseIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesLowerCaseIdentifiers());
    }

    @Test
    public void testStoresMixedCaseIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesMixedCaseIdentifiers());
    }

    @Test
    public void testSupportsMixedCaseQuotedIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsMixedCaseQuotedIdentifiers());
    }

    @Test
    public void testStoresUpperCaseQuotedIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesUpperCaseQuotedIdentifiers());
    }

    @Test
    public void testStoresLowerCaseQuotedIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesLowerCaseQuotedIdentifiers());
    }

    @Test
    public void testStoresMixedCaseQuotedIdentifiers() throws SQLException {

        assertFalse(genericDatabaseMetaData.storesMixedCaseQuotedIdentifiers());
    }

    @Test
    public void testGetIdentifierQuoteString() throws SQLException {

        assertEquals(" ", genericDatabaseMetaData.getIdentifierQuoteString());
    }

    @Test
    public void testGetSQLKeywords() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getSQLKeywords());
    }

    @Test
    public void testGetNumericFunctions() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getNumericFunctions());
    }

    @Test
    public void testGetStringFunctions() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getStringFunctions());
    }

    @Test
    public void testGetSystemFunctions() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getSystemFunctions());
    }

    @Test
    public void testGetTimeDateFunctions() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getTimeDateFunctions());
    }

    @Test
    public void testGetSearchStringEscape() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getSearchStringEscape());
    }

    @Test
    public void testGetExtraNameCharacters() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getExtraNameCharacters());
    }


    @Test
    public void testSupportsAlterTableWithAddColumn() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsAlterTableWithAddColumn());
    }

    @Test
    public void testSupportsAlterTableWithDropColumn() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsAlterTableWithDropColumn());
    }

    @Test
    public void testSupportsColumnAliasing() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsColumnAliasing());
    }

    @Test
    public void testNullPlusNonNullIsNull() throws SQLException {

        assertFalse(genericDatabaseMetaData.nullPlusNonNullIsNull());
    }

    @Test
    public void testSupportsConvert() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsConvert());
    }

    @Test
    public void testSupportsConvertBetween() throws SQLException {

        for(JDBCType sourceType : JDBCType.values()) {
            for(JDBCType targetType : JDBCType.values()) {

                Integer sourceTypeVendorTypeNumber = sourceType.getVendorTypeNumber();
                Integer targetTypeVendorTypeNumber = targetType.getVendorTypeNumber();

                assertFalse(genericDatabaseMetaData.supportsConvert(
                        sourceTypeVendorTypeNumber, targetTypeVendorTypeNumber));
            }
        }
    }

    @Test
    public void testSupportsTableCorrelationNames() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsTableCorrelationNames());
    }

    @Test
    public void testSupportsDifferentTableCorrelationNames() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsDifferentTableCorrelationNames());
    }

    @Test
    public void testSupportsExpressionsInOrderBy() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsExpressionsInOrderBy());
    }

    @Test
    public void testSupportsOrderByUnrelated() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOrderByUnrelated());
    }

    @Test
    public void testSupportsGroupBy() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsGroupBy());
    }

    @Test
    public void testSupportsGroupByUnrelated() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsGroupByUnrelated());
    }

    @Test
    public void testSupportsGroupByBeyondSelect() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsGroupByBeyondSelect());
    }

    @Test
    public void testSupportsLikeEscapeClause() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsLikeEscapeClause());
    }

    @Test
    public void testSupportsMultipleResultSets() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsMultipleResultSets());
    }

    @Test
    public void testSupportsMultipleTransactions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsMultipleTransactions());
    }

    @Test
    public void testSupportsNonNullableColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsNonNullableColumns());
    }

    @Test
    public void testSupportsMinimumSQLGrammar() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsMinimumSQLGrammar());
    }

    @Test
    public void testSupportsCoreSQLGrammar() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCoreSQLGrammar());
    }

    @Test
    public void testSupportsExtendedSQLGrammar() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsExtendedSQLGrammar());
    }

    @Test
    public void testSupportsANSI92EntryLevelSQL() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsANSI92EntryLevelSQL());
    }

    @Test
    public void testSupportsANSI92IntermediateSQL() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsANSI92IntermediateSQL());
    }

    @Test
    public void testSupportsANSI92FullSQL() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsANSI92FullSQL());
    }

    @Test
    public void testSupportsIntegrityEnhancementFacility() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsIntegrityEnhancementFacility());
    }

    @Test
    public void testSupportsOuterJoins() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOuterJoins());
    }

    @Test
    public void testSupportsFullOuterJoins() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsFullOuterJoins());
    }

    @Test
    public void testSupportsLimitedOuterJoins() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsLimitedOuterJoins());
    }

    @Test
    public void testGetSchemaTerm() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getSchemaTerm());
    }

    @Test
    public void testGetProcedureTerm() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getProcedureTerm());
    }

    @Test
    public void testGetCatalogTerm() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getCatalogTerm());
    }

    @Test
    public void testIsCatalogAtStart() throws SQLException {

        assertFalse(genericDatabaseMetaData.isCatalogAtStart());
    }


    @Test
    public void testGetCatalogSeparator() throws SQLException {

        assertEquals("", genericDatabaseMetaData.getCatalogSeparator());
    }



    @Test
    public void testSupportsSchemasInDataManipulation() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSchemasInDataManipulation());
    }

    @Test
    public void testSupportsSchemasInProcedureCalls() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSchemasInProcedureCalls());
    }

    @Test
    public void testSupportsSchemasInTableDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSchemasInTableDefinitions());
    }

    @Test
    public void testSupportsSchemasInIndexDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSchemasInIndexDefinitions());
    }

    @Test
    public void testSupportsSchemasInPrivilegeDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSchemasInPrivilegeDefinitions());
    }

    @Test
    public void testSupportsCatalogsInDataManipulation() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCatalogsInDataManipulation());
    }

    @Test
    public void testSupportsCatalogsInProcedureCalls() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCatalogsInProcedureCalls());
    }

    @Test
    public void testSupportsCatalogsInTableDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCatalogsInTableDefinitions());
    }

    @Test
    public void testSupportsCatalogsInIndexDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCatalogsInIndexDefinitions());
    }

    @Test
    public void testSupportsCatalogsInPrivilegeDefinitions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCatalogsInPrivilegeDefinitions());
    }

    @Test
    public void testSupportsPositionedDelete() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsPositionedDelete());
    }

    @Test
    public void testSupportsPositionedUpdate() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsPositionedUpdate());
    }

    @Test
    public void testSupportsSelectForUpdate() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSelectForUpdate());
    }

    @Test
    public void testSupportsStoredProcedures() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsStoredProcedures());
    }

    @Test
    public void testSupportsSubqueriesInComparisons() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSubqueriesInComparisons());
    }

    @Test
    public void testSupportsSubqueriesInExists() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSubqueriesInExists());
    }

    @Test
    public void testSupportsSubqueriesInIns() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSubqueriesInIns());
    }

    @Test
    public void testSupportsSubqueriesInQuantifieds() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSubqueriesInQuantifieds());
    }

    @Test
    public void testSupportsCorrelatedSubqueries() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsCorrelatedSubqueries());
    }

    @Test
    public void testSupportsUnion() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsUnion());
    }

    @Test
    public void testSupportsUnionAll() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsUnionAll());
    }

    @Test
    public void testSupportsOpenCursorsAcrossCommit() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOpenCursorsAcrossCommit());
    }

    @Test
    public void testSupportsOpenCursorsAcrossRollback() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOpenCursorsAcrossRollback());
    }

    @Test
    public void testSupportsOpenStatementsAcrossCommit() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOpenStatementsAcrossCommit());
    }

    @Test
    public void testSupportsOpenStatementsAcrossRollback() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsOpenStatementsAcrossRollback());
    }

    @Test
    public void testGetMaxBinaryLiteralLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxBinaryLiteralLength());
    }

    @Test
    public void testGetMaxCharLiteralLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxCharLiteralLength());
    }

    @Test
    public void testGetMaxColumnNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnNameLength());
    }

    @Test
    public void testGetMaxColumnsInGroupBy() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnsInGroupBy());
    }

    @Test
    public void testGetMaxColumnsInIndex() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnsInIndex());
    }

    @Test
    public void testGetMaxColumnsInOrderBy() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnsInOrderBy());
    }

    @Test
    public void testGetMaxColumnsInSelect() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnsInSelect());
    }

    @Test
    public void testGetMaxColumnsInTable() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxColumnsInTable());
    }

    @Test
    public void testGetMaxConnections() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxConnections());
    }

    @Test
    public void testGetMaxCursorNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxCursorNameLength());
    }

    @Test
    public void testGetMaxIndexLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxIndexLength());
    }

    @Test
    public void testGetMaxSchemaNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxSchemaNameLength());
    }

    @Test
    public void testGetMaxProcedureNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxProcedureNameLength());
    }

    @Test
    public void testGetMaxCatalogNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxCatalogNameLength());
    }

    @Test
    public void testGetMaxRowSize() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxRowSize());
    }

    @Test
    public void testDoesMaxRowSizeIncludeBlobs() throws SQLException {

        assertFalse(genericDatabaseMetaData.doesMaxRowSizeIncludeBlobs());
    }

    @Test
    public void testGetMaxStatementLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxStatementLength());
    }

    @Test
    public void testGetMaxStatements() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxStatements());
    }

    @Test
    public void testGetMaxTableNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxTableNameLength());
    }

    @Test
    public void testGetMaxTablesInSelect() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxTablesInSelect());
    }

    @Test
    public void testGetMaxUserNameLength() throws SQLException {

        assertEquals(0, genericDatabaseMetaData.getMaxUserNameLength());
    }

    @Test
    public void testGetDefaultTransactionIsolation() throws SQLException {

        assertEquals(Connection.TRANSACTION_NONE, genericDatabaseMetaData.getDefaultTransactionIsolation());
    }

    @Test
    public void testSupportsTransactions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsTransactions());
    }

    @Test
    public void testSupportsTransactionIsolationLevel() throws SQLException {

        assertTrue(genericDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
        assertFalse(genericDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
        assertFalse(genericDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
        assertFalse(genericDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
        assertFalse(genericDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
    }

    @Test
    public void testSupportsDataDefinitionAndDataManipulationTransactions() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsDataDefinitionAndDataManipulationTransactions());
    }

    @Test
    public void testSupportsDataManipulationTransactionsOnly() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsDataManipulationTransactionsOnly());
    }

    @Test
    public void testDataDefinitionCausesTransactionCommit() throws SQLException {

        assertFalse(genericDatabaseMetaData.dataDefinitionCausesTransactionCommit());
    }

    @Test
    public void testDataDefinitionIgnoredInTransactions() throws SQLException {

        assertFalse(genericDatabaseMetaData.dataDefinitionIgnoredInTransactions());
    }

    @Test
    public void testGetProcedures() throws SQLException {

        assertFalse(genericDatabaseMetaData.getProcedures(null, null, null).next());
    }

    @Test
    public void testGetProcedureColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.getProcedureColumns(null, null, null, null).next());
    }

    @Test
    public void testGetTables() throws SQLException {

        assertFalse(genericDatabaseMetaData.getTables(null, null, null, null).next());
    }

    @Test
    public void testGetSchemas() throws SQLException {

        assertFalse(genericDatabaseMetaData.getSchemas().next());
    }

    @Test
    public void testGetCatalogs() throws SQLException {

        assertFalse(genericDatabaseMetaData.getCatalogs().next());
    }

    @Test
    public void testGetTableTypes() throws SQLException {

        assertFalse(genericDatabaseMetaData.getTableTypes().next());
    }

    @Test
    public void testGetColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.getColumns(null, null, null, null).next());
    }

    @Test
    public void testGetColumnPrivileges() throws SQLException {

        assertFalse(genericDatabaseMetaData.getColumnPrivileges(null, null, null, null).next());
    }

    @Test
    public void testGetTablePrivileges() throws SQLException {

        assertFalse(genericDatabaseMetaData.getTablePrivileges(null, null, null).next());
    }

    @Test
    public void testGetBestRowIdentifier() throws SQLException {

        assertFalse(genericDatabaseMetaData.getBestRowIdentifier(null, null, null, 0, false).next());
    }

    @Test
    public void testGetVersionColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.getVersionColumns(null, null, null).next());
    }
    
    @Test
    public void testGetPrimaryKeys() throws SQLException {

        assertFalse(genericDatabaseMetaData.getPrimaryKeys(null, null, null).next());
    }
    
    @Test
    public void testGetImportedKeys() throws SQLException {

        assertFalse(genericDatabaseMetaData.getImportedKeys(null, null, null).next());
    }
    
    @Test
    public void testGetExportedKeys() throws SQLException {

        assertFalse(genericDatabaseMetaData.getExportedKeys(null, null, null).next());
    }
    
    @Test
    public void testGetCrossReference() throws SQLException {

        assertFalse(genericDatabaseMetaData.getCrossReference(null, null, null, null, null, null).next());
    }
    
    @Test
    public void testGetTypeInfo() throws SQLException {

        assertFalse(genericDatabaseMetaData.getTypeInfo().next());
    }
    
    @Test
    public void teszGetIndexInfo() throws SQLException {

        assertFalse(genericDatabaseMetaData.getIndexInfo(null, null, null, false, false).next());
    }

    @Test
    public void testSupportsResultSetType() throws SQLException {

        assertTrue(genericDatabaseMetaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testSupportsResultSetConcurrency() throws SQLException {

        assertTrue(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));

        assertFalse(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));

        assertFalse(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));

        assertFalse(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));

        assertFalse(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));

        assertFalse(genericDatabaseMetaData.supportsResultSetConcurrency(
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
    }

    @Test
    public void testOwnUpdatesAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testOwnDeletesAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testOwnInsertsAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testOthersUpdatesAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testOthersDeletesAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testOthersInsertsAreVisible() throws SQLException {

        assertFalse(genericDatabaseMetaData.othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testUpdatesAreDetected() throws SQLException {

        assertFalse(genericDatabaseMetaData.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testDeletesAreDetected() throws SQLException {

        assertFalse(genericDatabaseMetaData.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testInsertsAreDetected() throws SQLException {

        assertFalse(genericDatabaseMetaData.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY));
        assertFalse(genericDatabaseMetaData.insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertFalse(genericDatabaseMetaData.insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Test
    public void testSupportsBatchUpdates() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsBatchUpdates());
    }
    
    @Test
    public void testGetUDTs() throws SQLException {

        assertFalse(genericDatabaseMetaData.getUDTs(null, null, null, null).next());
    }

    @Test
    public void testSupportsSavepoints() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsSavepoints());
    }

    @Test
    public void testSupportsNamedParameters() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsNamedParameters());
    }

    @Test
    public void testSupportsMultipleOpenResults() throws SQLException {

        assertTrue(genericDatabaseMetaData.supportsMultipleOpenResults());
    }

    @Test
    public void testSupportsGetGeneratedKeys() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsGetGeneratedKeys());
    }
    
    @Test
    public void testGetSuperTypes() throws SQLException {

        assertFalse(genericDatabaseMetaData.getSuperTypes(null, null, null).next());
    }
    
    @Test
    public void testGetSuperTables() throws SQLException {

        assertFalse(genericDatabaseMetaData.getSuperTables(null, null, null).next());
    }
    
    @Test
    public void testGetAttributes() throws SQLException {

        assertFalse(genericDatabaseMetaData.getAttributes(null, null, null, null).next());
    }

    @Test
    public void testSupportsResultSetHoldability() throws SQLException {

        assertTrue(genericDatabaseMetaData.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT));
        assertFalse(genericDatabaseMetaData.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT));
    }
    
    @Test
    public void testGetResultSetHoldability() throws SQLException {

        assertEquals(genericDatabaseMetaData.getResultSetHoldability(), ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }
    
    
    @Test
    public void testGetDatabaseMajorVersion() throws SQLException {

        assertEquals(genericDatabaseMetaData.getDatabaseMajorVersion(), DriverInfo.DRIVER_VERSION_MAJOR);
    }
    
    @Test
    public void testGetDatabaseMinorVersion() throws SQLException {

        assertEquals(genericDatabaseMetaData.getDatabaseMinorVersion(), DriverInfo.DRIVER_VERSION_MINOR);
    }
    
    @Test
    public void testGetJDBCMajorVersion() throws SQLException {

        assertEquals(genericDatabaseMetaData.getJDBCMajorVersion(), 4);
    }
    
    @Test
    public void testGetJDBCMinorVersion() throws SQLException {

        assertEquals(genericDatabaseMetaData.getJDBCMinorVersion(), 2);
    }
    
    @Test
    public void testGetSQLStateType() throws SQLException {

        assertEquals(genericDatabaseMetaData.getSQLStateType(), DatabaseMetaData.sqlStateSQL99);
    }
    

    @Test
    public void testLocatorsUpdateCopy() throws SQLException {

        assertFalse(genericDatabaseMetaData.locatorsUpdateCopy());
    }

    @Test
    public void testSupportsStatementPooling() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsStatementPooling());
    }
    
    @Test
    public void testGetRowIdLifetime() throws SQLException {

        assertEquals(genericDatabaseMetaData.getRowIdLifetime(), RowIdLifetime.ROWID_UNSUPPORTED);
    }
    
    @Test
    public void testGetSchemasStringString() throws SQLException {

        assertFalse(genericDatabaseMetaData.getSchemas(null, null).next());
    }

    @Test
    public void testSupportsStoredFunctionsUsingCallSyntax() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsStoredFunctionsUsingCallSyntax());
    }

    @Test
    public void testAutoCommitFailureClosesAllResultSets() throws SQLException {

        assertFalse(genericDatabaseMetaData.autoCommitFailureClosesAllResultSets());
    }
    
    @Test
    public void testGetClientInfoProperties() throws SQLException {

        assertFalse(genericDatabaseMetaData.getClientInfoProperties().next());
    }
    
    @Test
    public void testGetFunctions() throws SQLException {

        assertFalse(genericDatabaseMetaData.getFunctions(null, null, null).next());
    }
    
    @Test
    public void testGetFunctionColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.getFunctionColumns(null, null, null, null).next());
    }
    
    @Test
    public void getPseudoColumns() throws SQLException {

        assertFalse(genericDatabaseMetaData.getPseudoColumns(null, null, null, null).next());
    }

    @Test
    public void testGeneratedKeyAlwaysReturned() throws SQLException {

        assertFalse(genericDatabaseMetaData.generatedKeyAlwaysReturned());
    }

    @Test
    public void testSupportsRefCursors() throws SQLException {

        assertFalse(genericDatabaseMetaData.supportsRefCursors());
    }

    @Test
    public void testIsReadOnly() throws SQLException {

        assertFalse(genericDatabaseMetaData.isReadOnly());
    }

}
