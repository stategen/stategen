package cn.org.rapid_framework.generator.test;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.stategen.framework.util.XmlUtil;

public class XmlUtilTst {
    public static void main(String[] args) throws DocumentException, IOException {
        String xml = "D:\\works\\fm\\trade-test\\pom.xml";
        XmlUtil.appendToNode(xml, "module", "7-trade-web-cms");
    }

}
