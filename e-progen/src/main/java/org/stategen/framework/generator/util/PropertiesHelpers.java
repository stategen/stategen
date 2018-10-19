/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.generator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * The Class PropertiesHelpers.
 */
public class PropertiesHelpers {
    public static Properties load(String...files) throws InvalidPropertiesFormatException, IOException {
        Properties properties = new Properties();
        for(String f : files) {
            File file = FileHelpers.getFile(f);
            InputStream input = new FileInputStream(file);
            try {
                if(file.getPath().endsWith(".xml")){
                    properties.loadFromXML(input);
                }else {
                    properties.load(input);
                }
                properties.putAll(properties);
            }finally {
                input.close();
            }
        }
        return properties;
    }
}