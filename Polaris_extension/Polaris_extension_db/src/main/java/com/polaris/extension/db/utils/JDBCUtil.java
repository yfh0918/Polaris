package com.polaris.extension.db.utils;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.ConfClient;
import com.polaris.core.datasource.DynamicDataSource;
import com.polaris.core.util.SpringUtil;

abstract public class JDBCUtil {

	private static final Logger logger =  LoggerFactory.getLogger(JDBCUtil.class);

	private static final int FETCHSIZE = 10000;
	private static final String QUEUEEND = "end";
	
	/**
	 * jdbc 游标方式 执行Sql
	 * @param sql
	 * @param queue
	 * @author: XuChuanHou
	 */
	public static void buildQueryTOQueue(String sql, LinkedBlockingQueue<Map<String, Object>> queue) {
		DataSource dSource = SpringUtil.getBean(DynamicDataSource.class);
		buildQueryTOQueue(sql,queue,dSource);
	}
	public static void buildQueryTOQueue(String sql, LinkedBlockingQueue<Map<String, Object>> queue, DataSource dSource) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rSet = null;
		try {
			connection = dSource.getConnection();
			ps = connection.prepareStatement(sql);
			rSet = ps.executeQuery();
			long cur1 = System.currentTimeMillis();
			long i = 0;
			rSet.setFetchDirection(ResultSet.FETCH_FORWARD);
			rSet.setFetchSize(FETCHSIZE);

			while (rSet.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>();

				ResultSetMetaData rsmd = rSet.getMetaData();// rs为查询结果集
				int count = rsmd.getColumnCount();
				for (int j = 1; j <= count; j++) {
					rowData.put(rsmd.getColumnName(j).toUpperCase(), rSet.getObject(rsmd.getColumnName(j)));
				}
				queue.put(rowData);

				i++;
				if (i % FETCHSIZE == 0) {
					logger.debug("i={} 耗时:{}秒", "1", Long.toString((System.currentTimeMillis() - cur1) / 1000));
					cur1 = System.currentTimeMillis();
				}
			}
			/* 添加 结束标志*/
			Map<String, Object> endData = new HashMap<>();
			endData.put(QUEUEEND, QUEUEEND);
			queue.put(endData);
			logger.info("buildQueryTOQueue  将sql 查询数据放入队列结束");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if (rSet != null) {
				try {
					rSet.close();
					rSet = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	
	/**
	 * jdbc 游标方式 执行Sql
	 * @param sql
	 * @param queue
	 * @author: XuChuanHou
	 */
	public List<Map<String, Object>> getQueryResult(String sql) {
		DataSource dSource = SpringUtil.getBean(DynamicDataSource.class);
		return getQueryResult(sql,dSource);
	}
	public static List<Map<String, Object>> getQueryResult(String sql, DataSource dSource) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rSet = null;
		List<Map<String, Object>> resultList = new ArrayList<>();

		try {
			connection = dSource.getConnection();
			ps = connection.prepareStatement(sql);
			rSet = ps.executeQuery();
			rSet.setFetchDirection(ResultSet.FETCH_FORWARD);
			rSet.setFetchSize(FETCHSIZE);

			while (rSet.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>();

				ResultSetMetaData rsmd = rSet.getMetaData();// rs为查询结果集
				int count = rsmd.getColumnCount();
				for (int j = 1; j <= count; j++) {
					rowData.put(rsmd.getColumnName(j).toUpperCase(), rSet.getObject(rsmd.getColumnName(j)));
				}
				resultList.add(rowData);
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if (rSet != null) {
				try {
					rSet.close();
					rSet = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return resultList;
	}

    /**
     * 查询数量
     *
     * @param sql
     * @author: 
     */
    public static long queryCount(String sql) {
    	DataSource dSource = SpringUtil.getBean(DynamicDataSource.class);
    	return queryCount(sql,dSource);
    }
    public static long queryCount(String sql,DataSource dSource) {
		
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = dSource.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                    statement = null;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return 0;
    }
	
	/**
	 * 批量提交SQL
	 * @param sqlList
	 * @author: XuChuanHou
	 */
	public static boolean saveBatch(List<String> sqlList) {
		DataSource dSource = SpringUtil.getBean(DynamicDataSource.class);
		return saveBatch(sqlList,dSource);
	}
	public static boolean saveBatch(List<String> sqlList,DataSource dSource) {
		
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dSource.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			if (sqlList != null) {
				for (String sql : sqlList) {
					statement.addBatch(sql);
				}
			}
			statement.executeBatch();
			connection.commit();
			return true;
		} catch (Exception ex) {
			if(ex instanceof BatchUpdateException){
                BatchUpdateException bException = (BatchUpdateException)ex;
                int[] s = bException.getUpdateCounts();
                if(s.length+1<sqlList.size()){                    
                    logger.info("更新失败数据:"+sqlList.get(s.length));
                    List<String> sList = sqlList.subList(s.length+1, sqlList.size());                    
                    saveBatch(sList);
                }
            } else {
            	logger.error(ex.getMessage());
            	if (connection != null) {
    				try {
    					connection.rollback();
    				} catch (SQLException e) {
    					logger.error(e.getMessage());
    				}
    			}
            }
		} finally {
			if (statement != null) {
				try {
					statement.close();
					statement = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 根据MAP 构建SQL
	 * </p>
	 * 
	 * @param tbName
	 * @param fields
	 * @param timeFields
	 * @return
	 * @author: XuChuanHou
	 */
	public static String buildSqlByMap(String tbName, Map<String, Object> fields, String... timeFields) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("INSERT INTO ").append(tbName).append("(");

		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append(") values(");
		String driverName = ConfClient.get("db.driverClassName","");
		List<String> timeFieldList = null;
		if (timeFields != null && timeFields.length > 0) {
			timeFieldList = Arrays.asList(timeFields);
		}

		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			sqlBuilder.append(entry.getKey()).append(",");
			if (timeFieldList != null && timeFieldList.contains(entry.getKey())) {
				if (driverName.toLowerCase().contains("oracle")) {
					valueBuilder.append("SYSDATE,");
				} else if (driverName.toLowerCase().contains("mysql")) {
					valueBuilder.append("SYSDATE(),");
				}
			} else {
				valueBuilder.append("'").append(entry.getValue()).append("'").append(",");
			}
		}
		String headSql = sqlBuilder.substring(0, sqlBuilder.length() - 1)
				+ valueBuilder.substring(0, valueBuilder.length() - 1) + ")";

		return headSql;
	}
}
