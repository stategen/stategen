package cn.org.rapid_framework.generator.ext.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Property;

public class PropertiesLoadTask extends Property {

    @Override
    public void execute() throws BuildException {
        super.execute();
    }
    
    @Override
    protected void loadFile(File file) throws BuildException {
        if(file.getName().endsWith(".xml")) {
            try {
                Properties props = new Properties();
                FileInputStream in = new FileInputStream(file);
                props.loadFromXML(in);
                in.close();
                addProperties(props);
            }catch(IOException e) {
                throw new BuildException("load properties occer error:"+file+" cause:"+e,e);
            }
        }else {
            super.loadFile(file);
        }
    }
}
