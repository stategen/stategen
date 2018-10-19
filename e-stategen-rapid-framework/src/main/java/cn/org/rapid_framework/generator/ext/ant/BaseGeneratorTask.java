package cn.org.rapid_framework.generator.ext.ant;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import cn.org.rapid_framework.generator.GeneratorFacade;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.SystemHelper;

public abstract class BaseGeneratorTask extends Task{
    protected Path classpath;
    
    protected String shareInput; //共享模板目录,可以使用classpath:前缀
    protected String input; //模板输入目录,可以使用classpath:前缀
    protected String output; //模板输出目录,可以使用classpath:前缀
    
    private boolean openOutputDir = false;
    private String _package;
    
    public static Properties toProperties(Hashtable properties) {
        Properties props = new Properties();
        props.putAll(properties);
        return props;
    }
    
    private void error(Exception e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        log(out.toString(),Project.MSG_ERR);
    }
    
    protected GeneratorFacade createGeneratorFacade(String input,String output) {
        if(input == null) throw new IllegalArgumentException("input must be not null");
        if(output == null) throw new IllegalArgumentException("output must be not null");
        
        GeneratorProperties.setProperties(new Properties());
        Properties properties = toProperties(getProject().getProperties());
        properties.setProperty("basedir", getProject().getBaseDir().getAbsolutePath());
        GeneratorProperties.setProperties(properties);
        
        GeneratorFacade gf = new GeneratorFacade();
        gf.getGenerator().addTemplateRootDir(input);
        if(shareInput != null) {
            gf.getGenerator().addTemplateRootDir(shareInput);
        }
        gf.getGenerator().setOutRootDir(output);
        return gf;
    }
    
    public String getShareInput() {
        return shareInput;
    }

    public void setShareInput(String shareInput) {
        this.shareInput = shareInput;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }
    
    public void setOpenOutputDir(boolean openOutputDir) {
        this.openOutputDir = openOutputDir;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }
    
    public void setClasspathRef(Reference r){
        this.classpath = new Path(getProject());
        this.classpath.setRefid(r);
    }

    @Override
    final public void execute() throws BuildException {
        super.execute();
        setContextClassLoader();
        try {
        	executeInternal();
        }catch(Exception e) {
            error(e);
            throw new BuildException(e);
        }
    }

    protected void executeInternal() throws Exception {
        freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
        
        executeBefore();
        
        GeneratorFacade facade = createGeneratorFacade(input,output);
        
        List<Map> maps = getGeneratorContexts();
        if(maps == null) return;
        for(Map map : maps) {
            facade.generateByMap(map);
        }
        
        if(openOutputDir && SystemHelper.isWindowsOS) {
            Runtime.getRuntime().exec("cmd.exe /c start "+output);
        }
    }

	private void setContextClassLoader() {
        if(classpath == null) {
            String cp = ((AntClassLoader) getClass().getClassLoader()).getClasspath();
            classpath = new Path(getProject(),cp);
        }
        AntClassLoader classloader = new AntClassLoader(getProject(),classpath,true);
        Thread.currentThread().setContextClassLoader(classloader);
    }

    protected void executeBefore() throws Exception {
	}
    
    protected abstract List<Map> getGeneratorContexts() throws Exception;
    
}
