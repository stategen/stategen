package cn.org.rapid_framework.generator.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
public class ZipUtils {

	private static AtomicLong tempFileCount = new AtomicLong(System.currentTimeMillis());
	
	public static File unzip2TempDir(File zipfile,String tempRootFolderName){
		try {
			File tempFolder = new File(System.getProperty("java.io.tmpdir"),tempRootFolderName+"/"+tempFileCount.incrementAndGet()+".tmp");
			if(!tempFolder.mkdirs()) {
				throw new RuntimeException("cannot make temp folder:"+tempFolder);
			}
			InputStream in = new BufferedInputStream(new FileInputStream(zipfile));
			unzip(tempFolder,in);
			in.close();
			return tempFolder;
		}catch(IOException e) {
			throw new RuntimeException("cannot create temp folder",e);
		}
	}
	/**
	 * 将输入的压缩文件流解压到解压目录(unzipDir)
	 * @param unzipDir 解压目录
	 * @param in 压缩文件流
	 * @throws IOException
	 */
	public static void unzip(File unzipDir, InputStream in) throws IOException {
		unzipDir.mkdirs();
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry entry = null;
		while ((entry = zin.getNextEntry()) != null) {
			File path = new File(unzipDir, entry.getName());
			if (entry.isDirectory()) {
				path.mkdirs();
			} else {
				FileHelper.parentMkdir(path.getAbsoluteFile());
				IOHelper.saveFile(path, zin);
			}
		}
	}
	
	/**
	 * 解压文件
	 * @param unzipDir 解压目录
	 * @param zipFile 压缩文件
	 * @throws IOException
	 */
	public static void unzip(File unzipDir,File zipFile) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
		unzip(unzipDir,in);
		in.close();
	}

	private static void makedirs(File f){
		File ff = new File(f.getParent());
		if(!ff.exists()) {
			ff.mkdirs();
		}
	}
}