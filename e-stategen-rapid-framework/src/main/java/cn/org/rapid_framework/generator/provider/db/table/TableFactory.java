/*
 * copy from rapid-framework<code.google.com/p/rapid-framework> and modify by niaoge
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.org.rapid_framework.generator.provider.db.table;


import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.provider.db.DataSourceProvider;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.DBHelper;
import cn.org.rapid_framework.generator.util.FileHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.XMLHelper;
import cn.org.rapid_framework.generator.util.XMLHelper.NodeData;
/**
 * 
 * 根据数据库表的元数据(metadata)创建Table对象
 * 
 * <pre>
 * getTable(sqlName) : 根据数据库表名,得到table对象
 * getAllTable() : 搜索数据库的所有表,并得到table对象列表
 * </pre>
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class TableFactory {
    
    private static TableFactory instance = null;
    
    private String schema;
    private String catalog;
    private List<TableFactoryListener> tableFactoryListeners = new ArrayList<TableFactoryListener>();
    
    private TableFactory(String schema,String catalog) {
        this.schema = schema;
        this.catalog = catalog;
    }
    
    public synchronized static TableFactory getInstance() {
        if(instance == null) instance = new TableFactory(GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_SCHEMA),GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_CATALOG));
        return instance;
    }
    
    public List<TableFactoryListener> getTableFactoryListeners() {
        return tableFactoryListeners;
    }

    public void setTableFactoryListeners(
            List<TableFactoryListener> tableFactoryListeners) {
        this.tableFactoryListeners = tableFactoryListeners;
    }

    public boolean addTableFactoryListener(TableFactoryListener o) {
        return tableFactoryListeners.add(o);
    }

    public void clearTableFactoryListener() {
        tableFactoryListeners.clear();
    }

    public boolean removeTableFactoryListener(TableFactoryListener o) {
        return tableFactoryListeners.remove(o);
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    public List getAllTables() {
        Connection conn = DataSourceProvider.getConnection();
        try {
            List<Table> tables = new TableCreateProcessor(conn,getSchema(),getCatalog()).getAllTables();
            for(Table t : tables) {
                dispatchOnTableCreatedEvent(t);
            }
            return tables;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }finally {
            DBHelper.close(conn);
        }
    }
    
    private void dispatchOnTableCreatedEvent(Table t) {
        for(TableFactoryListener listener : tableFactoryListeners) {
            listener.onTableCreated(t);
        }
    }

    public Table getTable(String tableName) {
        return getTable(getSchema(),tableName);
    }

    private Table getTable(String schema,String tableName) {
        return getTable(getCatalog(),schema,tableName);
    }
    
    private Table getTable(String catalog,String schema,String tableName) {
        Table t = null;
        try {
            t = _getTable(catalog,schema,tableName);
            if(t == null && !tableName.equals(tableName.toUpperCase())) {
                t = _getTable(catalog,schema,tableName.toUpperCase());
            }
            if(t == null && !tableName.equals(tableName.toLowerCase())) {
                t = _getTable(catalog,schema,tableName.toLowerCase());
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        if(t == null) {
            Connection conn = DataSourceProvider.getConnection();
            try {
                throw new NotFoundTableException("not found table with give name:"+tableName+ (DatabaseMetaDataUtils.isOracleDataBase(DatabaseMetaDataUtils.getMetaData(conn)) ? " \n databaseStructureInfo:"+DatabaseMetaDataUtils.getDatabaseStructureInfo(DatabaseMetaDataUtils.getMetaData(conn),schema,catalog) : "")+"\n current "+DataSourceProvider.getDataSource()+" current schema:"+getSchema()+" current catalog:"+getCatalog());
            }finally {
                DBHelper.close(conn);
            }
        }
        dispatchOnTableCreatedEvent(t);
        return t;
    }
    
    public static class NotFoundTableException extends RuntimeException {
        private static final long serialVersionUID = 5976869128012158628L;
        public NotFoundTableException(String message) {
            super(message);
        }
    }

    private Table _getTable(String catalog,String schema,String tableName) throws SQLException {
        if(tableName== null || tableName.trim().length() == 0) 
             throw new IllegalArgumentException("tableName must be not empty");
        catalog = StringHelper.defaultIfEmpty(catalog, null);
        schema = StringHelper.defaultIfEmpty(schema, null);
        
        Connection conn = DataSourceProvider.getConnection();
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = dbMetaData.getTables(catalog, schema, tableName, null);
        try {
            while(rs.next()) {
                Table table = new TableCreateProcessor(conn,getSchema(),getCatalog()).createTable(rs);
                return table;
            }
        }finally {
            DBHelper.close(conn,rs);
        }
        return null;
    }

    public static class TableCreateProcessor {
        private Connection connection;
        private String catalog;
        private String schema;
        
        public String getCatalog() {
            return catalog;
        }

        public String getSchema() {
            return schema;
        }
        
        public TableCreateProcessor(Connection connection,String schema,String catalog) {
            super();
            this.connection = connection;
            this.schema = schema;
            this.catalog = catalog;
        }

        public Table createTable(ResultSet rs) throws SQLException {
            long start = System.currentTimeMillis();
            String tableName = null;
            try {
//                ResultSetMetaData rsMetaData = rs.getMetaData();
//                String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
                tableName = rs.getString("TABLE_NAME");
                String tableType = rs.getString("TABLE_TYPE");
                String remarks = rs.getString("REMARKS");
                if(remarks == null && DatabaseMetaDataUtils.isOracleDataBase(connection.getMetaData())) {
                    remarks = getOracleTableComments(tableName);
                }
                
                Table table = new Table();
                table.setSchema(schema);
                table.setCatalog(catalog);
                table.setSqlName(tableName);
                table.setRemarks(remarks);
                
                if ("SYNONYM".equals(tableType) && DatabaseMetaDataUtils.isOracleDataBase(connection.getMetaData())) {
                    String[] ownerAndTableName = getSynonymOwnerAndTableName(tableName);
                    table.setOwnerSynonymName(ownerAndTableName[0]);
                    table.setTableSynonymName(ownerAndTableName[1]);
                }
                
                retriveTableColumns(table);
                table.initExportedKeys(connection.getMetaData());
                table.initImportedKeys(connection.getMetaData());
                BeanHelper.copyProperties(table, TableOverrideValuesProvider.getTableConfigValues(table.getSqlName()));
                return table;
            }catch(SQLException e) {
                throw new RuntimeException("create table object error,tableName:"+tableName,e);
            }finally {
                GLogger.perf("createTable() cost:"+(System.currentTimeMillis()- start)+" tableName:"+tableName);
            }
        }
        
        private List<Table> getAllTables() throws SQLException {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet rs = dbMetaData.getTables(getCatalog(), getSchema(), null, null);
            try {
                List<Table> tables = new ArrayList<Table>();
                while(rs.next()) {
                    tables.add(createTable(rs));
                }
                return tables;
            }finally {
                DBHelper.close(rs);
            }
        }
    
        private String[] getSynonymOwnerAndTableName(String synonymName)  {
              PreparedStatement ps = null;
              ResultSet rs = null;
              String[] ret = new String[2];
              try {
                 ps = connection.prepareStatement("select table_owner,table_name from sys.all_synonyms where synonym_name=? and owner=?");
                 ps.setString(1, synonymName);
                 ps.setString(2, getSchema());
                 rs = ps.executeQuery();
                 if (rs.next()) {
                    ret[0] = rs.getString(1);
                    ret[1] = rs.getString(2);
                 }
                 else {
                    String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(getMetaData(),schema,catalog);
                    throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
                 }
              } catch (SQLException e) {
                 String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(getMetaData(),schema,catalog);
                 GLogger.error(e.getMessage(), e);
                 throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
              } finally {
                 DBHelper.close(null,ps,rs);
              }
              return ret;
        }
       
        private DatabaseMetaData getMetaData() {
            return DatabaseMetaDataUtils.getMetaData(connection);
        }
        
        private void retriveTableColumns(Table table) throws SQLException {
              GLogger.trace("-------setColumns(" + table.getSqlName() + ")");
    
              List primaryKeys = getTablePrimaryKeys(table);
              table.setPrimaryKeyColumns(primaryKeys);
              
              // get the indices and unique columns
              List indices = new LinkedList();
              // maps index names to a list of columns in the index
              Map uniqueIndices = new HashMap();
              // maps column names to the index name.
              Map uniqueColumns = new HashMap();
              ResultSet indexRs = null;
    
              try {
    
                 if (table.getOwnerSynonymName() != null) {
                    indexRs = getMetaData().getIndexInfo(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), false, true);
                 }
                 else {
                    indexRs = getMetaData().getIndexInfo(getCatalog(), getSchema(), table.getSqlName(), false, true);
                 }
                 while (indexRs.next()) {
                    String columnName = indexRs.getString("COLUMN_NAME");
                    if (columnName != null) {
                       GLogger.trace("index:" + columnName);
                       indices.add(columnName);
                    }
    
                    // now look for unique columns
                    String indexName = indexRs.getString("INDEX_NAME");
                    boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");
    
                    if (!nonUnique && columnName != null && indexName != null) {
                       List l = (List)uniqueColumns.get(indexName);
                       if (l == null) {
                          l = new ArrayList();
                          uniqueColumns.put(indexName, l);
                       }
                       l.add(columnName);
                       uniqueIndices.put(columnName, indexName);
                       GLogger.trace("unique:" + columnName + " (" + indexName + ")");
                    }
                 }
              } catch (Throwable t) {
                 // Bug #604761 Oracle getIndexInfo() needs major grants
                 // http://sourceforge.net/tracker/index.php?func=detail&aid=604761&group_id=36044&atid=415990
              } finally {
                 DBHelper.close(indexRs);
              }
    
              List columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);
    
              for (Iterator i = columns.iterator(); i.hasNext(); ) {
                 Column column = (Column)i.next();
                 //把首字母变为小写，这样假如pojo原有大写就不会冲突
                 column.setColumnName(column.getColumnNameFirstLower());
                
                 table.addColumn(column);
              }
    
              // In case none of the columns were primary keys, issue a warning.
              if (primaryKeys.size() == 0) {
                 GLogger.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
              }
        }
    
        private List getTableColumns(Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns) throws SQLException {
            // get the columns
              List columns = new LinkedList();
              ResultSet columnRs = getColumnsResultSet(table);
              try {
                  while (columnRs.next()) {
                     int sqlType = columnRs.getInt("DATA_TYPE");
                     String sqlTypeName = columnRs.getString("TYPE_NAME");
                     String columnName = columnRs.getString("COLUMN_NAME");
                     String columnDefaultValue = columnRs.getString("COLUMN_DEF");
                     
                     String remarks = columnRs.getString("REMARKS");
                     if(remarks == null && DatabaseMetaDataUtils.isOracleDataBase(connection.getMetaData())) {
                         remarks = getOracleColumnComments(table.getSqlName(), columnName);
                     }
                     
                     // if columnNoNulls or columnNullableUnknown assume "not nullable"
                     boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
                     int size = columnRs.getInt("COLUMN_SIZE");
                     int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");
        
                     boolean isPk = primaryKeys.contains(columnName);
                     boolean isIndexed = indices.contains(columnName);
                     String uniqueIndex = (String)uniqueIndices.get(columnName);
                     List columnsInUniqueIndex = null;
                     if (uniqueIndex != null) {
                        columnsInUniqueIndex = (List)uniqueColumns.get(uniqueIndex);
                     }
        
                     boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
                     if (isUnique) {
                        GLogger.trace("unique column:" + columnName);
                     }
                     Column column = new Column(
                           table,
                           sqlType,
                           sqlTypeName,
                           columnName,
                           size,
                           decimalDigits,
                           isPk,
                           isNullable,
                           isIndexed,
                           isUnique,
                           columnDefaultValue,
                           remarks);
                     BeanHelper.copyProperties(column,TableOverrideValuesProvider.getColumnConfigValues(table,column));
                     columns.add(column);
                }
            }finally {
                DBHelper.close(columnRs);
            }
            return columns;
        }
        
        private ResultSet getColumnsResultSet(Table table) throws SQLException {
            ResultSet columnRs = null;
            if (table.getOwnerSynonymName() != null) {
                 columnRs = getMetaData().getColumns(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), null);
            } else {
                 columnRs = getMetaData().getColumns(getCatalog(), getSchema(), table.getSqlName(), null);
            }
            return columnRs;
        }
    
        private List<String> getTablePrimaryKeys(Table table) throws SQLException {
            // get the primary keys
              List primaryKeys = new LinkedList();
              ResultSet primaryKeyRs = null;
              try {
              if (table.getOwnerSynonymName() != null) {
                 primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName());
              }
              else {
                 primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), getSchema(), table.getSqlName());
              }
              while (primaryKeyRs.next()) {
                 String columnName = primaryKeyRs.getString("COLUMN_NAME");
                 GLogger.trace("primary key:" + columnName);
                 primaryKeys.add(columnName);
              }
              }finally {
                  DBHelper.close(primaryKeyRs);
              }
              return primaryKeys;
        }
        
        // FIXME 如果是oracle同义词:Synonym, 需要根据 OwnerSynonymName及TableSynonymName 才能查找回oracle注释
        private String getOracleTableComments(String table)  {
            String sql = "SELECT comments FROM user_tab_comments WHERE table_name='"+table+"'";
            return ExecuteSqlHelper.queryForString(connection,sql);
        }
    
        private String getOracleColumnComments(String table,String column)  {
            String sql = "SELECT comments FROM user_col_comments WHERE table_name='"+table+"' AND column_name = '"+column+"'";
            return ExecuteSqlHelper.queryForString(connection,sql);
        }
    }
    
    /** 得到表的自定义配置信息 */
    public static class TableOverrideValuesProvider {
        
        private static Map getTableConfigValues(String tableSqlName){
            NodeData nd = getTableConfigXmlNodeData(tableSqlName);
            if(nd == null) {
                return new HashMap();
            }
            return nd == null ? new HashMap() : nd.attributes;
        }
    
        private static Map getColumnConfigValues(Table table, Column column) {
            NodeData root = getTableConfigXmlNodeData(table.getSqlName());
            if(root != null){
                 for(NodeData item : root.childs) {
                     if(item.nodeName.equals("column")) {
                         if(column.getSqlName().equalsIgnoreCase(item.attributes.get("sqlName"))) {
                             return item.attributes;
                         }
                     }
                 }
            }
            return new HashMap();
        }
        
        private static NodeData getTableConfigXmlNodeData(String tableSqlName){
            NodeData nd = getTableConfigXmlNodeData0(tableSqlName);
            if(nd == null) {
                nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase());
                if(nd == null) {
                    nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase());
                }
            }
            return nd;
        }

        private static NodeData getTableConfigXmlNodeData0(String tableSqlName) {
            try {
                File file = FileHelper.getFileByClassLoader("generator_config/table/"+tableSqlName+".xml");
                GLogger.trace("getTableConfigXml() load nodeData by tableSqlName:"+tableSqlName+".xml");
                return new XMLHelper().parseXML(file);
            }catch(Exception e) {//ignore
                GLogger.trace("not found config xml for table:"+tableSqlName+", exception:"+e);
                return null;
            }
        }
    }
    
    static class ExecuteSqlHelper {

        public static String queryForString(Connection conn,String sql) {
            Statement s = null;
            ResultSet rs = null;
            try {
                s =  conn.createStatement();
                rs = s.executeQuery(sql);
                if(rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }finally {
                DBHelper.close(null,s,rs);
            }
        }       
    }
    
    public static class DatabaseMetaDataUtils {
        public static boolean isOracleDataBase(DatabaseMetaData metadata) {
            try {
                boolean ret = false;
                ret = (metadata.getDatabaseProductName().toLowerCase()
                            .indexOf("oracle") != -1);
                return ret;
            }catch(SQLException s) {
                return false;
//              throw new RuntimeException(s);
            }
        }
        public static boolean isHsqlDataBase(DatabaseMetaData metadata) {
            try {
                boolean ret = false;
                ret = (metadata.getDatabaseProductName().toLowerCase()
                            .indexOf("hsql") != -1);
                return ret;
            }catch(SQLException s) {
                return false;
//              throw new RuntimeException(s);
            }
        }   
        public static boolean isMysqlDataBase(DatabaseMetaData metadata) {
            try {
                boolean ret = false;
                ret = (metadata.getDatabaseProductName().toLowerCase()
                            .indexOf("mysql") != -1);
                return ret;
            }catch(SQLException s) {
                return false;
//              throw new RuntimeException(s);
            }
        }
        
        public static DatabaseMetaData getMetaData(Connection connection) {
            try {
                return connection.getMetaData();
            }catch(SQLException e) {
                throw new RuntimeException("cannot get DatabaseMetaData",e);
            }
        }
        
        public static String getDatabaseStructureInfo(DatabaseMetaData metadata,String schema,String catalog) {
              ResultSet schemaRs = null;
              ResultSet catalogRs = null;
              String nl = System.getProperty("line.separator");
              StringBuffer sb = new StringBuffer(nl);
              // Let's give the user some feedback. The exception
              // is probably related to incorrect schema configuration.
              sb.append("Configured schema:").append(schema).append(nl);
              sb.append("Configured catalog:").append(catalog).append(nl);

              try {
                 schemaRs = metadata.getSchemas();
                 sb.append("Available schemas:").append(nl);
                 while (schemaRs.next()) {
                    sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
                 }
              } catch (SQLException e2) {
                 GLogger.warn("Couldn't get schemas", e2);
                 sb.append("  ?? Couldn't get schemas ??").append(nl);
              } finally {
                 DBHelper.close(schemaRs);
              }

              try {
                 catalogRs = metadata.getCatalogs();
                 sb.append("Available catalogs:").append(nl);
                 while (catalogRs.next()) {
                    sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
                 }
              } catch (SQLException e2) {
                 GLogger.warn("Couldn't get catalogs", e2);
                 sb.append("  ?? Couldn't get catalogs ??").append(nl);
              } finally {
                 DBHelper.close(catalogRs);
              }
              return sb.toString();
        }       
    }
}
