/**
 * Copyright (C) 2021  niaoge<78493244@qq.com>
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

package org.stategen.framework.cloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.FunctionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author niaoge
 * @version $Id: ConfigStringStreamFunsProperties.java, v 0.1 2021年1月30日 上午5:18:20 XiaZhengsheng Exp $
 */
@ConfigurationProperties(prefix = "spring.cloud.stream")
@EnableConfigurationProperties(DefinitionAutoValueConfigration.class)
@Getter
@Setter
@Slf4j
@Configuration
@AutoConfigureBefore(FunctionConfiguration.class)
public class DefinitionAutoValueConfigration implements InitializingBean {
    
    /***这里用map代替List,key为methodname,value任意String,多个yml中,map可以合并,用来动态生成 spring.cloud.stream.function.definition*/
    private Map<String, String> definitionMethodMap;
    
    /***当原有的definition存在时,是否将 definitionMethodMap 中的 methodnames追加,默认 false*/
    private boolean overrideDefinitionIfExists;
    
    private static final String SPRING_CLOUD_STREAM_FUNCTION_DEFINITION = "spring.cloud.stream.function.definition";
    
    @Autowired
    private Environment environment;
    
    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        
        try {
            if (CollectionUtil.isNotEmpty(definitionMethodMap)) {
                String  definition     = environment.getProperty(SPRING_CLOUD_STREAM_FUNCTION_DEFINITION);
                boolean orgDefNotEmpty = StringUtil.isNotEmpty(definition);
                if (!overrideDefinitionIfExists && orgDefNotEmpty) {
                    return;
                }
                
                if (orgDefNotEmpty) {
                    String[]        defs   = definition.split(";");
                    HashSet<String> defSet = new HashSet<String>(Arrays.asList(defs));
                    
                    Set<String> methodkeySet = definitionMethodMap.keySet();
                    if (defSet.containsAll(methodkeySet)) {
                        return;
                    }
                    
                    for (String def : defSet) {
                        definitionMethodMap.putIfAbsent(def, def);
                    }
                }
                
                String              methodNames = String.join(";", definitionMethodMap.keySet());
                Map<String, Object> defMap      = new HashMap<String, Object>(1);
                defMap.put(SPRING_CLOUD_STREAM_FUNCTION_DEFINITION, methodNames);
                MutablePropertySources propertySources = ((StandardEnvironment) environment).getPropertySources();
                propertySources.addFirst(new MapPropertySource("dynamicDefinition", defMap));
                
            }
        } finally {
            //gc clear
            definitionMethodMap = null;
        }
        
    }
    
}
