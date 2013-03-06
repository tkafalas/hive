/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.hadoop.hive.serde.serdeConstants;

/**
 * HiveResultSetMetaData.
 *
 */
public class HiveResultSetMetaData implements java.sql.ResultSetMetaData {
  private final List<String> columnNames;
  private final List<String> columnTypes;

  public HiveResultSetMetaData(List<String> columnNames,
      List<String> columnTypes) {
    this.columnNames = columnNames;
    this.columnTypes = columnTypes;
  }

  public String getCatalogName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public String getColumnClassName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public int getColumnCount() throws SQLException {
    return columnNames.size();
  }

  public int getColumnDisplaySize(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnDisplaySize(columnType);
  }

  public String getColumnLabel(int column) throws SQLException {
    return columnNames.get(column - 1);
  }

  public String getColumnName(int column) throws SQLException {
    return columnNames.get(column - 1);
  }

  public int getColumnType(int column) throws SQLException {
    if (columnTypes == null) {
      throw new SQLException(
          "Could not determine column type name for ResultSet");
    }

    if (column < 1 || column > columnTypes.size()) {
      throw new SQLException("Invalid column value: " + column);
    }

    // we need to convert the thrift type to the SQL type
    String type = columnTypes.get(column - 1);

    // we need to convert the thrift type to the SQL type
    return Utils.hiveTypeToSqlType(type);
  }

  public String getColumnTypeName(int column) throws SQLException {
    if (columnTypes == null) {
      throw new SQLException(
          "Could not determine column type name for ResultSet");
    }

    if (column < 1 || column > columnTypes.size()) {
      throw new SQLException("Invalid column value: " + column);
    }

    // we need to convert the Hive type to the SQL type name
    // TODO: this would be better handled in an enum
    String type = columnTypes.get(column - 1);
    if ("string".equalsIgnoreCase(type)) {
      return serdeConstants.STRING_TYPE_NAME;
    } else if ("float".equalsIgnoreCase(type)) {
      return serdeConstants.FLOAT_TYPE_NAME;
    } else if ("double".equalsIgnoreCase(type)) {
      return serdeConstants.DOUBLE_TYPE_NAME;
    } else if ("bool".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
      return serdeConstants.BOOLEAN_TYPE_NAME;
    } else if ("byte".equalsIgnoreCase(type) || "tinyint".equalsIgnoreCase(type)) {
      return serdeConstants.TINYINT_TYPE_NAME;
    } else if ("smallint".equalsIgnoreCase(type)) {
      return serdeConstants.SMALLINT_TYPE_NAME;
    } else if ("i32".equalsIgnoreCase(type) || "int".equalsIgnoreCase(type)) {
      return serdeConstants.INT_TYPE_NAME;
    } else if ("i64".equalsIgnoreCase(type) || "bigint".equalsIgnoreCase(type)) {
      return serdeConstants.BIGINT_TYPE_NAME;
    } else if ("timestamp".equalsIgnoreCase(type)) {
      return serdeConstants.TIMESTAMP_TYPE_NAME;
    } else if (type.startsWith("map<")) {
      return serdeConstants.STRING_TYPE_NAME;
    } else if (type.startsWith("array<")) {
      return serdeConstants.STRING_TYPE_NAME;
    } else if (type.startsWith("struct<")) {
      return serdeConstants.STRING_TYPE_NAME;
    }

    throw new SQLException("Unrecognized column type: " + type);
  }

  public int getPrecision(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnPrecision(columnType);
  }

  public int getScale(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnScale(columnType);
  }

  public String getSchemaName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public String getTableName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isAutoIncrement(int column) throws SQLException {
    // Hive doesn't have an auto-increment concept
    return false;
  }

  public boolean isCaseSensitive(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isCurrency(int column) throws SQLException {
    // Hive doesn't support a currency type
    return false;
  }

  public boolean isDefinitelyWritable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public int isNullable(int column) throws SQLException {
    // Hive doesn't have the concept of not-null
    return ResultSetMetaData.columnNullable;
  }

  public boolean isReadOnly(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isSearchable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /**
   * Returns a true if column is signed, false if not.
   *
   * This method checks the type of the passed column.  If that
   * type is not numerical, then the result is false.
   * If the type is a numeric then a true is returned.
   *
   * @param column the index of the column to test
   * @return boolean
   * @throws
   */
  public boolean isSigned(int column) throws SQLException {
    if (columnTypes == null) {
      throw new SQLException(
          "Could not determine column type name for ResultSet");
    }

    if (column < 1 || column > columnTypes.size()) {
      throw new SQLException("Invalid column value: " + column);
    }


    // we need to convert the thrift type to the SQL type
    int type = getColumnType(column);
    switch(type){
    case Types.DOUBLE: case Types.DECIMAL: case Types.FLOAT:
    case Types.INTEGER: case Types.REAL: case Types.SMALLINT: case Types.TINYINT:
    case Types.BIGINT:
      return true;
    }
    return false;
  }

  public boolean isWritable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

}
