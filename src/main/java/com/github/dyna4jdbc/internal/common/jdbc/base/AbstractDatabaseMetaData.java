package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.DriverInfo;
import com.github.dyna4jdbc.internal.common.jdbc.generic.EmptyResultSet;

public abstract class AbstractDatabaseMetaData<T extends Connection> extends AbstractWrapper implements DatabaseMetaData {

    private final T connection;

    public AbstractDatabaseMetaData(T connection) {
        this.connection = connection;
    }

    public final T getConnection() throws SQLException {
        return connection;
    }

    public final String getDriverName() throws SQLException {
        return DriverInfo.DRIVER_NAME;
    }

    public final String getDriverVersion() throws SQLException {
        return String.format("%s.%s", getDriverMajorVersion(), getDriverMinorVersion());
    }

    public final int getDriverMajorVersion() {
        return DriverInfo.DRIVER_VERSION_MAJOR;
    }

    public final int getDriverMinorVersion() {
        return DriverInfo.DRIVER_VERSION_MINOR;
    }


    // --- reasonable implementation of DatabaseMetaData
    public final boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    public final boolean allTablesAreSelectable() throws SQLException {
        return false;
    }

    public final String getURL() throws SQLException {
        /* "Returns the URL for this DBMS or null if it cannot be generated"  */
        return null;
    }

    public final String getUserName() throws SQLException {
        return System.getProperty("user.name");
    }

    public final boolean isReadOnly() throws SQLException {
        return false;
    }

    public final boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public final boolean nullsAreSortedLow() throws SQLException {
        return false;
    }

    public final boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public final boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }


    public final boolean usesLocalFiles() throws SQLException {
        return false;
    }

    public final boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public final boolean supportsMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    public final boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public final boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public final String getIdentifierQuoteString() throws SQLException {
        return " ";
    }

    public final String getSQLKeywords() throws SQLException {
        return "";
    }

    public final String getNumericFunctions() throws SQLException {
        return "";
    }

    public final String getStringFunctions() throws SQLException {
        return "";
    }

    public final String getSystemFunctions() throws SQLException {
        return "";
    }

    public final String getTimeDateFunctions() throws SQLException {
        return "";
    }

    public final String getSearchStringEscape() throws SQLException {
        return "";
    }

    public final String getExtraNameCharacters() throws SQLException {
        return "";
    }

    public final boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    public final boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    public final boolean supportsColumnAliasing() throws SQLException {
        return false;
    }

    public final boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    public final boolean supportsConvert() throws SQLException {
        return false;
    }

    public final boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }

    public final boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }

    public final boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public final boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    public final boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    public final boolean supportsGroupBy() throws SQLException {
        return false;
    }

    public final boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    public final boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    public final boolean supportsLikeEscapeClause() throws SQLException {
        return false;
    }

    public final boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    public final boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    public final boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }

    public final boolean supportsMinimumSQLGrammar() throws SQLException {
        return false;
    }

    public final boolean supportsCoreSQLGrammar() throws SQLException {
        return false;
    }

    public final boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public final boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return false;
    }

    public final boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public final boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public final boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public final boolean supportsOuterJoins() throws SQLException {
        return false;
    }

    public final boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    public final boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }

    public final String getSchemaTerm() throws SQLException {
        return "";
    }

    public final String getProcedureTerm() throws SQLException {
        return "";
    }

    public final String getCatalogTerm() throws SQLException {
        return "";
    }

    public final boolean isCatalogAtStart() throws SQLException {
        return false;
    }

    public final String getCatalogSeparator() throws SQLException {
        return "";
    }

    public final boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    public final boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    public final boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    public final boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    public final boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public final boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    public final boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    public final boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public final boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    public final boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    public final boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    public final boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    public final boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    public final boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    public final boolean supportsUnion() throws SQLException {
        return false;
    }

    public final boolean supportsUnionAll() throws SQLException {
        return false;
    }

    public final boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    public final boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    public final boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    public final boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    public final int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    public final int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    public final int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    public final int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    public final int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    public final int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    public final int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    public final int getMaxConnections() throws SQLException {
        return 0;
    }

    public final int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxIndexLength() throws SQLException {
        return 0;
    }

    public final int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxRowSize() throws SQLException {
        return 0;
    }

    public final boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    public final int getMaxStatementLength() throws SQLException {
        return 0;
    }

    public final int getMaxStatements() throws SQLException {
        return 0;
    }

    public final int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    public final int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    public final int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    public final int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    public final boolean supportsTransactions() throws SQLException {
        return false;
    }

    public final boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return level == Connection.TRANSACTION_NONE;
    }

    public final boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    public final boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public final boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public final boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public final ResultSet getProcedures(
            String catalog,
            String schemaPattern,
            String procedureNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getTables(
            String catalog,
            String schemaPattern,
            String tableNamePattern,
            String[] types) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getSchemas() throws SQLException {
        return new EmptyResultSet();
    }

    public final ResultSet getCatalogs() throws SQLException {
        return new EmptyResultSet();
    }

    public final ResultSet getTableTypes() throws SQLException {
        return new EmptyResultSet();
    }

    public final ResultSet getColumns(
            String catalog,
            String schemaPattern,
            String tableNamePattern,
            String columnNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getColumnPrivileges(
            String catalog,
            String schema,
            String table,
            String columnNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getTablePrivileges(
            String catalog,
            String schemaPattern,
            String tableNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getBestRowIdentifier(
            String catalog,
            String schema,
            String table,
            int scope,
            boolean nullable) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getVersionColumns(
            String catalog,
            String schema,
            String table) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getPrimaryKeys(
            String catalog,
            String schema,
            String table) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getImportedKeys(
            String catalog,
            String schema,
            String table) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getExportedKeys(
            String catalog,
            String schema,
            String table) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getCrossReference(
            String parentCatalog,
            String parentSchema,
            String parentTable,
            String foreignCatalog,
            String foreignSchema,
            String foreignTable) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getTypeInfo() throws SQLException {
        return new EmptyResultSet();
    }

    public final ResultSet getIndexInfo(
            String catalog,
            String schema,
            String table,
            boolean unique,
            boolean approximate) throws SQLException {

        return new EmptyResultSet();
    }

    public final boolean supportsResultSetType(int type) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY;
    }

    public final boolean supportsResultSetConcurrency(
            int type, int concurrency) throws SQLException {

        return type == ResultSet.TYPE_SCROLL_INSENSITIVE &&
                concurrency == ResultSet.CONCUR_READ_ONLY;
    }

    public final boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public final boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    public final boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    public final boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    public final boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    public final ResultSet getUDTs(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            int[] types) throws SQLException {

        return new EmptyResultSet();
    }

    public final boolean supportsSavepoints() throws SQLException {
        return false;
    }

    public final boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    public final boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    public final boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    public final ResultSet getSuperTypes(
            String catalog,
            String schemaPattern,
            String typeNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getSuperTables(
            String catalog,
            String schemaPattern,
            String tableNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final boolean supportsResultSetHoldability(int holdability)
            throws SQLException {

        return false;
    }

    public final int getResultSetHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public final int getDatabaseMajorVersion() throws SQLException {
        return DriverInfo.DRIVER_VERSION_MAJOR;
    }

    public final int getDatabaseMinorVersion() throws SQLException {
        return DriverInfo.DRIVER_VERSION_MINOR;
    }

    public final int getJDBCMajorVersion() throws SQLException {
        return 0;
    }

    public final int getJDBCMinorVersion() throws SQLException {
        return 1;
    }

    public final int getSQLStateType() throws SQLException {
        return 0; // TODO: implement method
    }

    public final boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    public final boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    public final RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    public final ResultSet getSchemas(
            String catalog,
            String schemaPattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    public final boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    public final ResultSet getClientInfoProperties() throws SQLException {
        return new EmptyResultSet();
    }

    public final ResultSet getFunctions(
            String catalog,
            String schemaPattern,
            String functionNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final ResultSet getPseudoColumns(
            String catalog,
            String schemaPattern,
            String tableNamePattern,
            String columnNamePattern) throws SQLException {

        return new EmptyResultSet();
    }

    public final boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }

}
