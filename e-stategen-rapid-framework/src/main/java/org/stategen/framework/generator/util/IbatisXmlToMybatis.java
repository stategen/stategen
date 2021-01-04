package org.stategen.framework.generator.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import cn.org.rapid_framework.generator.GeneratorProperties;

import lombok.Cleanup;
import lombok.SneakyThrows;

public class IbatisXmlToMybatis {
    
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IbatisXmlToMybatis.class);
    
    /**
     * @param srcXmlText    源XML文本
     */
    
    @SneakyThrows
    public static String transformXmlByXslt(String srcXmlText) {
        
        long before = System.nanoTime();
        // 获取转换器工厂
        TransformerFactory tf         = TransformerFactory.newInstance();
        @Cleanup
        InputStream        xsltStream = IbatisXmlToMybatis.class.getResourceAsStream("ibatis2mybatis/migrate.xslt");
        
        String migrateText = replaceUtilMethod(xsltStream);
        
        // 获取转换器对象实例
        //            Transformer           transformer           = tf.newTransformer(new StreamSource(new InputStreamReader(xsltStream)));
        Transformer           transformer           = tf.newTransformer(new StreamSource(new StringReader(migrateText)));
        @Cleanup
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamResult          streamResult          = new StreamResult(byteArrayOutputStream);
        // 进行转换
        StreamSource xmlStreamSource = new StreamSource(new StringReader(srcXmlText));
        transformer.transform(xmlStreamSource, streamResult);
        String result = byteArrayOutputStream.toString("utf-8");
        
        //<!-- replace #id:NUMERIC# to #id,jdbcType=NUMERIC# etc. -->
        result = result.replace(":NUMERIC#", ",jdbcType=NUMERIC#");
        result = result.replace(":TIMESTAMP#", ",jdbcType=TIMESTAMP#");
        result = result.replace(":VARCHAR#", ",jdbcType=VARCHAR#");
        result = result.replace(":BLOB#", ",jdbcType=BLOB#");
        
        result = AntReplaceUtil.replaceAll(result, "\\$([a-zA-Z0-9.\\[\\]_]+)\\$", "$\\{\\1}", "mg");
        result = AntReplaceUtil.replaceAll(result, "#([a-zA-Z0-9,_.=\\[\\]]{2,})#", "#{\\1}", "mg");
        result = AntReplaceUtil.replaceAll(result, "[a-z.]{2,}\\[\\]", "item", "ig");
        Long after = System.nanoTime();
        if (logger.isInfoEnabled()) {
            logger.info(new StringBuilder("转换经过时间：").append((after - before) / 1000000L).append(" 毫秒").toString());
        }
        
        return result;
        
    }
    
    @SneakyThrows
    private static String replaceUtilMethod(InputStream xsltStream) {
        StringBuilder sb = new StringBuilder();
        String        line;
        
        @Cleanup
        InputStreamReader inputStreamReader = new InputStreamReader(xsltStream);
        
        @Cleanup
        BufferedReader br = new BufferedReader(inputStreamReader);
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String migrateText = sb.toString();
        
        Properties properties = GeneratorProperties.getProperties();
        /*
         * <entry key="mybatis_isNotEmpty">@util.Check@isNotEmpty</entry> <entry
         * key="mybatis_isEmpty">@util.Check@isEmpty</entry>
         */
        String mybatis_isNotEmpty = "mybatis_isNotEmpty";
        String mybatis_isEmpty    = "mybatis_isEmpty";
        
        String mybatis_isNotEmptyV = properties.getProperty(mybatis_isNotEmpty, "@util.Check@isNotEmpty");
        String mybatis_isEmptyV    = properties.getProperty(mybatis_isEmpty, "@util.Check@isEmpty");
        migrateText = migrateText.replace("${" + mybatis_isNotEmpty + "}", mybatis_isNotEmptyV);
        migrateText = migrateText.replace("${" + mybatis_isEmpty + "}", mybatis_isEmptyV);
        return migrateText;
    }
    
}
