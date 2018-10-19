package cn.org.rapid_framework.generator.util.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.org.rapid_framework.generator.Generator;
import cn.org.rapid_framework.generator.Generator.GeneratorModel;
import cn.org.rapid_framework.generator.GeneratorFacade;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.util.FileHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

/**
 * 生成器的测试工具类,用于将模板内容生成在某个临时文件夹,
 * 然后读取临时文件夹的内容返回 返回之前会删除临时文件夹
 * 
 * @author badqiu
 * 
 */
public class GeneratorTestHelper {
	private static AtomicLong count = new AtomicLong(System.currentTimeMillis());

	public static String generateBy(GeneratorFacade gf,
			GeneratorModel... models) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateBy(models);
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateByAllTable(GeneratorFacade gf)
			throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByAllTable();
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateByClass(GeneratorFacade gf, Class... clazzes)
			throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByClass(clazzes);
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateByMap(GeneratorFacade gf, Map... maps)
			throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByMap(maps);
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateBySql(GeneratorFacade gf, Sql... sqls)
			throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateBySql(sqls);
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateByTable(GeneratorFacade gf,
			String... tableNames) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByTable(tableNames);
		return readEntireDirectoryContentAndDelete(tempDir,gf.getGenerator().getOutputEncoding());
	}

	public static String generateBy(Generator g, Map templateModel)
			throws Exception {
		return generateBy(g, templateModel,templateModel);
	}

	public static String generateBy(Generator g, Map templateModel, Map filePathModel)
			throws Exception {
		File tempDir = getOutputTempDir();
		g.setOutRootDir(tempDir.getPath());
		g.generateBy(templateModel, filePathModel,false);
		return readEntireDirectoryContentAndDelete(tempDir,g.getOutputEncoding());
	}

	private static String readEntireDirectoryContentAndDelete(File tempDir,String encoding) throws IOException {
		String result = FileHelper.readEntireDirectoryContent(tempDir,encoding);
		List<File> files = FileHelper.searchAllNotIgnoreFile(tempDir);
		for(File f : files) {
			if(f.isDirectory()) continue;
			String relativePath = FileHelper.getRelativePath(tempDir,f).replace('\\', '/');
			if(StringHelper.isBlank(relativePath)) continue;
			
			result = result + "\n"+"file:"+relativePath;
		}
		try {
			FileHelper.deleteDirectory(tempDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static File getOutputTempDir() {
		File tempDir = new File(FileHelper.getTempDir(), "GeneratorTestHelper/"
				+ count.incrementAndGet() + ".tmp");
		tempDir.deleteOnExit();
		return tempDir;
	}

}
