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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.stategen.framework.util.StringUtil;

/**
 * The Class IOHelpers.
 */
public class IOHelpers {
    public static Writer NULL_WRITER = new NullWriter();

    public static void copy(Reader reader, Writer writer) {
        char[] buf = new char[8192];
        int n = 0;
        try {
            while ((n = reader.read(buf)) != -1) {
                writer.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[8192];
            int n = 0;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readLines(Reader input) {
        try {
            BufferedReader reader = new BufferedReader(input);
            List<String> list = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Reader in) {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static String readFile(File file, String encoding) throws IOException {
        return readFile(file, encoding, false);
    }
    
    public static List<String> splitStringToLine(String dest){
        if (null!=dest ){
            String lines[] = dest.split("\\r?\\n");
            return Arrays.asList(lines);
        }
        return new ArrayList<String>(0);
    }
    
    public static String addLineNumber(String dest){
        List<String> lines =splitStringToLine(dest);
        StringBuffer sb =new StringBuffer();
        int lineNum = 0;
        for (String line : lines) {
            appendWithLineNum(true, line, lineNum, sb); 
            lineNum++;
        }
        return sb.toString();
    }
    

    public static String readFile(File file, String encoding, boolean writeLineNum) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StringUtil.UTF_8);
        BufferedReader read = new BufferedReader(isr);

        String line = null;
        StringBuffer sb = new StringBuffer(1024 * 256);
        if (writeLineNum) {
            int lineNum = 0;
            while ((line = read.readLine()) != null) {
                appendWithLineNum(writeLineNum, line, lineNum, sb);
                lineNum++;
            }
        } else {
            String result = toString(read);
            sb.append(result);
        }
        read.close();
        isr.close();
        return sb.toString();
        //System.out.println(str);//此时str就保存了一行字符串

        //        StringBuffer sb = new StringBuffer();
        //        Reader reader = null;
        //        try {
        //            // 一次读多个字符
        //            char[] tempchars = new char[30];
        //            int charread = 0;
        //            reader = new InputStreamReader(new FileInputStream(file), encoding);
        //            // 读入多个字符到字符数组中，charread为一次读取字符数
        //            reader.rea
        //            while ((charread = reader.read(tempchars)) != -1) {
        //                sb.append(tempchars, 0, charread);
        //            }
        //            return sb.toString();
        //        } finally {
        //            if (reader != null) {
        //                reader.close();
        //            }
        //        }
    }

    private static void appendWithLineNum(boolean writeLineNum, String line, int lineNum, StringBuffer sb) {
        if (lineNum > 0) {
            sb.append('\n');
        }
        
        if (writeLineNum) {
            sb.append("/* ");
            if (lineNum < 10) {
                sb.append("   ");
            } else if (lineNum < 100) {
                sb.append("  ");
            } else if (lineNum < 1000) {
                sb.append(" ");
            }
            sb.append(lineNum).append(" */");
        }
        sb.append(line);
    }

    public static String toString(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream);
        StringWriter writer = new StringWriter();
        copy(reader, writer);
        return writer.toString();
    }

    public static String toString(String encoding, InputStream inputStream) {
        try {
            Reader reader = new InputStreamReader(inputStream, encoding);
            StringWriter writer = new StringWriter();
            copy(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveFile(File file, String content) {
        saveFile(file, content, null, false);
    }

    public static void saveFile(File file, String content, boolean append) {
        saveFile(file, content, null, append);
    }

    public static void saveFile(File file, String content, String encoding) {
        saveFile(file, content, encoding, false);
    }

    public static void saveFile(File file, String content, String encoding, boolean append) {
        try {
            FileOutputStream output = new FileOutputStream(file, append);
            CharArrayWriter charArrayWriter =new CharArrayWriter(content.length());
            Writer writer = StringUtil.isBlank(encoding) ? new OutputStreamWriter(output) : new OutputStreamWriter(output, encoding);
            charArrayWriter.write(content);
            charArrayWriter.writeTo(writer);
            charArrayWriter.close();
            writer.close();
            output.close();
//            writer.write(content);
//            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveFile(File file, InputStream in) {
        try {
            FileOutputStream output = new FileOutputStream(file);
            copy(in, output);
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The Class NullWriter.
     *
     * @author Xia Zhengsheng
     * @version $Id: IOHelper.java, v 0.1 2017-1-3 21:24:09 Xia zhengsheng Exp $
     */
    private static class NullWriter extends Writer {
        public void close() throws IOException {
        }

        public void flush() throws IOException {
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
        }
    }

    public static void copyAndClose(InputStream in, OutputStream out) {
        try {
            copy(in, out);
        } finally {
            close(in, out);
        }
    }

    public static void close(InputStream in, OutputStream out) {
        try {
            if (in != null)
                in.close();
        } catch (Exception e) {
        }
        ;
        try {
            if (out != null)
                out.close();
        } catch (Exception e) {
        }
        ;
    }

}