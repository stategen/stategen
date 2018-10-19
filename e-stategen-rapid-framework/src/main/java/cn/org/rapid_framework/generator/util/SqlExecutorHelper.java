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

public class SqlExecutorHelper {

	public static List<Map> queryForList(Connection conn,String sql,int limit) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql.trim());
		ps.setMaxRows(limit);
		ps.setFetchDirection(ResultSet.FETCH_FORWARD);
		ResultSet rs = ps.executeQuery();
		try {
			List result =  toListMap(limit, rs);
			return result;
		}finally {
			DBHelper.close(rs);
		}
	}
	
	public static boolean execute(DataSource ds,String sql) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			Statement s = conn.createStatement();
			return s.execute(sql);
		}catch(SQLException e) {
			throw new RuntimeException(e.getMessage()+" errorCode:"+e.getErrorCode()+" SQLState:"+e.getSQLState());
		}finally {
			DBHelper.close(conn);
		}
	}

	public static List<Map> toListMap(int limit, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = 0;
		List<Map> list = new ArrayList<Map>();
		while(rs.next()) {
			Map row = new HashMap();
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
