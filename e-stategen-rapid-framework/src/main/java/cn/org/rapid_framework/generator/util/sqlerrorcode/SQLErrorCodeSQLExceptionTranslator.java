/*
 * Copyright 2002-2007 the original author or authors.
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

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;

/**
 * Implementation of SQLExceptionTranslator that analyzes vendor-specific error codes.
 * More precise than an implementation based on SQL state, but vendor-specific.
 *
 * <p>This class applies the following matching rules:
 * <ul>
 * <li>Try custom translation implemented by any subclass. Note that this class is
 * concrete and is typically used itself, in which case this rule doesn't apply.
 * <li>Apply error code matching. Error codes are obtained from the SQLErrorCodesFactory
 * by default. This factory loads a "sql-error-codes.xml" file from the class path,
 * defining error code mappings for database names from database metadata.
 * <li>Fallback to a fallback translator. SQLStateSQLExceptionTranslator is the
 * default fallback translator, analyzing the exception's SQL state only.
 * </ul>
 *
 * <p>The configuration file named "sql-error-codes.xml" is by default read from
 * this package. It can be overridden through a file of the same name in the root
 * of the class path (e.g. in the "/WEB-INF/classes" directory).
 *
 * @author Rod Johnson
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @see SQLErrorCodesFactory
 * @see SQLStateSQLExceptionTranslator
 */
public class SQLErrorCodeSQLExceptionTranslator {

    /** Error codes used by this translator */
    private SQLErrorCodes    sqlErrorCodes;

    /**
     * Constructor for use as a JavaBean.
     * The SqlErrorCodes or DataSource property must be set.
     */
    public SQLErrorCodeSQLExceptionTranslator() {
    }

    /**
     * Create a SQL error code translator for the given DataSource.
     * Invoking this constructor will cause a Connection to be obtained
     * from the DataSource to get the metadata.
     * @param dataSource DataSource to use to find metadata and establish
     * which error codes are usable
     * @see SQLErrorCodesFactory
     */
    public SQLErrorCodeSQLExceptionTranslator(DataSource dataSource) {
        setDataSource(dataSource);
    }

    /**
     * Create a SQL error code translator for the given database product name.
     * Invoking this constructor will avoid obtaining a Connection from the
     * DataSource to get the metadata.
     * @param dbName the database product name that identifies the error codes entry
     * @see SQLErrorCodesFactory
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    public SQLErrorCodeSQLExceptionTranslator(String dbName) {
        setDatabaseProductName(dbName);
    }

    /**
     * Create a SQLErrorCode translator given these error codes.
     * Does not require a database metadata lookup to be performed using a connection.
     * @param sec error codes
     */
    public SQLErrorCodeSQLExceptionTranslator(SQLErrorCodes sec) {
        this.sqlErrorCodes = sec;
    }

    /**
     * Set the DataSource for this translator.
     * <p>Setting this property will cause a Connection to be obtained from
     * the DataSource to get the metadata.
     * @param dataSource DataSource to use to find metadata and establish
     * which error codes are usable
     * @see SQLErrorCodesFactory#getErrorCodes(javax.sql.DataSource)
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    public void setDataSource(DataSource dataSource) {
        this.sqlErrorCodes = SQLErrorCodesFactory.getInstance().getErrorCodes(dataSource);
    }

    /**
     * Set the database product name for this translator.
     * <p>Setting this property will avoid obtaining a Connection from the DataSource
     * to get the metadata.
     * @param dbName the database product name that identifies the error codes entry
     * @see SQLErrorCodesFactory#getErrorCodes(String)
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    public void setDatabaseProductName(String dbName) {
        this.sqlErrorCodes = SQLErrorCodesFactory.getInstance().getErrorCodes(dbName);
    }

    /**
     * Set custom error codes to be used for translation.
     * @param sec custom error codes to use
     */
    public void setSqlErrorCodes(SQLErrorCodes sec) {
        this.sqlErrorCodes = sec;
    }

    /**
     * Return the error codes used by this translator.
     * Usually determined via a DataSource.
     * @see #setDataSource
     */
    public SQLErrorCodes getSqlErrorCodes() {
        return sqlErrorCodes;
    }

    /** 判断是否是外键,完整性约束等异常 引发的异常 */
    public boolean isDataIntegrityViolation(SQLException e) {
        // Check SQLErrorCodes with corresponding error code, if available.
        if (this.sqlErrorCodes != null) {
            String errorCode = null;
            if (this.sqlErrorCodes.isUseSqlStateForTranslation()) {
                errorCode = e.getSQLState();
            } else {
                errorCode = Integer.toString(e.getErrorCode());
            }

            if(ignoreByCustom(errorCode)) {
            	return true;
            }
            
            if (errorCode != null) {
            	if(Arrays.asList(GeneratorProperties.getStringArray(GeneratorConstants.SQLPARSE_IGNORE_SQL_EXCEPTION_ERROR_CODES)).contains(errorCode)){
            		return true;
            	}
                if (Arrays.asList(sqlErrorCodes.getDataIntegrityViolationCodes()).contains(errorCode)) {
                    return true;
                }
            }
        }
        return false;
    }

	protected boolean ignoreByCustom(String errorCode) {
		return false;
	}
    
    public static SQLErrorCodeSQLExceptionTranslator getSQLErrorCodeSQLExceptionTranslator(DataSource ds) {
        SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator();
        translator.setDataSource(ds);
        return translator;
    }

}
