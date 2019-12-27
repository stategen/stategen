/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package ch.qos.logback.classic.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.stategen.framework.util.OSUtil;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import lombok.Cleanup;

// contributors
// Ted Graham, Matt Fowles, see also http://jira.qos.ch/browse/LBCORE-32

/**
 * This class contains logback's logic for automatic configuration
 * 该类用来覆盖logback中的ContextInitializer,用于logback读取/opt/config/stategen/下相应的log配置文件
 * @author Ceki Gulcu
 */
public class ContextInitializer {
    final public static String GROOVY_AUTOCONFIG_FILE    = "logback.groovy";
    final public static String AUTOCONFIG_FILE           = "logback.xml";
    final public static String TEST_AUTOCONFIG_FILE      = "logback-test.xml";
    final public static String CONFIG_FILE_PROPERTY      = "logback.configurationFile";

    final public static String application_properties = "application.properties";
    final public static String logback_config_xml_key    = "logback.configfile.xml";

    final LoggerContext loggerContext;

    public ContextInitializer(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    public void configureByResource(URL url) throws JoranException {
        if (url == null) {
            throw new IllegalArgumentException("URL argument cannot be null");
        }
        final String urlString = url.toString();
        if (urlString.endsWith("groovy")) {
            if (EnvUtil.isGroovyAvailable()) {
                // avoid directly referring to GafferConfigurator so as to avoid
                // loading groovy.lang.GroovyObject . See also http://jira.qos.ch/browse/LBCLASSIC-214
                GafferUtil.runGafferConfiguratorOn(loggerContext, this, url);
            } else {
                StatusManager sm = loggerContext.getStatusManager();
                sm.add(new ErrorStatus("Groovy classes are not available on the class path. ABORTING INITIALIZATION.", loggerContext));
            }
        } else if (urlString.endsWith("xml")) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(url);
        } else {
            throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be either .groovy or .xml");
        }
    }

    void joranConfigureByResource(URL url) throws JoranException {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(url);
    }

    private URL findConfigFileURLFromSystemProperties(ClassLoader classLoader, boolean updateStatus) {
        String logbackConfigFile = OptionHelper.getSystemProperty(CONFIG_FILE_PROPERTY);
        if (logbackConfigFile != null) {
            URL result = null;
            try {
                result = new URL(logbackConfigFile);
                return result;
            } catch (MalformedURLException e) {
                // so, resource is not a URL:
                // attempt to get the resource from the class path
                result = Loader.getResource(logbackConfigFile, classLoader);
                if (result != null) {
                    return result;
                }
                File f = new File(logbackConfigFile);
                if (f.exists() && f.isFile()) {
                    try {
                        result = f.toURI().toURL();
                        return result;
                    } catch (MalformedURLException e1) {
                    }
                }
            } finally {
                if (updateStatus) {
                    statusOnResourceSearch(logbackConfigFile, classLoader, result);
                }
            }
        }
        return null;
    }

    private URL loadURLFormPropertiesFile(ClassLoader myClassLoader, boolean updateStatus) throws IOException {
        StatusManager sm = loggerContext.getStatusManager();
        sm.add(new InfoStatus("正在读取：" + application_properties, loggerContext));
        URL application_url=getResource(application_properties, myClassLoader, updateStatus);
        if (application_url==null) {
            return null;
        }
        
        String applicationPropertiesPath = application_url.getPath();

        Properties prop = new Properties();
        @Cleanup
        InputStream in = null;
        try {
            sm.add(new InfoStatus(applicationPropertiesPath, loggerContext) );
            in=myClassLoader.getResourceAsStream(application_properties);
            prop.load(in);
            if (!prop.containsKey(logback_config_xml_key)) {
                sm.add(new ErrorStatus(applicationPropertiesPath+" 文件中没有键："+logback_config_xml_key, loggerContext) );
                return null;
            }
            sm.add(new InfoStatus(applicationPropertiesPath+" 文件中有键："+logback_config_xml_key, loggerContext) );

            String logbackFile = prop.getProperty(logback_config_xml_key);
            logbackFile = logbackFile.trim();
            
            if (logbackFile==null || logbackFile.isEmpty()) {
                sm.add(new ErrorStatus(applicationPropertiesPath+" 文件中键："+logback_config_xml_key+" 为空!", loggerContext) );
                return null;
            }
            sm.add(new InfoStatus(applicationPropertiesPath+" 文件中键："+logback_config_xml_key+" 值为:"+logbackFile, loggerContext) );

            logbackFile = OSUtil.getRealUriPathByOs(logbackFile);
            sm.add(new InfoStatus("Found logback configration :"+logbackFile, loggerContext) );
            sm.add(new InfoStatus("加载真正的Logback配置文件:"+logbackFile, loggerContext));
            URL url = new URL(logbackFile);
            return url;
        } catch (IOException e) {
            sm.add(new ErrorStatus(
                "Failed to get url list for resource [" + applicationPropertiesPath + "]", loggerContext, e));
        } 
        return null;
    }

    public URL findURLOfDefaultConfigurationFile(boolean updateStatus) throws IOException {
        StatusManager sm = loggerContext.getStatusManager();
        ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
        URL url = null;
        url = loadURLFormPropertiesFile(myClassLoader, updateStatus);
        if (url != null) {
            sm.add(new InfoStatus("装载文件:"+url, loggerContext) );
            return url;
        }
        sm.add(new ErrorStatus("从"+application_properties+" 装载文件不成功，不存在!", loggerContext) );

        url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
        if (url != null) {
            return url;
        }

        url = getResource(GROOVY_AUTOCONFIG_FILE, myClassLoader, updateStatus);
        if (url != null) {
            return url;
        }

        url = getResource(TEST_AUTOCONFIG_FILE, myClassLoader, updateStatus);
        if (url != null) {
            return url;
        }

        url = getResource(AUTOCONFIG_FILE, myClassLoader, updateStatus);
        sm.add(new InfoStatus(""+url, loggerContext) );
        return url;
    }

    private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
        URL url = Loader.getResource(filename, myClassLoader);
        if (updateStatus) {
            statusOnResourceSearch(filename, myClassLoader, url);
        }
        return url;
    }

    public void autoConfig() throws JoranException ,IOException{
        StatusListenerConfigHelper.installIfAsked(loggerContext);
        URL url = findURLOfDefaultConfigurationFile(true);
        if (url != null) {
            configureByResource(url);
        } else {
            Configurator c = EnvUtil.loadFromServiceLoader(Configurator.class);
            if (c != null) {
                try {
                    c.setContext(loggerContext);
                    c.configure(loggerContext);
                } catch (Exception e) {
                    throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", c != null ? c.getClass()
                                    .getCanonicalName() : "null"), e);
                }
            } else {
                BasicConfigurator basicConfigurator = new BasicConfigurator();
                basicConfigurator.setContext(loggerContext);
                basicConfigurator.configure(loggerContext);
            }
        }
    }

    private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url) {
        StatusManager sm = loggerContext.getStatusManager();
        if (url == null) {
            sm.add(new InfoStatus("Could NOT find resource [" + resourceName + "]", loggerContext));
        } else {
            sm.add(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", loggerContext));
            multiplicityWarning(resourceName, classLoader);
        }
    }

    private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
        Set<URL> urlSet = null;
        StatusManager sm = loggerContext.getStatusManager();
        try {
            urlSet = Loader.getResources(resourceName, classLoader);
        } catch (IOException e) {
            sm.add(new ErrorStatus("Failed to get url list for resource [" + resourceName + "]", loggerContext, e));
        }
        if (urlSet != null && urlSet.size() > 1) {
            sm.add(new WarnStatus("Resource [" + resourceName + "] occurs multiple times on the classpath.", loggerContext));
            for (URL url : urlSet) {
                sm.add(new WarnStatus("Resource [" + resourceName + "] occurs at [" + url.toString() + "]", loggerContext));
            }
        }
    }
}
