package org.stategen.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import lombok.Cleanup;

public class XmlUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(XmlUtil.class);

    public static String appendToNode(String xmlFileName, String nodeName, String value) throws DocumentException, IOException {
        SAXReader sr = new SAXReader(); // 需要导入jar包:dom4j
        // 关联xml
        Document document = sr.read(xmlFileName);

        // 获取根元素
        Element root = document.getRootElement();
        boolean append = appendToNode(root, nodeName, value);
        if (append) {
            String asXML = document.asXML();
//            saveDocument(document, new File(xmlFileName));
            return asXML;
        }
        return null;
    }

    public static boolean appendToNode(Element parent, String nodeName, String value) {
        int nodeCount = parent.nodeCount();
        boolean findNode = false;
        Node findValue = null;
        for (int i = 0; i < nodeCount; i++) {
            Node node = parent.node(i);
            String name = node.getName();
            if (nodeName.equals(name)) {
                findNode = true;
                String stringValue = node.getStringValue();
                if (stringValue.equals(value)) {
                    findValue = node;
                }
            }
        }

        if (findNode) {
            if (findValue == null) {
                Element ele = parent.addElement(nodeName);
                ele.setText(value);
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuffer("添加节点:").append(ele.asXML()).toString());
                }
                return true;
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuffer("节点存在:").append(findValue.asXML()).toString());
                }
            }
            return false;
        }

        for (int i = 0; i < nodeCount; i++) {
            Node node = parent.node(i);
            if (node instanceof Element) {
                boolean append =appendToNode((Element) node, nodeName, value);
                if (append){
                    return append;
                }
            }
        }
        return false;
    }

    // 下面的为固定代码---------可以完成java对XML的写,改等操作
    public static void saveDocument(Document document, File xmlFile) throws IOException {
        @Cleanup
        Writer osWrite = new OutputStreamWriter(new FileOutputStream(xmlFile),StringUtil.UTF_8);// 创建输出流
        OutputFormat format = OutputFormat.createPrettyPrint(); // 获取输出的指定格式
        format.setEncoding(document.getXMLEncoding());  
        format.setNewLineAfterDeclaration(false);//解决声明下空行问题  
        
        format.setEncoding(StringUtil.UTF_8);// 设置编码 ，确保解析的xml为UTF-8格式
        @Cleanup
        XMLWriter writer = new XMLWriter(osWrite, format);// XMLWriter
        writer.write(document);// 把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)
        writer.flush();
    }

}
