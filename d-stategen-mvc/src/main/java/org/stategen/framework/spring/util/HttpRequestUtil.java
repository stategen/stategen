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
package org.stategen.framework.spring.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.stategen.framework.util.StringUtil;

import lombok.Cleanup;

/**
 *  
 * Http请求工具类 .
 *
 * @author snowfigure
 * @version v1.0.1
 * @since 2014-8-24 13:30:56
 */
public class HttpRequestUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HttpRequestUtil.class);
    static boolean proxySet = false;
    static String proxyHost = "127.0.0.1";
    static int proxyPort = 8087;

    /** 
     * 编码 
     * @param source 
     * @return 
     */
    public static String urlEncode(String source, String encode) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return result;
    }

    public static String urlEncodeGBK(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return result;
    }

    /** 
     * 发起http请求获取返回结果 
     * @param req_url 请求地址 
     * @return 
     * @throws IOException 
     */
    public static String httpRequest(String req_url) throws IOException {
        StringBuffer buffer = new StringBuffer();
        URL url = new URL(req_url);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

        httpUrlConn.setDoOutput(false);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);

        httpUrlConn.setRequestMethod("GET");
        httpUrlConn.connect();

        // 将返回的输入流转换成字符串 
        @Cleanup
        InputStream inputStream = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StringUtil.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        httpUrlConn.disconnect();

        return buffer.toString();
    }

    /** 
     * 发送http请求取得返回的输入流 
     * @param requestUrl 请求地址 
     * @return InputStream 
     * @throws IOException 
     */
    public static InputStream httpRequestIO(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
        httpUrlConn.setDoInput(true);
        httpUrlConn.setRequestMethod("GET");
        httpUrlConn.connect();
        // 获得返回的输入流 
        InputStream inputStream = httpUrlConn.getInputStream();
        return inputStream;
    }

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *      发送请求的URL
     * @param params
     *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     * @throws IOException 
     */
    public static String sendGet(String url, String params) throws IOException {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            if (StringUtil.isNotBlank(params)) {
                urlNameString = urlNameString + "?" + params;
            }

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                //skip
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *      发送请求的 URL
     * @param params
     *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param isProxy
     *        是否使用代理模式
     * @return 所代表远程资源的响应结果
     * @throws IOException 
     */
    public static String sendPost(String url, String params, boolean isProxy) throws IOException {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = null;
            if (isProxy) {//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) realUrl.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            // 打开和URL之间的连接

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // POST方法

            // 设置通用的请求属性

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (StringUtil.isNotBlank(params)) {
                out.write(params);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                //skip
            }
        }
        return result;
    }

    public static Object proxy(String targetServer, String orgUrlRex, String reWrite) throws IOException {
        HttpServletRequest request = RequestUtil.getRequest();
        String requestURI = RequestUtil.getRequestPath();
        String newURI = requestURI.replaceFirst(orgUrlRex, reWrite);
        StringBuffer newUrlBuffer = new StringBuffer(targetServer).append(newURI);
        String queryString = request.getQueryString();
        String newUrl = newUrlBuffer.toString();
        String result = HttpRequestUtil.sendGet(newUrl, queryString);
        return result;
    }

}