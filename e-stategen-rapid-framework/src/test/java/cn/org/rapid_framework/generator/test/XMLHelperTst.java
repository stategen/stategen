package cn.org.rapid_framework.generator.test;


import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.util.XMLHelper;

public class XMLHelperTst {
    @Test 
    public void testReader() throws SAXException, IOException {
        String xmlFileName ="D:\\works\\fm\\trade\\tables\\menu.xml";
        XMLHelper xmlHelper =new XMLHelper();
        
//        NodeData parseXML = xmlHelper.parseXML(new File(xmlFileName));
//        System.out.println("parseXML<===========>:" + parseXML);
        TableConfigXmlBuilder tableConfigXmlBuilder = new TableConfigXmlBuilder();
        tableConfigXmlBuilder.parseFromXML(new File(xmlFileName));
    }  
      
}
