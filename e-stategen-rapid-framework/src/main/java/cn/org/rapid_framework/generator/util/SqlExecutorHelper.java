package cn.org.rapid_framework.generator.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.Cleanup;

public class SqlExecutorHelper {

	public static List<Map<String, Object>> queryForList(Connection conn,String sql,int limit) throws SQLException {
	    @Cleanup
		PreparedStatement ps = conn.prepareStatement(sql.trim());
		ps.setMaxRows(limit);
		ps.setFetchDirection(ResultSet.FETCH_FORWARD);
		@Cleanup
		ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> result =  toListMap(limit, rs);
			return result;
	}
	
	public static boolean execute(DataSource ds,String sql) {
	    try {
	        @Cleanup
	        Connection conn = ds.getConnection();
			@Cleanup
			Statement s = conn.createStatement();
			return s.execute(sql);
		}catch(SQLException e) {
			throw new RuntimeException(e.getMessage()+" errorCode:"+e.getErrorCode()+" SQLState:"+e.getSQLState());
		}
	}

	public static List<Map<String, Object>> toListMap(int limit, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while(rs.next()) {
			Map<String, Object> row = new HashMap<>();
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				row.put(rsmd.getColumnName(i), rs.getObject(i));
			}
			list.add(row);
			count ++;
			if(count >= limit) {
				break;
			}
		}
		return list;
	}
	
}
