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

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.stategen.framework.lite.CaseInsensitiveHashMap;
import org.stategen.framework.util.CollectionUtil;

import cn.org.rapid_framework.generator.util.GLogger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

/**
 * The Class ASTHelper.
 */
class ASTHelper {

    /**
     * Gets the one nodes by type.
     *
     * @param <N> the number type
     * @param container the container
     * @param clazz the clazz
     * @return the one nodes by type
     */
    @SuppressWarnings("unchecked")
    static <N extends Node> N getOneNodesByType(Node container, Class<N> clazz) {
        for (Node child : container.getChildNodes()) {
            if (clazz.isInstance(child)) {
                return (N) child;
            }
            Node result = getOneNodesByType(child, clazz);
            if (result != null)
                return (N) result;
        }
        return null;
    }

    /**
     * Gets the type index.
     *
     * @param <N> the number type
     * @param container the container
     * @param clazz the clazz
     * @return the type index
     */
    static <N extends Node> Integer getTypeIndex(Node container, Class<N> clazz) {
        List<Node> children = container.getChildNodes();
        if (CollectionUtil.isNotEmpty(children)) {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                if (clazz.isInstance(child)) {
                    return i;
                }
                Integer temIdx = getTypeIndex(child, clazz);
                if (temIdx != null)
                    return temIdx;
            }
        }

        return null;
    }

    /**
     * Gets the nodes by type.
     *
     * @param <N> the number type
     * @param container the container
     * @param clazz the clazz
     * @return the nodes by type
     */
    static <N extends Node> List<N> getNodesByType(Node container, Class<N> clazz) {
        List<N> nodes = new ArrayList<N>();
        boolean find = false;
        for (Node child : container.getChildNodes()) {
            if (clazz.isInstance(child)) {
                nodes.add(clazz.cast(child));
                find = true;
            }
            if (!find) {
                nodes.addAll(getNodesByType(child, clazz));
            }
        }
        return nodes;
    }

    /**
     * Prints the self and children.
     *
     * @param container the container
     */
    static void printSelfAndChildren(Node container) {
        GLogger.println("当前类为:" + container.getClass() + "...................");
        for (Node child : container.getChildNodes()) {
            GLogger.info("第一级子类为：" + child.getClass() + "..................." + child);
        }
        GLogger.println("打印结束");
        GLogger.println("\n\n\n");
    }

    /**
     * Gets the field declaration map.
     *
     * @param <T> the generic type
     * @param node the class declaration
     * @param type the type
     * @return the field declaration map
     */
    static <T extends BodyDeclaration> Map<String, T> getBodyDeclarationMap(Node node, Class<T> type) {
        List<Node> children = node.getChildNodes();
        if (CollectionUtil.isEmpty(children)) {
            return new HashMap<String, T>(0);
        }

        Map<String, T> bodyMap = new CaseInsensitiveHashMap<T>(children.size());

        List<T> bodyDeclarations = getNodesByType(node, type);

        for (T bodyDeclaration : bodyDeclarations) {
            if (bodyDeclaration instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
                VariableDeclarator variableDeclaratorId = getOneNodesByType(bodyDeclaration, VariableDeclarator.class);
                bodyMap.put(variableDeclaratorId.getName().toString(), bodyDeclaration);
            } else if (bodyDeclaration instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                bodyMap.put(methodDeclaration.getNameAsString(), bodyDeclaration);
            }
        }
        return bodyMap;
    }

    /**
     * Gets the field map.
     *
     * @param node the node
     * @return the field map
     */
    static Map<String, String> getFieldMap(Node node) {
        Map<String, String> fieldmap = null;
        Map<String, FieldDeclaration> fieldDeclarationMap = getBodyDeclarationMap(node, FieldDeclaration.class);
        if (fieldDeclarationMap != null) {
            fieldmap = new CaseInsensitiveHashMap<String>(fieldDeclarationMap.size());
            for (String oldFieldName : fieldDeclarationMap.keySet()) {
                fieldmap.put(oldFieldName, oldFieldName);
            }
        }
        return fieldmap;
    }

    /**
     * Adds the annotaion declare.
     *
     * @param oldAnnotationExprs the last annotation exprs
     * @param bodyDeclaration the body declaration
     * @return true, if successful
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    static boolean addAnnotationExprsToBody(NodeList<AnnotationExpr> oldAnnotationExprs, BodyDeclaration bodyDeclaration,
                                            boolean replace) throws InstantiationException, IllegalAccessException {
        if (CollectionUtil.isNotEmpty(oldAnnotationExprs)) {

            //            List<AnnotationExpr> newAnnotationExprs = new LinkedList<AnnotationExpr>(
            //                bodyDeclaration.getAnnotations());
            //            bodyDeclaration.getChildrenNodes().removeAll(newAnnotationExprs);
            //            newAnnotationExprs.addAll(oldAnnotationExprs);
            //            System.out.println(newAnnotationExprs);
            NodeList<AnnotationExpr> annotations = bodyDeclaration.getAnnotations();
            if (annotations == null) {
                bodyDeclaration.setAnnotations(oldAnnotationExprs);
                return true;
            }

            //TODO List怎么处理?
            Map<String, AnnotationExpr> tempMap = CollectionUtil.toMap(annotations, AnnotationExpr::getNameAsString);
            for (AnnotationExpr oldAnnotationExpr : oldAnnotationExprs) {
                String nameAsString = oldAnnotationExpr.getNameAsString();
                if (!tempMap.containsKey(nameAsString)) {
                    annotations.add(oldAnnotationExpr);
                } else {
                    //使用旧的标注
                    if (replace) {
                        
                        if (bodyDeclaration instanceof FieldDeclaration) {
                            FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
                            EnumSet<Modifier> modifiers = fieldDeclaration.getModifiers();
                            //不是自定义查询字段，使用数据库中的设置优先
                            if (!modifiers.contains(Modifier.TRANSIENT)) {
                                continue;
                            }
                            
                            String elementType = fieldDeclaration.getElementType().toString();
                            if (!elementType.equals("String") || !elementType.equals("java.lang.String")){
                                if (nameAsString.equals("Max") || nameAsString.equals("Min")){
                                    continue;
                                }
                            }
                        }

                        AnnotationExpr annotationExpr = tempMap.get(nameAsString);
                        if (annotationExpr != null) {
                            int indexOf = annotations.indexOf(annotationExpr);
                            annotations.remove(indexOf);
                            tempMap.remove(nameAsString);
                            annotations.add(indexOf, oldAnnotationExpr);
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Gets the imports.
     *
     * @param cu the cu
     * @return the imports
     */
    static Set<String> getImports(CompilationUnit cu) {
        Set<String> importSet = null;
        List<ImportDeclaration> imports = cu.getImports();
        if (CollectionUtil.isNotEmpty(imports)) {
            importSet = new HashSet<String>(imports.size());
            for (ImportDeclaration im : imports) {
                String imStr = (im.isStatic() ? "static " : "") + im.getName() + (im.isAsterisk() ? ".*" : "");
                importSet.add(imStr);
            }
        } else {
            importSet = new HashSet<String>(0);
        }
        return importSet;
    }

    /**
     * The Class ImportComparator.
     */
    static class ImportComparator implements java.util.Comparator<ImportDeclaration> {
        static final PrettyPrinterConfiguration prettyPrinterConfiguration;
        static {
            prettyPrinterConfiguration = new PrettyPrinterConfiguration();
            prettyPrinterConfiguration.setPrintComments(false);
        }

        @Override
        public int compare(ImportDeclaration o1, ImportDeclaration o2) {
            String stringWithoutComments1 = o1.getName().toString(prettyPrinterConfiguration);
            String stringWithoutComments2 = o2.getName().toString(prettyPrinterConfiguration);

            if (o1.isStatic() && !o2.isStatic()) {
                return -1;
            } else if (!o1.isStatic() && o2.isStatic()) {
                return 1;
            } else if (o1.isStatic() && o2.isStatic()) {
                return stringWithoutComments1.compareTo(stringWithoutComments2);
            }

            int compareEclipseImport = compareEclipseImport(stringWithoutComments1, stringWithoutComments2, "java.");
            if (compareEclipseImport != -2) {
                return compareEclipseImport;
            }

            compareEclipseImport = compareEclipseImport(stringWithoutComments1, stringWithoutComments2, "javax.");
            if (compareEclipseImport != -2) {
                return compareEclipseImport;
            }

            compareEclipseImport = compareEclipseImport(stringWithoutComments1, stringWithoutComments2, "org.");
            if (compareEclipseImport != -2) {
                return compareEclipseImport;
            }

            compareEclipseImport = compareEclipseImport(stringWithoutComments1, stringWithoutComments2, "cn.");
            if (compareEclipseImport != -2) {
                return compareEclipseImport;
            }

            compareEclipseImport = compareEclipseImport(stringWithoutComments1, stringWithoutComments2, "com.");
            if (compareEclipseImport != -2) {
                return compareEclipseImport;
            }

            return stringWithoutComments1.compareTo(stringWithoutComments2);
        }

        private int compareEclipseImport(String stringWithoutComments1, String stringWithoutComments2, String importStartsWith) {
            if (stringWithoutComments1.startsWith(importStartsWith) && !stringWithoutComments2.startsWith(importStartsWith)) {
                return -1;
            } else if (!stringWithoutComments1.startsWith(importStartsWith) && stringWithoutComments2.startsWith(importStartsWith)) {
                return 1;
            } else if (stringWithoutComments1.startsWith(importStartsWith) && stringWithoutComments2.startsWith(importStartsWith)) {
                return stringWithoutComments1.compareTo(stringWithoutComments2);
            }
            return -2;
        }
    }

    protected static void addBlankImports(List<ImportDeclaration> imports, CompilationUnit nowCompilationUnit) {
        String lastStartWith = null;
        int size = imports.size();
        for (int i = size - 1; i >= 0; i--) {
            ImportDeclaration importDeclaration = imports.get(i);
            String importName = importDeclaration.getName().toString();
            int idx = importName.indexOf('.');
            if (idx > 0) {
                String nowStrartWith = importName.substring(0, idx + 1);
                if (lastStartWith != null && !lastStartWith.equals(nowStrartWith)) {
                    Range range = new Range(Position.pos(0, 0), Position.pos(0, 0));
                    ImportDeclaration emptyDeclaration = new ImportDeclaration(range, new Name(), false, false);
                    imports.add(i + 1, emptyDeclaration);
                    lastStartWith = null;
                } else {
                    lastStartWith = nowStrartWith;
                }
            }
        }
    }

    /**
     * 将旧的import插入到新import中,还有Field上的标注.
     *
     * @param lastCompilationUnit the last compilation unit
     * @param isEntity the is entity
     * @param nowJavaUnitCharArrayWriter the now java unit char array writer
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParseException the parse exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    static void replaceJava(CompilationUnit lastCompilationUnit, JavaType javaType, CharArrayWriter nowJavaUnitCharArrayWriter,
                            String shortFileName) throws IOException, ParseException, InstantiationException, IllegalAccessException {
        boolean replaced = false;
        CompilationUnit nowCompilationUnit = null;
        Reader nowFileReader = new CharArrayReader(nowJavaUnitCharArrayWriter.toCharArray());
        try {
            //下面这种不会产生乱码
            nowCompilationUnit = JavaParser.parse(nowFileReader);
        } catch (Exception e) {
            String erroJavaText = nowJavaUnitCharArrayWriter.toString();
            erroJavaText = IOHelpers.addLineNumber(erroJavaText);
            GLogger.error("错误的java文件:\n" + erroJavaText);
            throw e;
        }

        try {
            switch (javaType) {
                case isControllerBase:
                case isController:
                case isService:
                case isServiceInternal:
                case isServiceImpl: {
                    CompilationUnit temp = lastCompilationUnit;
                    lastCompilationUnit = nowCompilationUnit;
                    nowCompilationUnit = temp;
                    replaced = true;
                    break;
                }
                default:
                    break;
            }

            Set<String> lastImportSet = getImports(lastCompilationUnit);
            Set<String> nowImportSet = getImports(nowCompilationUnit);

            //将之前的java引用加上去
            List<ImportDeclaration> imports = nowCompilationUnit.getImports();
            for (String lastImpt : lastImportSet) {
                if (!nowImportSet.contains(lastImpt)) {
                    lastImpt = lastImpt.trim();
                    ImportDeclaration newImportDeclaration = new ImportDeclaration(new Name(lastImpt), false, false);
                    imports.add(newImportDeclaration);
                    GLogger.info("引用:" + lastImpt + " 被保留或增加");
                    replaced = true;
                }
            }
            Collections.sort(imports, new ImportComparator());
            addBlankImports(imports, nowCompilationUnit);

            if (JavaType.isDao == javaType || JavaType.isDaoImpl == javaType) {
                return;
            }

            ClassOrInterfaceDeclaration oldClassDeclaration = getOneNodesByType(lastCompilationUnit, ClassOrInterfaceDeclaration.class);

            if (null != oldClassDeclaration) {
                ClassOrInterfaceDeclaration nowClassDeclaration = getOneNodesByType(nowCompilationUnit, ClassOrInterfaceDeclaration.class);

                if (null != nowClassDeclaration) {

                    //将之前java类 标注加入 ,不再处理controller ,service,serviceImpl
                    if (JavaType.isEntry == javaType) {
                        if (addAnnotationExprsToBody(oldClassDeclaration.getAnnotations(), nowClassDeclaration, true)) {
                            replaced = true;
                        }
                    }

                    Map<String, FieldDeclaration> oldFieldDeclarationMap = getBodyDeclarationMap(oldClassDeclaration, FieldDeclaration.class);
                    Map<String, FieldDeclaration> nowFieldDeclarationMap = getBodyDeclarationMap(nowClassDeclaration, FieldDeclaration.class);

                    //把之前的Field加进来
                    int fieldIdx = -1;
                    for (Entry<String, FieldDeclaration> oldEntry : oldFieldDeclarationMap.entrySet()) {
                        String fieldName = oldEntry.getKey();
                        if (!nowFieldDeclarationMap.containsKey(fieldName)) {
                            FieldDeclaration oldFieldDeclaration = oldEntry.getValue();
                            //methodIndex = methodIndex - 2;
                            fieldIdx++;
                            GLogger.info(fieldIdx + " oldFieldDeclaration<===========>:" + oldFieldDeclaration);

                            nowClassDeclaration.getMembers().add(fieldIdx, oldFieldDeclaration);
                            replaced = true;
                            GLogger.println("属性(field)被保留或增加:" + fieldName + "  被保留<<<<<<<<<<<<<<<<<<<<<<");
                        }
                    }

                    //把Field上之前的标注注加进来,不再处理controller ,service,serviceImpl
                    if (JavaType.isEntry == javaType) {
                        for (Entry<String, FieldDeclaration> nowEntry : nowFieldDeclarationMap.entrySet()) {
                            FieldDeclaration nowFieldDeclaration = nowEntry.getValue();
                            String fieldName = nowEntry.getKey();

                            FieldDeclaration oldFieldDeclaration = oldFieldDeclarationMap.get(fieldName);

                            if (oldFieldDeclaration == null) {
                                continue;
                            }

                            if (addAnnotationExprsToBody(oldFieldDeclaration.getAnnotations(), nowFieldDeclaration, true)) {
                                replaced = true;
                            }
                            //把Modifile加进来
                            if (oldFieldDeclaration.getModifiers() != null) {
                                nowFieldDeclaration.getModifiers().addAll(oldFieldDeclaration.getModifiers());
                            }

                            //oldFieldDeclarationMap.remove(fieldName);
                        }
                    }

                    //把之前的方法加进来
                    Map<String, MethodDeclaration> oldMethodDeclarationMap = getBodyDeclarationMap(oldClassDeclaration, MethodDeclaration.class);
                    Map<String, MethodDeclaration> nowMethodDeclarationMap = getBodyDeclarationMap(nowClassDeclaration, MethodDeclaration.class);

                    for (Entry<String, MethodDeclaration> oldEntry : oldMethodDeclarationMap.entrySet()) {
                        String methodName = oldEntry.getKey();
                        if (!nowMethodDeclarationMap.containsKey(methodName)) {
                            nowClassDeclaration.getMembers().add(oldEntry.getValue());
                            replaced = true;
                            GLogger.println("方法(Method):" + methodName + "  被保留<<<<<<<<<<<<<<<<<<<<<<");
                        }
                    }

                    //接口，继承加进来
                    copyExtendsAndImpliments(oldClassDeclaration, nowClassDeclaration);
                }
            }
        } finally {
            if (replaced) {
                nowJavaUnitCharArrayWriter.reset();
                nowJavaUnitCharArrayWriter.write(nowCompilationUnit.toString());
            }
        }
    }

    private static void copyExtendsAndImpliments(ClassOrInterfaceDeclaration oldClassDeclaration, ClassOrInterfaceDeclaration nowClassDeclaration) {
        List<ClassOrInterfaceType> oldExtends = oldClassDeclaration.getExtendedTypes();
        List<ClassOrInterfaceType> nowExtends = nowClassDeclaration.getExtendedTypes();

        Set<String> nowExtendNames = classOrInterfaceTypesToNameSet(nowExtends);
        if (CollectionUtil.isNotEmpty(oldExtends)) {
            for (ClassOrInterfaceType classOrInterfaceType : oldExtends) {
                String name = classOrInterfaceType.getNameAsString();
                if (!nowExtendNames.contains(name)) {
                    nowClassDeclaration.addExtendedType(classOrInterfaceType);
                }
            }
        }

        List<ClassOrInterfaceType> oldImplements = oldClassDeclaration.getImplementedTypes();
        List<ClassOrInterfaceType> nowImplements = nowClassDeclaration.getImplementedTypes();

        Set<String> nowImplementNames = classOrInterfaceTypesToNameSet(nowImplements);
        if (CollectionUtil.isNotEmpty(oldImplements)) {
            for (ClassOrInterfaceType classOrInterfaceType : oldImplements) {
                String name = classOrInterfaceType.getNameAsString();
                if (!nowImplementNames.contains(name)) {
                    nowClassDeclaration.addImplementedType(classOrInterfaceType);
                }
            }
        }
    }

    public static Set<String> classOrInterfaceTypesToNameSet(List<ClassOrInterfaceType> types) {
        Set<String> nameSet = new HashSet<String>();
        if (CollectionUtil.isNotEmpty(types)) {
            for (ClassOrInterfaceType classOrInterfaceType : types) {
                nameSet.add(classOrInterfaceType.getNameAsString());
            }
        }
        return nameSet;
    }
}
