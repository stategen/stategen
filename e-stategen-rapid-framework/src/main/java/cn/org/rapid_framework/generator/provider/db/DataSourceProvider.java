package cn.org.rapid_framework.generator.provider.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.StringHelper;
/**
 * 用于提供生成器的数据源
 * 
 * @author badqiu
 *
 */
public class DataSourceProvider {
	private static Connection connection;
	private static DataSource dataSource;

	public synchronized static Connection getNewConnection() {
		try {
			return getDataSource().getConnection();
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static Connection getConnection() {
		try {
			if(connection == null || connection.isClosed()) {
				connection = getDataSource().getConnection();
			}
			return connection;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setDataSource(DataSource dataSource) {
		DataSourceProvider.dataSource = dataSource;
	}

	public synchronized static DataSource getDataSource() {
		if(dataSource == null) {
			dataSource = lookupJndiDataSource(GeneratorProperties.getProperty(GeneratorConstants.DATA_SOURCE_JNDI_NAME));
			if(dataSource == null) {
				dataSource = new DriverManagerDataSource();
			}
		}
		return dataSource;
	}

	private static DataSource lookupJndiDataSource(String name) {
		if(StringHelper.isBlank(name)) return null;
		
		try {
			Context context = new InitialContext();
			return (DataSource) context.lookup(name);
		}catch(NamingException e) {
			GLogger.warn("lookup generator dataSource fail by name:"+name+" cause:"+e.toString()+",retry by jdbc_url again");
			return null;
		}
	}
	
	public static class DriverManagerDataSource implements DataSource {
		
		private static void loadJdbcDriver(String driverClass) {
			try {
				if(driverClass == null || "".equals(driverClass.trim())) {
					throw new IllegalArgumentException("jdbc 'driverClass' must not be empty");
				}
				Class.forName(driverClass.trim());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("not found jdbc driver class:["+driverClass+"]",e);
			}
		}
		
		public DriverManagerDataSource() {
		}

		public Connection getConnection() throws SQLException {
			loadJdbcDriver(getDriverClass());
			return DriverManager.getConnection(getUrl(),getUsername(),getPassword());
		}

		public Connection getConnection(String username, String password) throws SQLException {
			loadJdbcDriver(getDriverClass());
			return DriverManager.getConnection(getUrl(),username,password);
		}

		public PrintWriter getLogWriter() throws SQLException {
			throw new UnsupportedOperationException("getLogWriter");
		}

		public int getLoginTimeout() throws SQLException {
			throw new UnsupportedOperationException("getLoginTimeout");
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			throw new UnsupportedOperationException("setLogWriter");
		}

		public void setLoginTimeout(int seconds) throws SQLException {
			throw new UnsupportedOperationException("setLoginTimeout");
		}

		public <T> T  unwrap(Class<T> iface) throws SQLException {
			if(iface == null) throw new IllegalArgumentException("Interface argument must not be null");
			if (!DataSource.class.equals(iface)) {
				throw new SQLException("DataSource of type [" + getClass().getName() +
						"] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
			}
			return (T) this;
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return DataSource.class.equals(iface);
		}

		private String getUrl() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_URL);
		}

		private String getUsername() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_USERNAME);
		}

		private String getPassword() {
			return GeneratorProperties.getProperty(GeneratorConstants.JDBC_PASSWORD);
		}

		private String getDriverClass() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_DRIVER);
		}
		
		public String toString() {
			return "DataSource: "+"url="+getUrl()+" username="+getUsername();
		}

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
	}
}
