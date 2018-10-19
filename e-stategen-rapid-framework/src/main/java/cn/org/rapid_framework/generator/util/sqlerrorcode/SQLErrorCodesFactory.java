/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.org.rapid_framework.generator.util.sqlerrorcode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import cn.org.rapid_framework.generator.util.DBHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.PatternMatchHelper;

/**
 * Factory for creating {@link SQLErrorCodes} based on the
 * "databaseProductName" taken from the {@link java.sql.DatabaseMetaData}.
 *
 * <p>Returns <code>SQLErrorCodes</code> populated with vendor codes
 * defined in a configuration file named "sql-error-codes.xml".
 * Reads the default file in this package if not overridden by a file in
 * the root of the class path (for example in the "/WEB-INF/classes" directory).
 *
 * @author Thomas Risberg
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
 */
public class SQLErrorCodesFactory {


	/**
	 * Keep track of a single instance so we can return it to classes that request it.
	 */
	private static final SQLErrorCodesFactory instance = new SQLErrorCodesFactory();


	/**
	 * Return the singleton instance.
	 */
	public static SQLErrorCodesFactory getInstance() {
		return instance;
	}

	/**
	 * Map to hold error codes for all databases defined in the config file.
	 * Key is the database product name, value is the SQLErrorCodes instance.
	 */
	private final Map<String, SQLErrorCodes> errorCodesMap = new LinkedHashMap<String, SQLErrorCodes>();

	/**
	 * Map to cache the SQLErrorCodes instance per DataSource.
	 */
	private final Map<DataSource, SQLErrorCodes> dataSourceCache = new WeakHashMap<DataSource, SQLErrorCodes>(16);


	public SQLErrorCodesFactory() {
        //ORA-01400: 无法将NULL插入 
	    //ORA-01722: 无效数字 
	    //ORA-02291: 外键约束
	    //ORA-02292: An attempt was made to delete a row that is referenced by a foreign key. 
	    
	    //ORA-12899: value too large for column "APAYFUND"."PDC_CARD"."CARD_NO" (actual: 32, maximum: 16)
        //ORA-00001  unique constraint "APAYFUND"."PDC_CARD"."CARD_NO" violated
	    //ORA-02290  check constraint "APAYFUND"."PDC_CARD"."CARD_NO" violated
	    //ORA-01461: can bind a LONG value only for insert into a LONG column
	    //ORA-01438: value larger than specified precision allowed for this column
	    
        //ms-sql
        //1215  SQLSTATE: HY000 (ER_CANNOT_ADD_FOREIGN) Cannot add foreign key constraint
        //1216  SQLSTATE: 23000 (ER_NO_REFERENCED_ROW) Cannot add or update a child row: a foreign key constraint fails
        //1217  SQLSTATE: 23000 (ER_ROW_IS_REFERENCED) Cannot delete or update a parent row: a foreign key constraint fails
        
	    //MySQL
	    /*
	     * 1452: 不能删除或更新父行，外键约束失败(%s)。
	     * 1453: 消息：不能添加或更新子行，外键约束失败(%s)。错误： 
	     * 1217: 无法添加或更新子行，外键约束失败。
	     * 1218: 无法删除或更新父行，外键约束失败。
	     * 1062: Duplicate entry '123' for key
	     * 1048 SQLState:23000 errorCodeTranslatorDataBaaseName:[MySQL] Column 'reason_detail' cannot be null
	     */
	    
	    //PostgreSQL
	    /*
        23000   违反完整性约束（INTEGRITY CONSTRAINT VIOLATION）
        23001   违反限制（RESTRICT VIOLATION）
        23502   违反非空（NOT NULL VIOLATION）
        23503   违反外键约束（FOREIGN KEY VIOLATION）
        23505   违反唯一约束（UNIQUE VIOLATION）
        23514   违反检查（CHECK VIOLATION）
	     */
	    
        // e.getErrorCode() e.getSQLState() 
	    
	    errorCodesMap.put("DB2", newSQLErrorCodes(false,"DB2*","-407,-530,-531,-532,-543,-544,-545,-603,-667"));
	    errorCodesMap.put("Derby", newSQLErrorCodes(true,"Apache Derby","22001,22005,23502,23503,23513,X0Y32"));
	    errorCodesMap.put("H2", newSQLErrorCodes(false,"H2","22003,22012,22025,23000,23001,23002,23003")); // http://www.h2database.com/javadoc/org/h2/constant/ErrorCode.html
	    errorCodesMap.put("HSQL", newSQLErrorCodes(false,"HSQL Database Engine","-9"));
	    errorCodesMap.put("Informix", newSQLErrorCodes(false,"Informix","-692,-11030"));
	    errorCodesMap.put("MS-SQL", newSQLErrorCodes(false,"Microsoft SQL Server","2627,8114,8115"));
	    errorCodesMap.put("MySQL", newSQLErrorCodes(false,"MySQL","1217,1218,1452,1453,1062,1406,1048,630,839,840,893,1169,1215,1216,1217,1218,1451,1452,1453,1557,1264")); //spring error code定义:630,839,840,893,1169,1215,1216,1217,1218,1451,1452,1453,1557
	    errorCodesMap.put("Oracle", newSQLErrorCodes(false,"Oracle","2291,2292,1400,1722,12899,1,2290,1461,1438")); // TODO spring定义: 2291,2292,1400,1722
	    errorCodesMap.put("PostgreSQL", newSQLErrorCodes(true,"PostgreSQL","23001,23503,23514")); //全部约束: "23000,23001,23502,23503,23505,23514"
	    errorCodesMap.put("Sybase", newSQLErrorCodes(false,"Sybase SQL Server,SQL Server,Adaptive Server Enterprise,ASE,sql server","233,423,511,515,530,547,2615,2714"));
	}
	
	public SQLErrorCodes newSQLErrorCodes(boolean useStateCodeForTranslation,String databaseProductNames,String dataIntegrityViolationCodes) {
	    SQLErrorCodes r = new SQLErrorCodes();
	    r.setDatabaseProductNames(databaseProductNames.split(","));
	    r.setDataIntegrityViolationCodes(dataIntegrityViolationCodes.split(","));
	    r.setUseSqlStateForTranslation(useStateCodeForTranslation);
	    return r;
	}
	
	/**
	 * Return the {@link SQLErrorCodes} instance for the given database.
	 * <p>No need for a database metadata lookup.
	 * @param dbName the database name (must not be <code>null</code>)
	 * @return the <code>SQLErrorCodes</code> instance for the given database
	 * @throws IllegalArgumentException if the supplied database name is <code>null</code>
	 */
	public SQLErrorCodes getErrorCodes(String dbName) {
		if(dbName == null) throw new IllegalArgumentException("Database product name must not be null");

		SQLErrorCodes sec = this.errorCodesMap.get(dbName);
		if (sec == null) {
			for (SQLErrorCodes candidate : this.errorCodesMap.values()) {
				if (PatternMatchHelper.simpleMatch(candidate.getDatabaseProductNames(), dbName)) {
					sec = candidate;
					break;
				}
			}
		}
		if (sec != null) {
			GLogger.debug("SQL error codes for '" + dbName + "' found");
			return sec;
		}

		// Could not find the database among the defined ones.
		GLogger.debug("SQL error codes for '" + dbName + "' not found");
		return new SQLErrorCodes();
	}

	/**
	 * Return {@link SQLErrorCodes} for the given {@link DataSource},
	 * evaluating "databaseProductName" from the
	 * {@link java.sql.DatabaseMetaData}, or an empty error codes
	 * instance if no <code>SQLErrorCodes</code> were found.
	 * @param dataSource the <code>DataSource</code> identifying the database
	 * @return the corresponding <code>SQLErrorCodes</code> object
	 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
	 */
	public SQLErrorCodes getErrorCodes(DataSource dataSource) {


		synchronized (this.dataSourceCache) {
			// Let's avoid looking up database product info if we can.
			SQLErrorCodes sec = this.dataSourceCache.get(dataSource);
			if (sec != null) {
				return sec;
			}
			// We could not find it - got to look it up.
			Connection conn = null;
			try {
			    conn = dataSource.getConnection();
                String dbName = (String) conn.getMetaData().getDatabaseProductName();;
    			if (dbName != null) {
    				GLogger.debug("Database product name cached for DataSource [" +
    						dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode()) +
    						"]: name is '" + dbName + "'");
    				sec = getErrorCodes(dbName);
    				this.dataSourceCache.put(dataSource, sec);
    				return sec;
    			}
			} catch(SQLException e) {
			    DBHelper.close(conn);
			    throw new IllegalStateException("canot getErrorCodes by dataSource",e);
			}
		}

		// Fallback is to return an empty SQLErrorCodes instance.
		return new SQLErrorCodes();
	}
	
	public String getDatabaseType(DataSource ds) {
	    Connection conn = null; 
	    try {
	        conn = ds.getConnection();
	        String dbName = (String) conn.getMetaData().getDatabaseProductName();
	        
	        for (String database : this.errorCodesMap.keySet()) {
	            SQLErrorCodes candidate = errorCodesMap.get(database);
                if (database.equals(dbName) || PatternMatchHelper.simpleMatch(candidate.getDatabaseProductNames(), dbName)) {
                    return database;
                }
            }
	        return null;
	    }catch(SQLException e) {
	        DBHelper.close(conn);
	        throw new IllegalStateException("canot get database type by dataSource",e);
	    }
	}

}
