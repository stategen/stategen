/*
 * copy from rapid-framework<code.google.com/p/rapid-framework> and modify by niaoge
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
package cn.org.rapid_framework.generator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.stategen.framework.util.StringUtil;

/**
 * The Class<?> FileHelper.
 *
 * @author Xia Zhengsheng
 * @version $Id: FileHelper.java, v 0.1 2017-1-3 21:22:31 Xia zhengsheng Exp $
 */
public class FileHelper {
    public static File getFile(String file) {
        if (StringUtil.isBlank(file)) {
            throw new IllegalArgumentException("'file' must be not blank");
        }
        try {
            if (file.startsWith("classpath:")) {
                return getFileByClassLoader(file.substring("classpath:"
                        .length()));
            } else {
                return new File(toFilePathIfIsURL(new File(file)));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.toString(), e);
        } catch (IOException e) {
            throw new RuntimeException("getFile() error,file:" + file, e);
        }
    }

    public static InputStream createInputStream(String file)
            throws FileNotFoundException {
        InputStream inputStream;
        if (file.startsWith("classpath:")) {
            inputStream = ClassHelper.getDefaultClassLoader()
                    .getResourceAsStream(file.substring("classpath:".length()));
        } else {
            inputStream = new FileInputStream(file);
        }
        return inputStream;
    }



    /**
     * 读取整个目录下所有子文件的内容至String中.
     *
     * @param dir the dir
     * @param encoding the encoding
     * @return the string
     * @throws IOException 
     */
    public static String readEntireDirectoryContent(File dir, String encoding) throws IOException {
        List<File> files = searchAllNotIgnoreFile(dir);
        StringBuilder result = new StringBuilder();
        for (File file : files) {
            if (file.isDirectory())
                continue;
            if (file.isFile() && file.exists()) {
                result.append(IOHelper.readFile(file, encoding));
            }
        }
        return result.toString();
    }

    public static File mkdir(String dir, String file) {
        if (dir == null)
            throw new IllegalArgumentException("dir must be not null");
        File result = new File(dir, file);
        parentMkdir(result);
        return result;
    }
    
    public static String getCanonicalPath(String fileName) throws IOException {
        return getFile(fileName).getCanonicalPath();
    }    
    
    public static File getFileByClassLoader(String resourceName)
            throws IOException {
        String pathToUse = resourceName;
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        Enumeration<URL> urls = ClassHelper.getDefaultClassLoader()
                .getResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        urls = FileHelper.class.getClassLoader().getResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        urls = ClassLoader.getSystemResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        throw new FileNotFoundException("classpath:" + resourceName);
    }
    
    /**
     *  搜索目录下的所有文件,并忽略如 .svn .cvs等文件 ,并且只能含有 如 .java ,.xml的文件
     *
     * @param dir the dir
     * @param extractFileName the extract file name
     * @return the list
     */
    public static List<File> searchAllNotIgnoreFile(File dir,Set<String> extractFileName) {
        ArrayList<File> arrayList = new ArrayList<File>();
        searchAllNotIgnoreFile(dir, arrayList);
        for (int i = arrayList.size()-1; i >=0; i--) {
            File file =arrayList.get(i);
            if (file.isDirectory()){
                arrayList.remove(i);
                continue;
            }
            
            String fileName=file.getName();
            String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
            GLogger.info(prefix);
            
            if (!extractFileName.contains(prefix)){
                arrayList.remove(i);
                continue;
            }
        }
        
        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return arrayList;
    }    
    
    
    public static void searchAllNotIgnoreFile(File dir, List<File> collector) {
        collector.add(dir);
        if ((!dir.isHidden() && dir.isDirectory()) && !isIgnoreFile(dir)) {
            File[] subFiles = dir.listFiles();
            for (int i = 0; i < subFiles.length; i++) {
                searchAllNotIgnoreFile(subFiles[i], collector);
            }
        }
    }  
    
    public static Set<String> ignoreList = new HashSet<String>();
    static {
        ignoreList.add(".svn");
        ignoreList.add("CVS");
        ignoreList.add(".cvsignore");
        ignoreList.add(".copyarea.db"); // ClearCase
        ignoreList.add("SCCS");
        ignoreList.add("vssver.scc");
        ignoreList.add(".DS_Store");
        ignoreList.add(".git");
        ignoreList.add(".gitignore");
    }

    private static boolean isIgnoreFile(File file) {
        return ignoreList.contains(file.getName());
//        for (int i = 0; i < ignoreList.size(); i++) {
//            if (file.getName().equals(ignoreList.get(i))) {
//                return true;
//            }
//        }
//        return false;
    }

    public static Set<String> binaryExtentionsList = new HashSet<String>();
    static {
        loadBinaryExtentionsList("binary_filelist.txt", true);
        loadBinaryExtentionsList(
                "cn/org/rapid_framework/generator/util/binary_filelist.txt",
                false);
    }

    public static void loadBinaryExtentionsList(String resourceName,
            boolean ignoreException) {
        try {
            Enumeration<URL> urls = FileHelper.class.getClassLoader()
                    .getResources(resourceName);
            boolean notFound = true;
            while (urls.hasMoreElements()) {
                notFound = false;
                URL url = urls.nextElement();
                InputStream input = url.openStream();
                binaryExtentionsList.addAll(IOHelper
                        .readLines(new InputStreamReader(input)));
                input.close();
            }
            if (notFound)
                throw new IllegalStateException("not found required file with:"
                        + resourceName);
        } catch (Exception e) {
            if (!ignoreException)
                throw new RuntimeException(
                        "loadBinaryExtentionsList occer error,resourceName:"
                                + resourceName, e);
        }
    }
    /**
     * 得到相对路径.
     *
     * @param baseDir the base dir
     * @param file the file
     * @return the relative path
     */
    public static String getRelativePath(File baseDir, File file) {
        if (baseDir.equals(file))
            return "";
        if (baseDir.getParentFile() == null)
            return file.getAbsolutePath().substring(
                    baseDir.getAbsolutePath().length());
        return file.getAbsolutePath().substring(
                baseDir.getAbsolutePath().length() + 1);
    }

    public static File parentMkdir(String file) {
        if (file == null)
            throw new IllegalArgumentException("file must be not null");
        File result = new File(file);
        parentMkdir(result);
        return result;
    }

    public static void parentMkdir(File outputFile) {
        File parentFile = outputFile.getParentFile();
        if (parentFile != null && !parentFile.equals(outputFile)) {
            parentFile.mkdirs();
        }
    }

    /** 搜索目录下的所有文件,并忽略如 .svn .cvs等文件 */
    public static List<File> searchAllNotIgnoreFile(File dir) {
        ArrayList<File> arrayList = new ArrayList<File>();
        searchAllNotIgnoreFile(dir, arrayList);
        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return arrayList;
    }
    /**
     *  检查文件是否是二进制文件.
     *
     * @param file the file
     * @return true, if is binary file
     */
    public static boolean isBinaryFile(File file) {
        if (file.isDirectory())
            return false;
        return isBinaryFile(file.getName());
    }

    public static boolean isBinaryFile(String filename) {
        if (StringUtil.isBlank(getExtension(filename)))
            return false;
        return binaryExtentionsList.contains(getExtension(filename)
                .toLowerCase());
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.indexOf(".");
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Deletes a directory recursively.
     * 
     * @param directory
     *            directory to delete
     * @throws IOException
     *             in case deletion is unsuccessful
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Deletes a file, never throwing an exception. If file is a directory,
     * delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
     * </ul>
     * 
     * @param file
     *            file or directory to delete, can be <code>null</code>
     * @return <code>true</code> if the file or directory was deleted, otherwise
     *         <code>false</code>
     * 
     * @since Commons IO 1.4
     */
    public static boolean deleteQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception e) {
        }

        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cleans a directory without deleting it.
     * 
     * @param directory
     *            directory to clean
     * @throws IOException
     *             in case cleaning is unsuccessful
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: "
                            + file);
                }
                throw new IOException("Unable to delete file: " + file);
            }
        }
    }

    public static String toFilePathIfIsURL(File file) {
        try {
            return new URL(((File) file).getPath()).getPath();
        } catch (MalformedURLException e) {
            // ignore,fallback to file.getPath()
            return file.getPath();
        }
    }

    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
