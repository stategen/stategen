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
class FreeMarkerTempFileContext {
    private Configuration conf = null;
    private List<FtlFile> ftlFiles = null;

    public FreeMarkerTempFileContext(List<String> tempPaths) throws IOException {
        String[] tempPathArray = new String[tempPaths.size()];
        tempPaths.toArray(tempPathArray);
        conf = TemplateHelpers.getConfiguration(tempPathArray);
        ftlFiles = new ArrayList<FtlFile>();

        for (String tempPath : tempPaths) {
            File tempPathFile = FileHelpers.getFile(tempPath);
            if (tempPathFile.exists() && tempPathFile.isDirectory()) {
                List<File> allSubFiles = FileHelpers.searchAllNotIgnoreFile(tempPathFile);
                for (File tempFile : allSubFiles) {
                    if (tempFile.isFile() && !tempFile.getName().endsWith(".include.ftl")) {
                        ftlFiles.add(new FtlFile(tempPathFile, tempFile));
                    }
                }
            }
        }

    }

    public Configuration getConf() {
        return conf;
    }


    public List<FtlFile> getFtlFiles() {
        return ftlFiles;
    }

}