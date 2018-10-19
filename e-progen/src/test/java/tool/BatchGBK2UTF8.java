package tool;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class BatchGBK2UTF8 {

    @Test
    public void convert() throws Exception {
        //GBK编码格式源码路径
        String srcDirPath = "E:\\SOFTSETUP\\webtools\\java\\tbschedule\\tbschedule\\trunk\\";
        //转为UTF-8编码格式源码路径
        String utf8DirPath = "E:\\SOFTSETUP\\webtools\\java\\tbschedule\\tbschedule\\trunk\\";

        //获取所有java文件
        Collection<File> javaGbkFileCol = FileUtils.listFiles(new File(srcDirPath), new String[] { "java","jsp" }, true);

        for (File javaGbkFile : javaGbkFileCol) {
            //UTF8格式文件路径
            String utf8FilePath = utf8DirPath + javaGbkFile.getAbsolutePath().substring(srcDirPath.length());
            //使用GBK读取数据，然后用UTF-8写入数据
            FileUtils.writeLines(new File(utf8FilePath), "UTF-8", FileUtils.readLines(javaGbkFile, "GBK"));
        }
    }

}
