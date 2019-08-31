package org.stategen.framework.spring.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
 
public class HttpsTrustManager implements X509TrustManager {
    /*
     * 处理https GET/POST请求 请求地址、请求方法、参数
     */
    public static String httpsRequest(String requestUrl, String requestMethod,
            String outputStr) {
        StringBuffer buffer = null;
        try {
            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = { new HttpsTrustManager() };
            // 初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            ;
            // 获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            // url对象
            URL url = new URL(requestUrl);
            // 打开连接
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            /**
             * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
             * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
             * 
             * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
             * 
             * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be localhost
             */
            conn.setHostnameVerifier(new HttpsTrustManager().new TrustAnyHostnameVerifier());
            // 设置一些参数
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            // 设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            // 往服务器端的参数
            if (null != outputStr) {
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            //读取内容
            InputStreamReader isr = new InputStreamReader(is,"utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
 
 
    public static void downLoadFromUrlHttps(String urlStr, String fileName,
            String savePath) throws Exception {
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] tm = { new HttpsTrustManager() };
        // 初始化
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        // url对象
        URL url = new URL(urlStr);
        // 打开连接
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        /**
         * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
         * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
         * 
         * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
         * 
         * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be <localhost>
         */
        conn.setHostnameVerifier(new HttpsTrustManager().new TrustAnyHostnameVerifier());
        // 设置一些参数
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置当前实例使用的SSLSoctetFactory
        conn.setSSLSocketFactory(ssf);
        conn.connect();
 
 
        // 得到输入流
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        // 文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        //输出流
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }
    public static void downloadHttpUrl(String urlStr, String fileName,String savePath) throws IOException {

        URL urlPath = new URL(urlStr);
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String newPath =savePath + fileName;
        FileUtils.copyURLToFile(urlPath, new File(newPath));
    }

    public static void httpGetImg(CloseableHttpClient client,String imgUrl,String savePath) {
        
         
        // 发送get请求
        HttpGet request = new HttpGet(imgUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(50000).setConnectTimeout(50000).build();
        
        //设置请求头
        request.setHeader( "User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1" );
        
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);
            
             if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) { 
                  HttpEntity entity = response.getEntity();  
                  
                  InputStream in = entity.getContent();  
                  
                  FileUtils.copyInputStreamToFile(in, new File(savePath));
                 
             }
             
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            request.releaseConnection();
            
        }
    }

    /**
     * 从网络http类型Url中下载文件
     * 
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static boolean downLoadFromUrlHttp(String urlStr, String fileName,
            String savePath) throws IOException {
        String filePath =savePath + File.separator + fileName;
        File file = new File(filePath);
        if (file.isFile() && file.exists()){
            return false;
        }
        
        if (urlStr.startsWith("http://")){
            try {
                CloseableHttpClient createDefault = HttpClients.createDefault();
                httpGetImg(createDefault, urlStr,savePath+fileName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        // 防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        try {
            conn.connect();
        } catch (UnknownHostException e) {
            return false;
        }
        
        // 得到输入流
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        // 文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        // 输出流
        
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return true;
    }
 
 
    /**
     * 从输入流中获取字节数组
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream)
            throws IOException {
        byte[] b = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
 
 
    /***
     * 校验https网址是否安全
     * 
     * @author solexit06
     * 
     */
    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            // 直接返回true:默认所有https请求都是安全的
            return true;
        }
    }
 
 
    /*
     * 里面的方法都是空的，当方法为空是默认为所有的链接都为安全，也就是所有的链接都能够访问到 当然这样有一定的安全风险，可以根据实际需要写入内容
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }
 
 
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }
 
 
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}

// ———————————————— 
//版权声明：本文为CSDN博主「beinlife」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
//原文链接：https://blog.csdn.net/beinlife/article/details/80938219