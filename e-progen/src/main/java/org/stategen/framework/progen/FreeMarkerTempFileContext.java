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
package org.stategen.framework.progen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.TemplateHelpers;

import freemarker.template.Configuration;

/**
 * The Class FreeMarkerTempFileContext.
 */
class FreeMarkerTempFileContext{
    private Configuration conf =null;
    private List<File> fltFiles=null;
    private File tempPathFile =null;
    
    public FreeMarkerTempFileContext(String tempPath,String sharePath) throws IOException{
        conf = TemplateHelpers.getConfiguration(tempPath,sharePath);
        tempPathFile = FileHelpers.getFile(tempPath);
        List<File> allFiles = FileHelpers.searchAllNotIgnoreFile(tempPathFile);
        fltFiles = new ArrayList<File>();
        for (File file : allFiles) {
            if (file.isFile() && !file.getName().endsWith(".include.ftl")) {
                fltFiles.add(file);
            }
        }
    }
    
    public Configuration getConf() {
        return conf;
    }
    
    public List<File> getFltFiles() {
        return fltFiles;
    }
    
    public File getTempPathFile() {
        return tempPathFile;
    }
}