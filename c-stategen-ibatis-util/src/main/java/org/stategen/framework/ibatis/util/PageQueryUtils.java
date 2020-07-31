package org.stategen.framework.ibatis.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.lite.PageList;
import org.stategen.framework.util.NumberUtil;

/**
 * Ibatis的分页查询工具类. 将为分页查询提供附加的四个参数
 * 基于0开始的: offset, limit 用于mysql分页查询
 * 基于1开始的: startRow,endRow 用于oracle分页查询
 * 该类复制自rapid_framework
 * @author badqiu
 * @version $Id: PageQueryUtils.java,v 0.1 2010-7-28 下午04:45:27 badqiu Exp $
 */
public class PageQueryUtils {
    final static Logger logger = LoggerFactory.getLogger(PageQueryUtils.class);

    /**
     * 封装ibatis的分页查询
     * @param sqlMapClientTemplate
     * @param statementName
     * @param parameterObject
     * @return
     */
    public static <T> PageList<T> pageQuery(SqlDaoSupport sqlMapClientTemplate,
                                     String statementName, PageQuery parameterObject) {
        return pageQuery(sqlMapClientTemplate, statementName, statementName + "_count",
            parameterObject, parameterObject.getPage(), parameterObject.getPageSize());
    }

    /**
     * 封装ibatis的分页查询   
     * @param sqlMapClientTemplate
     * @param statementName
     * @param countStatementName count查询sql,用于查询count总数
     * @param parameterObject
     * @return
     */
    public static <T> PageList<T> pageQuery(SqlDaoSupport sqlMapClientTemplate,
                                     String statementName, String countStatementName,
                                     PageQuery parameterObject) {
        return pageQuery(sqlMapClientTemplate, statementName, countStatementName, parameterObject,
            parameterObject.getPage(), parameterObject.getPageSize());
    }

    /**
     * 封装ibatis的分页查询
     * @return
     */
    public static <T> PageList<T> pageQuery(SqlDaoSupport sqlMapClientTemplate,
                                     String statementName, Object parameterObject, int page,
                                     int pageSize) {
        return pageQuery(sqlMapClientTemplate, statementName, statementName + "_count",
            parameterObject, page, pageSize);
    }

    /**
     * 封装ibatis的分页查询
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static <T> PageList<T> pageQuery(SqlDaoSupport sqlMapClientTemplate,
                                     String statementName, String countStatementName,
                                     Object parameterObject, int page, int pageSize) {

        Number totalCount = (Number) sqlMapClientTemplate.selectOne(countStatementName,  parameterObject);

        if (NumberUtil.isGreatZero(totalCount)) {
            Paginator paginator = new Paginator(page, pageSize, totalCount.intValue());
            Map<String, Integer> otherParams = new HashMap<String, Integer>();
            otherParams.put("offset", paginator.getOffset());
            otherParams.put("limit", paginator.getLimit());
            otherParams.put("startRow", paginator.getStartRow());
            otherParams.put("endRow", paginator.getEndRow());

            MapAndObject mapAndObject = new MapAndObject((Map)otherParams, parameterObject);
            List<T> list = sqlMapClientTemplate.selectList(statementName, mapAndObject);
            return new PageList<T>(list,paginator.getPage(), paginator.getPageSize(), paginator.getTotalItems());
        }
        return new PageList<T>(0, pageSize, 0);
    }

}
