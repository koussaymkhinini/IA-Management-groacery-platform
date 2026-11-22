
package com.groceryai.backend.Analytics.metadata;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DatabaseSchemaService {

    private final DataSource dataSource;

    public DatabaseSchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<TableSchema> getSchema() {
        List<TableSchema> tables = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();

            try (ResultSet tableResult = metadata.getTables(
                    connection.getCatalog(),
                    "public",
                    "%",
                    new String[]{"TABLE"}
            )) {
                while (tableResult.next()) {
                    String tableName = tableResult.getString("TABLE_NAME");

                    List<ColumnSchema> columns =
                            getColumns(metadata, connection, tableName);

                    List<String> primaryKeys =
                            getPrimaryKeys(metadata, connection, tableName);

                    List<ForeignKeySchema> foreignKeys =
                            getForeignKeys(metadata, connection, tableName);

                    tables.add(new TableSchema(
                            tableName,
                            columns,
                            primaryKeys,
                            foreignKeys
                    ));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to read database schema", e
            );
        }

        tables.sort(Comparator.comparing(TableSchema::tableName));

        return tables;
    }

    private List<ColumnSchema> getColumns(
            DatabaseMetaData metadata,
            Connection connection,
            String tableName
    ) throws SQLException {

        List<ColumnSchema> columns = new ArrayList<>();

        try (ResultSet result = metadata.getColumns(
                connection.getCatalog(),
                "public",
                tableName,
                "%"
        )) {
            while (result.next()) {
                columns.add(new ColumnSchema(
                        result.getString("COLUMN_NAME"),
                        result.getString("TYPE_NAME"),
                        result.getInt("DATA_TYPE"),
                        result.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls
                ));
            }
        }

        return columns;
    }

    private List<String> getPrimaryKeys(
            DatabaseMetaData metadata,
            Connection connection,
            String tableName
    ) throws SQLException {

        List<String> primaryKeys = new ArrayList<>();

        try (ResultSet result = metadata.getPrimaryKeys(
                connection.getCatalog(),
                "public",
                tableName
        )) {
            while (result.next()) {
                primaryKeys.add(result.getString("COLUMN_NAME"));
            }
        }

        return primaryKeys;
    }

    private List<ForeignKeySchema> getForeignKeys(
            DatabaseMetaData metadata,
            Connection connection,
            String tableName
    ) throws SQLException {

        List<ForeignKeySchema> foreignKeys = new ArrayList<>();

        try (ResultSet result = metadata.getImportedKeys(
                connection.getCatalog(),
                "public",
                tableName
        )) {
            while (result.next()) {
                foreignKeys.add(new ForeignKeySchema(
                        result.getString("FKCOLUMN_NAME"),
                        result.getString("PKTABLE_NAME"),
                        result.getString("PKCOLUMN_NAME")
                ));
            }
        }

        return foreignKeys;
    }

    public record TableSchema(
            String tableName,
            List<ColumnSchema> columns,
            List<String> primaryKeys,
            List<ForeignKeySchema> foreignKeys
    ) {}

    public record ColumnSchema(
            String columnName,
            String typeName,
            int dataType,
            boolean nullable
    ) {}

    public record ForeignKeySchema(
            String columnName,
            String referencedTable,
            String referencedColumn
    ) {}
}
