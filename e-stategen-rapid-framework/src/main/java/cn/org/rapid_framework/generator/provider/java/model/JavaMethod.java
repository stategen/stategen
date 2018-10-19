/**
 * project:pomer
 * 
 * Copyright 2008 [pomer], Inc. All rights reserved.
 * Website: http://www.pomer.org.cn/
 * 
 */
package cn.org.rapid_framework.generator.provider.java.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.rapid_framework.generator.provider.java.model.MethodParameter.JavaSourceFileMethodParametersParser;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.typemapping.JavaImport;

/**
 * 
 * @author badqiu,Linlin Yu
 */
public class JavaMethod {
	Method method;
	private JavaClass clazz; //与method相关联的class
	
	
	public JavaMethod(Method method, JavaClass clazz) {
		super();
		if(method == null) throw new IllegalArgumentException("method must be not null");
		if(clazz == null) throw new IllegalArgumentException("clazz must be not null");
		this.method = method;
		this.clazz = clazz;
	}

	public JavaClass getClazz() {
		return clazz;
	}

	public String getMethodName() {
		return method.getName();
	}

	public JavaClass getReturnType() {
		return new JavaClass(method.getReturnType());
	}
	
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}

	public boolean isBridge() {
		return method.isBridge();
	}

    public List<JavaClass> getExceptionTypes() {
        List<JavaClass> result = new ArrayList();
        for(Class c : method.getExceptionTypes()) {
            result.add(new JavaClass(c));
        }
        return result;
    }

    public boolean isSynthetic() {
		return method.isSynthetic();
	}

	public boolean isVarArgs() {
		return method.isVarArgs();
	}

	public Set<JavaClass> getImportClasses() {
		Set<JavaClass> set = new LinkedHashSet<JavaClass>();
        JavaImport.addImportClass(set,method.getParameterTypes());
        JavaImport.addImportClass(set,method.getExceptionTypes());
        JavaImport.addImportClass(set, method.getReturnType());
        return set;
	}
	
	public List<MethodParameter> getParameters() {
		Class[] parameters  = method.getParameterTypes();
		List<MethodParameter> results = new ArrayList<MethodParameter>();
		for(int i = 0; i < parameters.length; i++) {
			results.add(new MethodParameter(i+1,this,new JavaClass(parameters[i])));
		}
		return results;
	}
	
	public String getMethodNameUpper() {
		return StringHelper.capitalize(getMethodName());
	}
	
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JavaMethod other = (JavaMethod) obj;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        return true;
    }
    
    public boolean isPropertyMethod() {
    	if(getMethodName().startsWith("set") || getMethodName().startsWith("get") || (getMethodName().startsWith("is") && getReturnType().isBooleanType())) {
    		return true;
    	}
    	return false;
    }

    /**
     * 解析java源代码的方法,将一个方法中调用field的所有方法全部提取出来.
     * @return
     */
    public List<FieldMethodInvocation> getFieldMethodInvocationSequences() {
    	if(StringHelper.isNotBlank(clazz.getMavenJavaSourceFileContent())) {
    	    try {
        		JavaMethodInvokeSequencesParser cmd = new JavaMethodInvokeSequencesParser(this,clazz.getMavenJavaSourceFileContent());
    	    	cmd.execute();
    	    	return cmd.getMethodInvokeSequences();
    	    }catch(Exception e) {
    	        GLogger.warn("getFieldMethodInvocationSequences() occer error on method:"+method.toString(),e);
    	        return new ArrayList<FieldMethodInvocation>(0);
    	    }
    	}else {
    		return new ArrayList<FieldMethodInvocation>(0);
    	}
    }
    
    public String toString() {
		return clazz.getJavaType()+"."+getMethodName()+"()";
	}
    
    public static class FieldMethodInvocation {
    	JavaField field;
    	JavaMethod method;
		public FieldMethodInvocation(JavaField field, JavaMethod method) {
			super();
			this.field = field;
			this.method = method;
		}
		public JavaField getField() {
			return field;
		}
		public void setField(JavaField field) {
			this.field = field;
		}
		public JavaMethod getMethod() {
			return method;
		}
		public void setMethod(JavaMethod method) {
			this.method = method;
		}
		public boolean equals(Object obj) {
		    if(obj == null) return false;
		    if(! (obj instanceof FieldMethodInvocation)) return false;
		    FieldMethodInvocation other = (FieldMethodInvocation)obj;
		    return field.equals(other.field) && method.equals(other.method);
		}
		public int hashCode() {
		    return field.hashCode() + method.hashCode();
		}
		public String toString() {
		    return field.getFieldName()+"."+method.getMethodName()+"()";
		}
    }
    
    /**
     * 解析java源代码的方法,将一个方法中调用field的所有方法全部提取出来.
     **/
    public static class JavaMethodInvokeSequencesParser {
    	//匹配一个field的方法调用,如  generator.deleteBy() method.getClass()
    	public static String fieldMethodInvokeRegex = "(\\w+)\\.(\\w+)\\(";
    	
    	private JavaMethod method;
    	private String javaSourceContent;
    	private JavaClass clazz;
    	
    	boolean executed = false;
    	public JavaMethodInvokeSequencesParser(JavaMethod method, String javaSourceContent) {
			super();
    		if(StringHelper.isBlank(javaSourceContent)) {
    			throw new IllegalArgumentException("'javaSourceContent' must be not blank");
    		}
    		
			this.method = method;
			this.javaSourceContent = javaSourceContent;
			this.clazz = method.getClazz();
		}

		private List<FieldMethodInvocation> methodInvokeFlows = new ArrayList<FieldMethodInvocation>();
		
    	public List<FieldMethodInvocation> getMethodInvokeSequences() {
    		if(executed) {
    			return methodInvokeFlows;
    		}else {
    			throw new IllegalStateException("please invoke execute() method before getMethodInvokeFlows()");
    		}
    	}
    	
    	public void execute() {
    		executed = true;
    		//本类是否有声明
    		if(!declaredMethodsContains()) {
    		    return;
    		}
    		//是否是匿名方法
    		if(method.getMethodName().indexOf("$") >= 0) {
    		    return;
    		}
    		
    		String javaSourceContent = removeSomeThings();
    		String methodBody = getMethodBody(javaSourceContent);
    		
    		Pattern p = Pattern.compile(fieldMethodInvokeRegex);
    		Matcher m = p.matcher(methodBody);
    		while(m.find()) {
    			String field = m.group(1);
    			String methodName= m.group(2);
    			addFieldMethodInvocation(field, methodName);
    		}
    	}

        private boolean declaredMethodsContains() {
            for(Method m : clazz.getClazz().getDeclaredMethods()) {
    		    if(m.equals(method.method)) {
    		        return true;
    		    }
    		}
            return false;
        }

		private void addFieldMethodInvocation(String field, String methodName) {
			try {
				JavaField javaField = clazz.getField(field);
				JavaClass fieldType = javaField.getType();
				JavaMethod method = fieldType.getMethod(methodName);
				if(method != null) {
					methodInvokeFlows.add(new FieldMethodInvocation(javaField,method));
				}
			} catch (NoSuchFieldException e) {
				//ignore
			}
		}
		
		public static String modifierToString(int mod) {
		    if ((mod & Modifier.PUBLIC) != 0)    return "public";
		    if ((mod & Modifier.PROTECTED) != 0) return "protected";
		    if ((mod & Modifier.PRIVATE) != 0)   return "private";
		    return "";
		}
		
		private String getMethodBody(String javaSourceContent) {
		    
		    String methodStartPattern = "(?s)\\s+"+method.getMethodName()+"\\s*\\("+JavaSourceFileMethodParametersParser.getSimpleParamsPattern(method.method)+"\\)\\s*";
            
        	int methodStart = StringHelper.indexOfByRegex(javaSourceContent,methodStartPattern);
        	if(methodStart == -1) throw new IllegalArgumentException("cannot get method body by pattern:"+methodStartPattern+" methodName:"+method.getMethodName() +"\n javaSource:"+javaSourceContent);
        	
        	try {
        		String methodEnd = javaSourceContent.substring(methodStart);
        		int[] beginAndEnd = findWrapCharEndLocation(methodEnd,'{','}');
        		if(beginAndEnd == null) return "";
        		
        		String methodBody = methodEnd.substring(beginAndEnd[0], beginAndEnd[1]);
    			return methodBody;
		    }catch(RuntimeException e) {
		        throw new IllegalArgumentException("cannot get method body by pattern:"+methodStartPattern+"\n javaSource:"+javaSourceContent,e);
		    }
		}

		private String removeSomeThings() {
			String javaSourceContent = removeJavaComments(this.javaSourceContent);
//    		javaSourceContent = removeJavaImports(javaSourceContent);
//    		javaSourceContent = removeJavaPackage(javaSourceContent);
    		javaSourceContent = replaceString2EmptyString(javaSourceContent);
			return javaSourceContent;
		}
    	
    	public static  String replaceString2EmptyString(String str) {
    		if(str == null) return null;
    		str = str.replaceAll("\".*?\"", ""); // replace string from "234 " => ""
			return str;
		}
		// getName\s*\(.*?\)\s*\{.*?\;\s*}
		public static String removeJavaComments(String str) {
			if(str == null) return null;
			str = str.replaceAll("//.*", ""); // remove line comment: //
			str = str.replaceAll("(?s)/\\*.*?\\*/", ""); // remove block comment: /* */
			return str;
		}
//		public static String removeJavaImports(String str) {
//    		if(str == null) return null;
//    		str = str.replaceAll("\\s*import\\s.*", ""); // remove java import
//			return str;
//		}
//		public static String removeJavaPackage(String str) {
//    		if(str == null) return null;
//    		str = str.replaceAll("\\s*package\\s.*", ""); // remove java package
//			return str;
//		}
		
		/** 
		 * 找到对称的一条括号所处的位置,
		 * 如 findWrapCharEndLocation("0123{{67}}}",'{','}'), 将返回 [4,9]
		 * 如果没有将到,将返回null 
		 **/
		public static int[] findWrapCharEndLocation(String str,char begin,char end) {
			int count = 0;
			boolean foundEnd = false;
			boolean foundBegin = false;
			int[] beginAndEnd = new int[2];
			for(int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if(c == begin) {
					if(!foundBegin) {
						beginAndEnd[0] = i;
					}
					foundBegin = true;
					count ++;
				}
				if(c == end) {
					foundEnd = true;
					count--;
				}
				if(count == 0 && foundBegin && foundEnd) {
					beginAndEnd[1] = i;
					return beginAndEnd;
				}
			}
			return null;
		}
    }
    
}
