package cn.org.rapid_framework.util;


public class ProjectTesterGenerator4Sofa {
//    
//    //扫描包名
//    private final static String scanClassPackage;
//    //项目扫描主路径
//    private final static String projectMainPath;
//    //maven仓库路径
//    private final static String mavenRepository;
//    
//    static {
//        scanClassPackage = "com.alipay.mcenter.**";
//        projectMainPath = "D:/dev/clearcase/zhongxuan_mquery_jycf4_upgrade/vobs/mcenter/mcenter";
//        mavenRepository = System.getProperty("user.home")+"/.m2/repository";
//    }
//    
//    
//    public static void main(String args []) throws Exception {
//        for(Object key : System.getProperties().keySet()) {
//            System.out.println(key+"="+System.getProperty(key.toString()));
//        }
//        System.setProperty("gg.isOverride", "true");
//        
//        List<String> classPathFiles = scanClassPathFiles();
//        
//        //分析目标项目classpath文件，获得class编译路径及项目依赖
//        List<File> projectClassesOutputPaths = scanProjectsOutputPaths(classPathFiles);
//        ClassLoader classloader = genDependenciesUrlsClassLoader(classPathFiles);
//        Thread.currentThread().setContextClassLoader(classloader);   
//        
//        //查询出所有spring配置文件
//        List<String> xmlFiles = new ArrayList<String>();
//        scanProjectsWithFilePrefixName(projectClassesOutputPaths,xmlFiles,".xml");
//        
//        //根据xml文件查询出spring bean定义
////      Map beanDefinations = analysisSpringBeanConfig(xmlFiles);
//        
//        //根据spring xml配置文件生成BaseTestCase
//        SpringXmls springXmls = new SofaConfigsProcessor().generateBaseTestCaseBySpringConfigs(projectClassesOutputPaths,xmlFiles);
//        //根据通配符扫描需要生成TestCase的目标测试类，并生成TestCase
//        generateTestCaseByPackage(scanClassPackage,springXmls);
//    }
//
//    private static ClassLoader genDependenciesUrlsClassLoader(
//                                                              List<String> classPathFiles) {
//        Set<URL> dependenciesUrls = scanDependenciesUrls(classPathFiles);        
//        //把类编译路径添加到classLoader中
//        URL [] urlArray = new URL[dependenciesUrls.size()];
//        ClassLoader classloader = new URLClassLoader(dependenciesUrls.toArray(urlArray));
//        return classloader;
//    }
//
//    private static Set<URL> scanDependenciesUrls(List<String> classPathFiles) {
//        Set<URL> dependenciesUrls = new LinkedHashSet<URL>();
//        for(String classPathFile:classPathFiles){
//            System.out.println("===========================================================");
//            System.out.println("analysis project class path file:"+classPathFile);
//            
//            URL clazzTargetPath = getEclipseClasspathOutputPath(classPathFile);
//            dependenciesUrls.add(clazzTargetPath);
////            System.out.println("project class target path:"+clazzTargetPath);
//            List<URL> dependencyFiles = analysisProjectDependencyJar(classPathFile);
//            for(URL dependencyFile:dependencyFiles){
////                System.out.println("project dependency file:"+dependencyFile);
//                dependenciesUrls.add(dependencyFile);
//            }
//            System.out.println("===========================================================");
//        }
//        return dependenciesUrls;
//    }
//
//    private static List<File> scanProjectsOutputPaths(
//                                                      List<String> classPathFiles) {
//        List<File> projectClassOutputPaths = new ArrayList<File>();
//        for(String classPathFile:classPathFiles){
//            URL clazzTargetPath = getEclipseClasspathOutputPath(classPathFile);
//            projectClassOutputPaths.add(new File(clazzTargetPath.getFile()));
//        }
//        return projectClassOutputPaths;
//    }
//
//    private static List<String> scanClassPathFiles() {
//        List<String> classPathFiles = new ArrayList<String>();
//        //查找目标项目
//        File project = new File(projectMainPath);
//        scanProjectWithFilePrefixName(project,classPathFiles,".classpath");
//        return classPathFiles;
//    }
//
//    public static void scanProjectWithFilePrefixName(File project,List<String> classPathFiles,String prefixName){
//        if(project.isFile()){
//            if(project.getName().toLowerCase().endsWith(prefixName.toLowerCase())){
//                classPathFiles.add(project.getAbsolutePath());
//            }
//        } else{
//            File [] childFiles = project.listFiles();
//            for(File childFile:childFiles){
//                scanProjectWithFilePrefixName(childFile,classPathFiles,prefixName);
//            }
//        }
//    }
//    
//    public static void scanProjectsWithFilePrefixName(List<File> projects,List<String> classPathFiles,String prefixName){
//        for(File project:projects){
//            scanProjectWithFilePrefixName(new File(project.getAbsolutePath()+"\\META-INF\\spring\\"),classPathFiles,prefixName);
//        }
//    }
//    
//    public static URL getEclipseClasspathOutputPath(String classPathFile){
//        String projectPath = classPathFile.substring(0, classPathFile.indexOf(".classpath"));
//        SAXBuilder builder = new SAXBuilder();
//        try {
//            Document doc = builder.build(new File(classPathFile));
//            XPath classpathentryPath = XPath.newInstance("//classpath/classpathentry");
//            List classpathentrys = classpathentryPath.selectNodes(doc);
//            Iterator classpathentryIterator = classpathentrys.iterator();
//            while(classpathentryIterator.hasNext()){
//                Element classpathentry = (Element) classpathentryIterator.next();
//                Attribute kind = classpathentry.getAttribute("kind");
//                if("output".equalsIgnoreCase(kind.getValue())){
//                    Attribute path = classpathentry.getAttribute("path");
//                    return new File(projectPath+path.getValue().replace("/", "\\")).toURL();
//                }
//            }
//        } catch (JDOMException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    
//    public static List<URL> analysisProjectDependencyJar(String classPathFile){
//        List<URL> dependencyPaths = new ArrayList<URL>();
//        SAXBuilder builder = new SAXBuilder();
//        try {
//            Document doc = builder.build(new File(classPathFile));
//            XPath classpathentryPath = XPath.newInstance("//classpath/classpathentry");
//            List classpathentrys = classpathentryPath.selectNodes(doc);
//            Iterator classpathentryIterator = classpathentrys.iterator();
//            while(classpathentryIterator.hasNext()){
//                Element classpathentry = (Element) classpathentryIterator.next();
//                Attribute kind = classpathentry.getAttribute("kind");
//                if("var".equalsIgnoreCase(kind.getValue())){
//                    Attribute path = classpathentry.getAttribute("path");
//                    dependencyPaths.add(new File(path.getValue().replace("M2_REPO", mavenRepository)).toURL());
//                    Attribute sourcepath = classpathentry.getAttribute("sourcepath");
//                    if(sourcepath != null) {
//                        dependencyPaths.add(new File(sourcepath.getValue().replace("M2_REPO", mavenRepository)).toURL());
//                    }
//                }
//            }
//        } catch (JDOMException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return dependencyPaths;
//    }
//    
//    private static void generateTestCaseByPackage(String packageName,SpringXmls springXmls) throws ClassNotFoundException,Exception {
//        List<String> classes = ScanClassUtils.scanPackages(packageName);
//        for(String className:classes){
//            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
//            //如果该类不属于一般服务类，则不生成TestCase
//            if(clazz.isAnnotation()||clazz.isAnonymousClass()||clazz.isArray()||clazz.isEnum()||clazz.isLocalClass()||clazz.isPrimitive()||clazz.isInterface()){
//                continue;
//            }
//            //如果该类属于内部类，则不生成TestCase
//            if(clazz.getSimpleName().contains("$")){
//                continue;
//            }
//            //如果该类不含有任何public方法，则不生成TestCase
//            if(!hasPublicMethod(clazz)){
//                continue;
//            }
//            
//            String springBeanId = getSpringBeanIdByClass(springXmls, clazz);
//            System.out.println("generate TestCase by class:"+className+" springBeanId:"+springBeanId);
//            GeneratorContext.put("springBeanId",springBeanId);
//            try {
//                new GeneratorFacade().generateByClass(clazz, "test_template\\test_case");
//            }finally {
//                GeneratorContext.clear();
//            }
//        }
//    }
//    
//    /** 根据spring.xml中的bean定义得到spring bean id */
//    private static String getSpringBeanIdByClass(SpringXmls springXmls,Class<?> clazz) {
//        for(Map bean : springXmls.springBeans) {
//            String beanClass = (String)bean.get("class");
//            if(clazz.getName().equals(beanClass)) {
//                String id = (String)bean.get("id");
//                if(StringHelper.isNotBlank(id)) {
//                    return id;
//                }
//            }
//        }
//        return StringHelper.uncapitalize(clazz.getSimpleName());
//    }
//    
//    public static class SofaConfigsProcessor {
//        public static class SpringXmls {
//            List<Map> springBeans;
//            List<Map> sofaReferences;
//            List<Map> sofaServiceses;
//            List<String> springXmls;
//            public SpringXmls(List<Map> springBeans, List<Map> sofaReferences,
//                              List<Map> sofaServiceses,
//                              List<String> springXmls) {
//                this.springBeans = springBeans;
//                this.sofaReferences = sofaReferences;
//                this.sofaServiceses = sofaServiceses;
//                this.springXmls = springXmls;
//            }
//        }
//        private SpringXmls generateBaseTestCaseBySpringConfigs(List<File> projectClassesOutputPaths,List<String> xmlConfigs) throws Exception{
//    
//            List<String> springConfigs = new ArrayList<String>();
//            List<String> resourceFilters = getSofaResourceFilters(projectClassesOutputPaths);
//            List<String> springReplaceConfigs = new ArrayList<String>();
//            List<String> hasSofaReferenceConfigs = new ArrayList<String>();
//            //过滤xml文件，选出spring配置文件及需要替换properties配置的spring配置文件
//            SAXBuilder builder = new SAXBuilder();
//            List allBeans = new ArrayList();
//            List allSofaReferences = new ArrayList();
//            List allSofaServiceses = new ArrayList();
//            for(String xmlConfig:xmlConfigs){
//                String springPath = xmlConfig.substring(xmlConfig.indexOf("META-INF\\spring\\"));
//                springConfigs.add(springPath);
//                for(String resourceFilter : resourceFilters){
//                    if(springPath.contains(resourceFilter)){
//                        springReplaceConfigs.add(resourceFilter);
//                    }
//                }
//                
//                Document doc = builder.build(new FileInputStream(xmlConfig));
//                List<Map> beans = SpringXmlConfigUtils.selectAttributesByXpath(doc, "//beans:beans/beans:bean");
//                List<Map> sofaReferences = SpringXmlConfigUtils.selectAttributesByXpath(doc, "//beans:beans/sofa:reference");
//                List<Map> sofaServiceses = SpringXmlConfigUtils.selectAttributesByXpath(doc, "//beans:beans/sofa:service");
//                
//                if(sofaReferences.size() > 0) {
//                    hasSofaReferenceConfigs.add(springPath);
//                }
//                allBeans.addAll(beans);
//                allSofaReferences.addAll(allSofaReferences);
//                allSofaServiceses.addAll(allSofaServiceses);
//            }
//            
//            Map<String, List<String>> params = new HashMap<String, List<String>>();
//            params.put("springConfigs", springConfigs);
//            params.put("springReplaceConfigs", springReplaceConfigs);
//            params.put("hasSofaReferenceConfigs", hasSofaReferenceConfigs);
//            new GeneratorFacade().generateByMap(params, "test_template\\base_test_case");
//            
//            return new SpringXmls(allBeans,allSofaReferences,allSofaServiceses,springConfigs);
//        }
//
//        private List<String> getSofaResourceFilters(List<File> projectClassesOutputPaths)
//                                                                                         throws IOException,
//                                                                                         FileNotFoundException {
//            List<String> resourceFilters = new ArrayList<String>();
//            
//            //查询项目中的MF文件,解析manifest中的Resource-Filter属性
//            for(File projectTargetPath : projectClassesOutputPaths){
//                Manifest manifest = new Manifest(new FileInputStream(projectTargetPath.getAbsolutePath()+"\\META-INF\\MANIFEST.MF"));
//                Attributes attrs =  manifest.getMainAttributes();
//                String resourceFilterString = attrs.getValue("Resource-Filter");
//                if(resourceFilterString != null && resourceFilterString.trim().length()>0){
//                    for(String filter : StringHelper.tokenizeToStringArray(resourceFilterString,", \t\n\r\f")){
//                        resourceFilters.add(filter.trim());
//                    }
//                }
//            }
//            return resourceFilters;
//        }
//    }
//    
//    public static class SpringXmlConfigUtils {
//        private static List<Map> selectAttributesByXpath(Document doc, String xpath) throws JDOMException {
//            XPath sofaRef = XPath.newInstance(xpath);
//            sofaRef.addNamespace("beans", "http://www.springframework.org/schema/beans");
//            sofaRef.addNamespace("sofa", "http://www.alipay.com/schema/service");
//            sofaRef.addNamespace("p", "http://www.springframework.org/schema/p");
//            sofaRef.addNamespace("context", "http://www.springframework.org/schema/context");
//            sofaRef.addNamespace("webflow", "http://www.springframework.org/schema/webflow-config");
//            Iterator<Element> it2 = sofaRef.selectNodes(doc).iterator();
//            List<Map> elements = new ArrayList();
//            while(it2.hasNext()){
//                Element elm = it2.next();
//                Map attributes = attributes2Map(elm.getAttributes());
//                elements.add(attributes);
//            }
//            return elements;
//        }
//        
//        public static Map toMap(List<Map> list,String... keyProperties) {
//            Map result = new LinkedHashMap();
//            for(Map m : list) {
//                for(String key : keyProperties) {
//                    Object  value = m.get(key);
//                    if(value != null) {
//                        result.put(value, m);
//                        break;
//                    }
//                }
//            }
//            return result;
//        }
//    }
//    
////    private static Map analysisSpringBeanConfig(List<String> springConfigs){
////      Map beandefinations = new HashMap();
////      String [] springXmlConfigs = new String[springConfigs.size()];
////      ApplicationContext context = new FileSystemXmlApplicationContext(springConfigs.toArray(springXmlConfigs));
////      String[] beanNames = context.getBeanDefinitionNames();
////      for(String beanName:beanNames){
////          Object bean = context.getBean(beanName);
////          beandefinations.put(bean.getClass(), beanName);
////      }
////      return beandefinations;
////    }
//    
//    private static Map attributes2Map(List<Attribute> attributes) {
//        Map map = new LinkedHashMap();
//        for(Attribute attr : attributes) {
//            map.put(attr.getName(),attr.getValue());
//        }
//        return map;
//    }
//
//    private static boolean hasPublicMethod(Class<?> clazz){
//        Method[] methods = clazz.getDeclaredMethods();
//        for(Method method:methods){
//            if(Modifier.isPublic(method.getModifiers())){
//                return true;
//            }
//        }
//        return false;
//    }
}
