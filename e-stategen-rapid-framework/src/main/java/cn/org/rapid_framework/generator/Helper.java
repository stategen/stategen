/*
 * copy from rapid-framework<code.google.com/p/rapid-framework> and modify by niaoge
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
package cn.org.rapid_framework.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;

public class Helper {
    
    
    public static List<String> getTableConfigFiles(File basedir) {
        String[] tableConfigFilesArray = basedir.list();
        List<String> result = new ArrayList<>();
        for(String str : tableConfigFilesArray) {
            if(str.endsWith(".xml")) {
                result.add(str);
            }
        }
        return result;
    }
    
    
    public static Collection<TableConfig> getTableConfigs(TableConfigSet tableConfigSet,String tableSqlName) {
        if("*".equals(tableSqlName)) {
            return tableConfigSet.getTableConfigs();
        }else {
            TableConfig tableConfig = tableConfigSet.getBySqlName(tableSqlName);
            if(tableConfig == null) {
                throw new RuntimeException("\n#####>"+tableSqlName+"表不存在或者表对应的xml没有创建<#####");
            }
            List<TableConfig> tableConfigs= Arrays.asList(tableConfig);
            return tableConfigs;
        }
    }

    public static GeneratorFacade createGeneratorFacade(String outRootDir,String... templateRootDirs) {
        if(templateRootDirs == null) throw new IllegalArgumentException("templateRootDirs must be not null");
        if(outRootDir == null) throw new IllegalArgumentException("outRootDir must be not null");

        GeneratorFacade gf = new GeneratorFacade();
        gf.getGenerator().setTemplateRootDirs(templateRootDirs);
        gf.getGenerator().setOutRootDir(outRootDir);
        return gf;
    }
    
    
    
}