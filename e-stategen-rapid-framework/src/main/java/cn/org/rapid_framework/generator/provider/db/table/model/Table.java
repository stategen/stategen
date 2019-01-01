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
package cn.org.rapid_framework.generator.provider.db.table.model;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.stategen.framework.lite.CaseInsensitiveHashMap;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.Setting;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.provider.db.sql.model.SqlParameter;
import cn.org.rapid_framework.generator.util.StringHelper;

/**
 * 用于生成代码的Table对象.对应数据库的table
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class Table implements java.io.Serializable,Cloneable {

    /**  */
    private static final long serialVersionUID = 1L;
    String sqlName;
    String remarks;
    String className;
    
    /** the name of the owner of the synonym if this table is a synonym */
    private String ownerSynonymName = null;
    /** real table name for oracle SYNONYM */
    private String tableSynonymName = null; 
    
    ColumnSet columns = new ColumnSet();
    Map<String, Column> _columnMap =new CaseInsensitiveHashMap<Column>();
    Map<String, SqlParameter> _fieldParameterMap =new CaseInsensitiveHashMap<SqlParameter>();
    
    List<Column> primaryKeyColumns = new ArrayList<Column>();
    
    public Table() {}
    
    public Table(Table t) {
        setSqlName(t.getSqlName());
        this.remarks = t.getRemarks();
        this.className = t.getClassName();
        this.ownerSynonymName = t.getOwnerSynonymName();
        setColumns(t.getColumns());
        this.primaryKeyColumns = t.getPrimaryKeyColumns();
        this.tableAlias = t.getTableAlias();
        this.exportedKeys = t.exportedKeys;
        this.importedKeys = t.importedKeys;
    }
    
    public LinkedHashSet<Column> getColumns() {
        return columns.getColumns();
    }
    
    public void putParameterAsField(SqlParameter parameter) {
        String paramName = parameter.getParamName();
        if (!_columnMap.containsKey(paramName)){
            CollectionUtil.putIfNotExists(_fieldParameterMap, paramName, parameter);
        }
    }
    
    public List<SqlParameter> getFieldParameters() {
        return CollectionUtil.newArrayList(_fieldParameterMap.values());
    }
    

    public LinkedHashSet<Column> getCanSelColumns() {
        LinkedHashSet<Column> result =new LinkedHashSet<>();
        LinkedHashSet<Column> columns = this.columns.getColumns();

        for (Column column:columns ) {
            String sqlName = column.getSqlName().toUpperCase();
            if (Setting.created_date_fields.containsKey(sqlName)){
                if (!Setting.gen_select_create_date_field){
                    continue;
                }
            }
            
            if (Setting.updated_date_fields.containsKey(sqlName)){
                if (!Setting.gen_select_update_date_field){
                    continue;
                }
            }
            
            if (Setting.soft_delete_fields.containsKey(sqlName)){
                if (!Setting.gen_select_delete_flag_field){
                    continue;
                }
            }
            
            result.add(column);
        }
        return  result;
    }

    public void setColumns(LinkedHashSet<Column> columns) {
        this.columns.setColumns(columns);
        this._columnMap.clear();
        CollectionUtil.toMap(_columnMap, columns,Column::getColumnName);
    }
    public String getOwnerSynonymName() {
        return ownerSynonymName;
    }
    public void setOwnerSynonymName(String ownerSynonymName) {
        this.ownerSynonymName = ownerSynonymName;
    }
    public String getTableSynonymName() {
        return tableSynonymName;
    }
    public void setTableSynonymName(String tableSynonymName) {
        this.tableSynonymName = tableSynonymName;
    }

    /** 使用 getPkColumns() 替换*/
    @Deprecated
    public List<Column> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }
    
//    public List<Column> getPKColumns() {
//        return primaryKeyColumns;
//    }
    
    /** 使用 setPkColumns() 替换*/
    @Deprecated
    public void setPrimaryKeyColumns(List<Column> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }
    
//    public void setPKColumns(List<Column> primaryKeyColumns) {
//        this.primaryKeyColumns = primaryKeyColumns;
//    }
    
    /** 数据库中表的表名称,其它属性很多都是根据此属性派生 */
    public String getSqlName() {
        return sqlName;
    }
    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }
    
    /** 数据库中表的表备注 */
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public void addColumn(Column column) {
        columns.addColumn(column);
        this._columnMap.put(column.getColumnName(), column);
    }
    
    public void setClassName(String customClassName) {
        this.className = customClassName;
    }
    /**
     * 根据sqlName得到的类名称，示例值: UserInfo
     * @return
     */
    public String getClassName() {
        if(StringUtil.isBlank(className)) {
            return StringHelper.toJavaClassName(sqlName);
        }else {
            return className;
        }
    }
    /** 数据库中表的别名，等价于:  getRemarks().isEmpty() ? getClassName() : getRemarks() */
    public String getTableAlias() {
        if(StringUtil.isNotBlank(tableAlias)) return tableAlias;
        return StringHelper.removeCrlf(StringHelper.defaultIfEmpty(getRemarks(), getClassName()));
    }
    public void setTableAlias(String v) {
        this.tableAlias = v;
    }
    
    /**
     * 等价于getClassName().toLowerCase()
     * @return
     */
    public String getClassNameLowerCase() {
        return getClassName().toLowerCase();
    }
    /**
     * 得到用下划线分隔的类名称，如className=UserInfo,则underscoreName=user_info
     * @return
     */
    public String getUnderscoreName() {
        return StringHelper.toUnderscoreName(getClassName()).toLowerCase();
    }
    /**
     * 返回值为getClassName()的第一个字母小写,如className=UserInfo,则ClassNameFirstLower=userInfo
     * @return
     */
    public String getClassNameFirstLower() {
        return StringHelper.uncapitalize(getClassName());
    }
    
    /**
     * 根据getClassName()计算而来,用于得到常量名,如className=UserInfo,则constantName=USER_INFO
     * @return
     */
    public String getConstantName() {
        return StringHelper.toUnderscoreName(getClassName()).toUpperCase();
    }
    
    /** 使用 getPkCount() 替换*/
    @Deprecated
    public boolean isSingleId() {
        return getPkCount() == 1 ? true : false;
    }
    
    /** 使用 getPkCount() 替换*/
    @Deprecated
    public boolean isCompositeId() {
        return getPkCount() > 1 ? true : false;
    }

    /** 使用 getPkCount() 替换*/
    @Deprecated
    public boolean isNotCompositeId() {
        return !isCompositeId();
    }
    
    /**
     * 得到主键总数
     * @return
     */
    public int getPkCount() {
        return columns.getPkCount();
    }
    /**
     * use getPkColumns()
     * @deprecated 
     */
    public List<Column> getCompositeIdColumns() {
        return getPkColumns();
    }
    
    /**
     * 得到是主键的全部column
     * @return
     */ 
    public List<Column> getPkColumns() {
        return columns.getPkColumns();
    }
    
    /**
     * 得到不是主键的全部column
     * @return
     */
    public List<Column> getNotPkColumns() {
        return columns.getNotPkColumns();
    }
    
    /** 得到单主键，等价于getPkColumns().get(0)  */
    public Column getPkColumn() {
        Column c = columns.getPkColumn();
        if(c == null) {
            throw new IllegalStateException("not found primary key on table:"+getSqlName());
        }
        return c;
    }
    
    /**使用 getPkColumn()替换 */
    @Deprecated
    public Column getIdColumn() {
        return getPkColumn();
    }
    
    public List<Column> getEnumColumns() {
        return columns.getEnumColumns();   
    }
    
    public Column getColumnByName(String name) {
        return columns.getByName(name);
    }
    
    public Column getColumnBySqlName(String sqlName) {
        return columns.getBySqlName(sqlName);
    }
    
   public Column getRequiredColumnBySqlName(String sqlName) {
       if(getColumnBySqlName(sqlName) == null) {
           throw new IllegalArgumentException("not found column with sqlName:"+sqlName+" on table:"+getSqlName());
       }
       return getColumnBySqlName(sqlName);
    }
    
    /**
     * 忽略过滤掉某些关键字的列,关键字不区分大小写,以逗号分隔
     * @param ignoreKeywords
     * @return
     */
    public List<Column> getIgnoreKeywordsColumns(String ignoreKeywords) {
        List<Column> results = new ArrayList<Column>();
        for(Column c : getColumns()) {
            String sqlname = c.getSqlName().toLowerCase();
            if(StringHelper.contains(sqlname,ignoreKeywords.split(","))) {
                continue;
            }
            results.add(c);
        }
        return results;
    }
    
    /**
     * This method was created in VisualAge.
     */
    public void initImportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException {
        
               // get imported keys a
    
               ResultSet fkeys = dbmd.getImportedKeys(catalog,schema,this.sqlName);

               while ( fkeys.next()) {
                 String pktable = fkeys.getString(PKTABLE_NAME);
                 String pkcol   = fkeys.getString(PKCOLUMN_NAME);
//                 String fktable = fkeys.getString(FKTABLE_NAME);
                 String fkcol   = fkeys.getString(FKCOLUMN_NAME);
                 String seq     = fkeys.getString(KEY_SEQ);
                 Integer iseq   = new Integer(seq);
                 getImportedKeys().addForeignKey(pktable,pkcol,fkcol,iseq);
               }
               fkeys.close();
    }
    
    /**
     * This method was created in VisualAge.
     */
    public void initExportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException {
               // get Exported keys
    
               ResultSet fkeys = dbmd.getExportedKeys(catalog,schema,this.sqlName);
              
               while ( fkeys.next()) {
//                 String pktable = fkeys.getString(PKTABLE_NAME);
                 String pkcol   = fkeys.getString(PKCOLUMN_NAME);
                 String fktable = fkeys.getString(FKTABLE_NAME);
                 String fkcol   = fkeys.getString(FKCOLUMN_NAME);
                 String seq     = fkeys.getString(KEY_SEQ);
                 Integer iseq   = new Integer(seq);
                 getExportedKeys().addForeignKey(fktable,fkcol,pkcol,iseq);
               }
               fkeys.close();
    }
    
    /**
     * @return Returns the exportedKeys.
     */
    public ForeignKeys getExportedKeys() {
        if (exportedKeys == null) {
            exportedKeys = new ForeignKeys(this);
        }
        return exportedKeys;
    }
    /**
     * @return Returns the importedKeys.
     */
    public ForeignKeys getImportedKeys() {
        if (importedKeys == null) {
            importedKeys = new ForeignKeys(this);
        }
        return importedKeys;
    }
    
    @Override
    public String toString() {
        return "[  getColumns()=" + getColumns() + ", getOwnerSynonymName()=" + getOwnerSynonymName()
               + ", getTableSynonymName()=" + getTableSynonymName() + ", getPrimaryKeyColumns()="
               + getPrimaryKeyColumns() + ", getSqlName()=" + getSqlName() + ", getRemarks()=" + getRemarks()
               + ", getClassName()=" + getClassName() + ", getTableAlias()=" + getTableAlias()
               + ", getClassNameLowerCase()=" + getClassNameLowerCase() + ", getUnderscoreName()="
               + getUnderscoreName() + ", getClassNameFirstLower()=" + getClassNameFirstLower()
               + ", getConstantName()=" + getConstantName() + ", isSingleId()=" + isSingleId() + ", isCompositeId()="
               + isCompositeId() + ", isNotCompositeId()=" + isNotCompositeId() + ", getPkCount()=" + getPkCount()
               + ", getCompositeIdColumns()=" + getCompositeIdColumns() + ", getPkColumns()=" + getPkColumns()
               + ", getNotPkColumns()=" + getNotPkColumns() + ", getPkColumn()=" + getPkColumn() + ", getIdColumn()="
               + getIdColumn() + ", getEnumColumns()=" + getEnumColumns() + ", getExportedKeys()=" + getExportedKeys()
               + ", getImportedKeys()=" + getImportedKeys() + "]";
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            //ignore
            return null;
        }
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    String catalog = null;
    String schema = null;
    
    private String tableAlias;
    private ForeignKeys exportedKeys;
    private ForeignKeys importedKeys;
    
    public    static final String PKTABLE_NAME  = "PKTABLE_NAME";
    public    static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
    public    static final String FKTABLE_NAME  = "FKTABLE_NAME";
    public    static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
    public    static final String KEY_SEQ       = "KEY_SEQ";
    
}
