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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.progen.wrap.ApiWrap;
import org.stategen.framework.progen.wrap.BaseWrap;
import org.stategen.framework.progen.wrap.BeanWrap;
import org.stategen.framework.progen.wrap.CanbeImportWrap;
import org.stategen.framework.progen.wrap.FieldWrap;
import org.stategen.framework.progen.wrap.MemberWrap;
import org.stategen.framework.progen.wrap.SimpleWrap;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CopyUtil;
import org.stategen.framework.util.StringUtil;

/**
 * The Class WrapContainer.
 */
public class WrapContainer {

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WrapContainer.class);

    private Map<PathType, Map<Class<?>, CanbeImportWrap>> wrapMaps = new HashMap<PathType, Map<Class<?>, CanbeImportWrap>>();

    /**基本数据类型,integer,long ,string,void等转换  */
    private Map<Class<?>, SimpleWrap> simpleWrapMap = new HashMap<Class<?>, SimpleWrap>();
    private Map<Class<?>, SimpleWrap> simpleWrapMapNoPath = new HashMap<Class<?>, SimpleWrap>();
    
    /***simpleClassName*/
    
    private Set<String> simpleClassNameSet = new HashSet<String>();

    public WrapContainer() {
        PathType[] PathTypes = PathType.values();
        for (PathType pathType : PathTypes) {
            wrapMaps.put(pathType, new HashMap<Class<?>, CanbeImportWrap>());
        }
    }

    public void registSimpleClz(Class<?> dest, String convertTo,String wholeImportPath) {
        SimpleWrap simpleWrap = new SimpleWrap(dest, convertTo);
        simpleWrapMap.put(dest, simpleWrap);
        
        if (StringUtil.isNotEmpty(wholeImportPath)){
            simpleWrap.setWholeImportPath(wholeImportPath);
        } else {
            simpleWrapMapNoPath.put(dest, simpleWrap);
        }
    }
    

    public boolean checkIsOrgSimple(Class<?> clz){
        clz = ClassHelpers.getClazzIfCollection(clz); 
        return simpleWrapMapNoPath.containsKey(clz);
    }
    
    public Map<Class<?>, CanbeImportWrap> getCanbeImportWrapMapByPathType(PathType pathType) {
        return wrapMaps.get(pathType);
    }

    public SimpleWrap checkAndGetFromSimple(Class<?> clz) {
        clz = ClassHelpers.getClazzIfCollection(clz);
        SimpleWrap simpleWrap = simpleWrapMap.get(clz);
        return simpleWrap;
    }

    public BaseWrap get(Class<?> clz) {
        clz = ClassHelpers.getClazzIfCollection(clz);
        SimpleWrap simpleWrap = checkAndGetFromSimple(clz);
        if (simpleWrap != null) {
            return simpleWrap;
        }

        return checkInWrapMap(clz);
    }

    private BaseWrap checkInWrapMap(Class<?> clz) {
        for (Map<Class<?>, CanbeImportWrap> wrapMap : wrapMaps.values()) {
            CanbeImportWrap baselWrap = wrapMap.get(clz);
            if (baselWrap != null) {
                return (BaseWrap) baselWrap;
            }
        }
        return null;
    }

    private PathType getEnumOrBeanPathTypeByClz(Class<?> clz, boolean isApi) {
        if (isApi) {
            return PathType.API;
        }

        return clz.isEnum() ? PathType.ENUM : PathType.BEAN;
    }

    public BaseWrap add(Class<?> clz, boolean isApi) {
        clz = ClassHelpers.getClazzIfCollection(clz);
        BaseWrap baseWrap = get(clz);
        if (baseWrap == null) {
            String simpleName = clz.getSimpleName();
            AssertUtil.mustTrue(!simpleClassNameSet.contains(simpleName), StringUtil.concatNoNull(simpleName, " 不能存在相同的类名,否则在生成前端代码时会相互覆盖"));

            PathType pathType = getEnumOrBeanPathTypeByClz(clz, isApi);
            CanbeImportWrap canbeImportWrap = BeanUtils.instantiate(pathType.getCanbeImportWrapClass());

            Map<Class<?>, CanbeImportWrap> wrapMap = this.getCanbeImportWrapMapByPathType(pathType);
            wrapMap.put(clz, canbeImportWrap);

            //去重
            simpleClassNameSet.add(simpleName);

            canbeImportWrap.setClazz(clz);
            baseWrap = (BaseWrap) canbeImportWrap;
        }

        return baseWrap;
    }

    public <T extends MemberWrap> T genMemberWrap(ApiWrap apiWrap, Class<?> rawClass, Type genericType, Class<T> memberWrapClz,
                                                  AnnotatedElement annotatedElement) {
        rawClass = ClassHelpers.getClazzIfCollection(rawClass);
        BaseWrap ownClazzWrap = add(rawClass, false);

        T memberWrap = CopyUtil.copy(ownClazzWrap, memberWrapClz);
        memberWrap.setOwnClazzWrap(ownClazzWrap);
        memberWrap.setApiWap(apiWrap);

        if (ownClazzWrap.getIsMap()) {
            return memberWrap;
        }


        //创建比如泛型子类,不加入到Field，return,param中
        Class<?> genericClazz = GenericTypeResolver.getClass(genericType, 0);
        genericClazz = ClassHelpers.getClazzIfCollection(genericClazz);
        if (genericClazz != rawClass) {
            BaseWrap genericWrap = add(genericClazz, false);
            memberWrap.setGenericWrap(genericWrap);

            if (ownClazzWrap.getIsGeneric() && ownClazzWrap.getClazz() == genericWrap.getClazz()) {
                AssertUtil.throwException("请给泛型参数或返回值配上具体的类型!" + annotatedElement);
            }
        }

        return memberWrap;
    }

    public void scanBeanRelationShipAndMakeFeilds() {
        scanBeanRelationShip();
        scanBeanFieldsImports();
    }

    private void scanBeanFieldsImports() {
        Map<Class<?>, CanbeImportWrap> wrapMap = this.getCanbeImportWrapMapByPathType(PathType.BEAN);
        for (CanbeImportWrap baseBeanWrap : wrapMap.values()) {
            BeanWrap beanWrap = (BeanWrap) baseBeanWrap;
            Class<?> currentType = beanWrap.getClazz();
            Map<String, FieldWrap> fieldWrapMap = beanWrap.getFieldWrapMap();

            for (FieldWrap fieldWrap : fieldWrapMap.values()) {
                if (fieldWrap.getIsArray()) {
                    continue;
                }

                Class<?> fieldRealType = fieldWrap.getClazz();
                if (currentType != fieldRealType && needAddImports(fieldRealType)) {
                    BaseWrap fieldTypeWrap = this.get(fieldRealType);
                    beanWrap.addImport(fieldTypeWrap);
                }
            }
        }
    }

    private void scanBeanRelationShip() {
        Map<Class<?>, CanbeImportWrap> wrapMap = this.getCanbeImportWrapMapByPathType(PathType.BEAN);
        List<Class<?>> clzs = new ArrayList<Class<?>>(wrapMap.keySet());
        for (Class<?> clz : clzs) {
            Class<?> superclass = clz.getSuperclass();
            BeanWrap beanWrap = (BeanWrap) wrapMap.get(clz);
            while (superclass != null && !(superclass == Object.class)) {
                BeanWrap parentBean = (BeanWrap) wrapMap.get(superclass);
                if (parentBean != null) {
                    beanWrap.setParentBean(parentBean);
                    break;
                }
                superclass = superclass.getSuperclass();
            }
        }
    }

    public boolean needAddImports(Class<?> clz) {
        clz = ClassHelpers.getClazzIfCollection(clz);
        if (ClassHelpers.isArrayOrMap(clz)){
            return false;
        }
        
        if (clz.equals(Void.TYPE)) {
            return false;
        }
        
        return true;
    }
    
    

}
