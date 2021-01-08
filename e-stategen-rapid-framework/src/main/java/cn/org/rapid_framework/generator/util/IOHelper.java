package cn.org.rapid_framework.generator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;

/**
 * 
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class IOHelper {
    
    public static Writer NULL_WRITER = new NullWriter();
    
    public static void copy(Reader reader, Writer writer) {
        char[] buf = new char[8192];
        int    n   = 0;
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
            int    n   = 0;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static List<String> readLines(Reader input) {
        try {
            @Cleanup
            BufferedReader reader = new BufferedReader(input);
            List<String>   list   = new ArrayList<>();
            String         line   = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String readFile(File file) {
        try {
            @Cleanup
            Reader in     = new BufferedReader(new FileReader(file));
            String result = toString(in);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("occer IOException when read file:" + file, e);
        }
    }
    
    public static String toString(Reader in) {
        try {
            @Cleanup
            StringWriter out = new StringWriter();
            copy(in, out);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String readFile(File file, String encoding) {
        try {
            @Cleanup
            InputStream inputStream = new FileInputStream(file);
            return toString(encoding, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toString(InputStream inputStream) {
        try {
            @Cleanup
            Reader       reader = new InputStreamReader(inputStream);
            @Cleanup
            StringWriter writer = new StringWriter();
            copy(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toString(String encoding, InputStream inputStream) {
        try {
            @Cleanup
            Reader       reader = new InputStreamReader(inputStream, encoding);
            @Cleanup
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
            @Cleanup
            FileOutputStream output = new FileOutputStream(file, append);
            @Cleanup
            Writer           writer = StringHelper.isBlank(encoding) ? new OutputStreamWriter(output)
                    : new OutputStreamWriter(output, encoding);
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void saveFile(File file, InputStream in) {
        try {
            @Cleanup
            FileOutputStream output = new FileOutputStream(file);
            copy(in, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    private static class NullWriter extends Writer {
        
        public void close() throws IOException {
        }
        
        public void flush() throws IOException {
        }
        
        public void write(char[] cbuf, int off, int len) throws IOException {
        }
    }
    
}
