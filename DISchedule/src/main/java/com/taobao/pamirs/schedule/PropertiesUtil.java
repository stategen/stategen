package com.taobao.pamirs.schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import lombok.Cleanup;

public class PropertiesUtil {

    public static Properties loadPropertiesFromClasspath(final String propertiesFileName) {
        Properties  result      = null;
        InputStream imputStream = AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
                                    public InputStream run() {
                                        ClassLoader cl = Thread.currentThread().getContextClassLoader();
                                        if (cl != null) {
                                            return cl.getResourceAsStream(propertiesFileName);
                                        } else {
                                            return ClassLoader.getSystemResourceAsStream(propertiesFileName);
                                        }
                                    }
                                });

        if (null != imputStream) {
            try {
                result = new Properties();
                if (propertiesFileName.endsWith(".xml")) {
                    result.loadFromXML(imputStream);
                } else {
                    result.load(imputStream);
                }
                imputStream.close();
            } catch (java.io.IOException e) {
                // skip
            }
        }
        return result;
    }

    public static String getStringProperty(String name, Properties properties) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            //skip
        }
        return (prop == null) ? properties.getProperty(name) : prop;
    }

    public static Properties load(String... files) throws InvalidPropertiesFormatException, IOException {
        Properties properties = new Properties();
        for (String f : files) {
            File        file  = new File(f);
            @Cleanup
            InputStream input = new FileInputStream(file);
            if (file.getPath().endsWith(".xml")) {
                properties.loadFromXML(input);
            } else {
                properties.load(input);
            }
            properties.putAll(properties);
        }
        return properties;
    }

}
