package org.stategen.framework.generator.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import lombok.Cleanup;

public class IbatisXmlToMybatis {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IbatisXmlToMybatis.class);

    /**
     * @param srcXmlText    源XML文本
     */

    public static String transformXmlByXslt(String srcXmlText) {

        try {
            long before = System.nanoTime();
            // 获取转换器工厂
            TransformerFactory tf         = TransformerFactory.newInstance();
            @Cleanup
            InputStream        xsltStream = IbatisXmlToMybatis.class.getResourceAsStream("ibatis2mybatis/migrate.xslt");
            // 获取转换器对象实例
            Transformer           transformer           = tf
                .newTransformer(new StreamSource(new InputStreamReader(xsltStream)));
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
            Long after =System.nanoTime();
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuilder("转换经过时间：").append((after-before)/1000000L).append(" 毫秒").toString());
            }
            
            
            //            <!-- add any needed jdbc type here (for example :CLOB#, :FLOAT#, :REAL#, :BIT#, :INTEGER#, :DECIMAL#, :DATE#, :TIME#, .... ) 
            //            <replace dir="destination" includes="*.xml" token=":???#" value=",jdbcType=???#" encoding="UTF8"/>
            //            -->
            //            <!-- replace $id$ with ${id} -->
            //            <replaceregexp match="\$([a-zA-Z0-9.\[\]_]+)\$" replace="$\{\1}" flags="mg" byline="false" encoding="UTF8">
            //                <fileset dir="destination" includes="*.xml" />
            //            </replaceregexp>
            ////            <!-- replace #id# with #{id} -->
            //            <replaceregexp match="#([a-zA-Z0-9,_.=\[\]]{2,})#" replace="#{\1}" flags="mg" byline="false" encoding="UTF8">
            //                <fileset dir="destination" includes="*.xml" />
            //            </replaceregexp>
            ////            <!-- replace xyz[] with item for use in iterators-->
            //            <replaceregexp match="[a-z.]{2,}\[\]" replace="item" flags="ig" encoding="UTF8">
            //                <fileset dir="destination" includes="*.xml" />
            //            </replaceregexp>
            //            

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 

    }

}
