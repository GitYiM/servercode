package unnet.weixin.netdisk.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.StandardConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpsUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpsUtils.class);
    static CloseableHttpClient httpClient;
    static CloseableHttpResponse httpResponse;

    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // ????????????
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();

    }

    /**
     * ??????HTTP_GET??????
     *
     * @param decodeCharset ???????????????,???????????????????????????,??????null???????????????UTF-8??????
     * @return ????????????????????????
     */
    public static String sendGetRequest(String reqURL, String decodeCharset) {
        long responseLength = 0;       //????????????
        String responseContent = null; //????????????
        if (httpClient == null) {
            httpClient = HttpClientBuilder
                    .create()
     //               .setProxy(new HttpHost("202.202.43.209", 1235, "http")) //????????????????????????
                    .build(); //???????????????httpClient??????
        }
        HttpGet httpGet = new HttpGet(reqURL);           //??????org.apache.http.client.methods.HttpGet
        try {
            httpResponse = httpClient.execute(httpGet); //??????GET??????
            HttpEntity entity = httpResponse.getEntity();            //??????????????????
            if (null != entity) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity); //Consume response content
            }
            System.out.println("????????????: " + httpGet.getURI());
            System.out.println("????????????: " + httpResponse.getStatusLine());
            System.out.println("????????????: " + responseLength);
            System.out.println("????????????: " + responseContent);
        } catch (ClientProtocolException e) {
            logger.debug("????????????????????????????????????,????????????HttpGet??????????????????????????????(???'http'??????'htp')??????????????????????????????????????????HTTP???????????????,??????????????????", e);
        } catch (ParseException e) {
            logger.debug(e.getMessage(), e);
        } catch (IOException e) {
            logger.debug("???????????????????????????????????????,???HTTP?????????????????????,??????????????????", e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
//               httpClient.close();
                logger.info("?????????????????????");
            } catch (IOException e) {
                logger.info("?????????????????????");
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * ??????https??????
     *
     * @param params
     * @param url
     * @return
     */
    public static String sendByHttp(Map<String, Object> params, String url) {
        try {
            HttpPost httpPost = new HttpPost(url);
            String json = JSON.toJSONString(params);
            StringEntity entity = new StringEntity(json, "UTF-8");
            httpPost.setHeader("Content-Type", "application/json;charset=utf8");
            httpPost.setEntity(entity);
            if (httpClient == null) {
                httpClient = HttpClientBuilder
                        .create()
        //                .setProxy(new HttpHost("202.202.43.209", 1235, "http")) //????????????????????????
                        .build(); //???????????????httpClient??????
            }
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
//                httpClient.close();
                logger.info("?????????????????????");
            } catch (IOException e) {
                logger.info("?????????????????????");
                e.printStackTrace();
            }
        }
    }

    public static String sendFile(String fileName, String type, String url, InputStream file, ContentType contentType) {
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //??????UTF-8??????????????????????????????????????????
            builder.setMode(HttpMultipartMode.RFC6532)
                    .addBinaryBody(type, file, contentType, fileName);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            if (httpClient == null) {
                httpClient = HttpClientBuilder
                        .create()
   //                     .setProxy(new HttpHost("202.202.43.209", 1235, "http")) //????????????????????????
                        .build(); //???????????????httpClient??????
            }
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
//                httpClient.close();
                logger.info("?????????????????????");
            } catch (IOException e) {
                logger.info("?????????????????????");
                e.printStackTrace();
            }
        }
    }
}

