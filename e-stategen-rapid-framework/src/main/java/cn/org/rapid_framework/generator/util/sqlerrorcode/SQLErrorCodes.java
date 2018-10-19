/*
 * Copyright 2002-2006 the original author or authors.
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


/**
 * JavaBean for holding JDBC error codes for a particular database.
 * Instances of this class are normally loaded through a bean factory.
 *
 * <p>Used by Spring's SQLErrorCodeSQLExceptionTranslator.
 * The file "sql-error-codes.xml" in this package contains default
 * SQLErrorCodes instances for various databases.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @see SQLErrorCodesFactory
 * @see SQLErrorCodeSQLExceptionTranslator
 */
public class SQLErrorCodes {

	private String[] databaseProductNames;

	private boolean useSqlStateForTranslation = false;

	private String[] dataIntegrityViolationCodes = new String[0];
	
	/**
	 * Set this property to specify multiple database names that contains spaces,
	 * in which case we can not use bean names for lookup.
	 */
	public void setDatabaseProductNames(String... databaseProductNames) {
		this.databaseProductNames = databaseProductNames;
	}

	public String[] getDatabaseProductNames() {
		return databaseProductNames;
	}

	/**
	 * Set this property to true for databases that do not provide an error code
	 * but that do provide SQL State (this includes PostgreSQL).
	 */
	public void setUseSqlStateForTranslation(boolean useStateCodeForTranslation) {
		this.useSqlStateForTranslation = useStateCodeForTranslation;
	}

	public boolean isUseSqlStateForTranslation() {
		return useSqlStateForTranslation;
	}

	public void setDataIntegrityViolationCodes(String... dataIntegrityViolationCodes) {
		this.dataIntegrityViolationCodes = dataIntegrityViolationCodes;
	}

	public String[] getDataIntegrityViolationCodes() {
		return dataIntegrityViolationCodes;
	}

}
