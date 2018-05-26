package org.wisestar.lottery.util.http;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * Simple HTTP client for JavaScript, using HttpClient 4
 *
 * @author zhangxu
 */
public class SimpleHttpClient {

    private static final String DEFAULT_USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:56.0) Gecko/20100101 Firefox/56.0";
    private PoolingHttpClientConnectionManager connectionManager;
    private int timeout = 30000;
    private final static Map<String, String> DEFAULT_CONFIG = new HashMap<>();
    public final static String CONFIG_KEY_USER_AGENT = "userAgent";

    private static class SimpleHttpClientHolder {
        private static final SimpleHttpClient INSTANCE = new SimpleHttpClient();
    }

    public static SimpleHttpClient getCurrent() {
        return SimpleHttpClientHolder.INSTANCE;
    }

    private SimpleHttpClient() {
    }

    private HttpClient createClient(Map<String, String> config) {
        if (config == null) {
            config = DEFAULT_CONFIG;
        }

        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(100);
        connectionManager.setMaxTotal(20);
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connectionManager);
        if (config.containsKey(CONFIG_KEY_USER_AGENT)) {
            builder.setUserAgent(DEFAULT_USERAGENT);
        } else {
            builder.setUserAgent(config.get(CONFIG_KEY_USER_AGENT));
        }

        builder.addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
            if (!request.containsHeader(HttpHeaders.ACCEPT_ENCODING)) {
                request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
            }
        });

        builder.addInterceptorLast((HttpResponseInterceptor) (response, context) -> {
            HttpEntity entity = response.getEntity();
            Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                HeaderElement[] codecs = ceheader.getElements();
                for (int i = 0; i < codecs.length; i++) {
                    if ("gzip".equalsIgnoreCase(codecs[i].getName())) {
                        response.setEntity(new GzipDecompressingEntity(entity));
                        return;
                    }
                }
            }
        });

        return builder.build();
    }

    private HttpUriRequest createUriRequest(String method, String url, Map<String, String> params) throws UnsupportedEncodingException {
        RequestBuilder requestBuilder = RequestBuilder.create(method).setUri(url);

        if (method.equals(HttpPost.METHOD_NAME) && params != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            requestBuilder.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }

        RequestConfig.Builder requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .setCookieSpec(CookieSpecs.DEFAULT);

        requestBuilder.setConfig(requestConfig.build());
        return requestBuilder.build();
    }

    public SimpleHttpResponse get(String url) {
        return get(url, null);
    }

    public SimpleHttpResponse get(String url, Map<String, String> config) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        HttpClient httpClient = createClient(config);
        try {
            HttpUriRequest httpUriRequest = createUriRequest(HttpGet.METHOD_NAME, url, null);
            HttpResponse rsp = httpClient.execute(httpUriRequest);
            return wrapResponse(rsp);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public SimpleHttpResponse post(String url, Map<String, String> params) {
        return post(url, params, null);
    }

    public SimpleHttpResponse post(String url, Map<String, String> params, Map<String, String> config) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        HttpClient httpClient = createClient(config);
        try {
            HttpUriRequest httpUriRequest = createUriRequest(HttpPost.METHOD_NAME, url, params);
            HttpResponse rsp = httpClient.execute(httpUriRequest);
            return wrapResponse(rsp);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private byte[] toBytes(InputStream stream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int len = 0;
            while ((len = stream.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return null;
        }
    }

    private SimpleHttpResponse wrapResponse(HttpResponse rsp) throws IOException {
        SimpleHttpResponse response = new SimpleHttpResponse();
        StatusLine status = rsp.getStatusLine();
        response.setProtocol(status.getProtocolVersion().getProtocol());
        response.setProtocolVersion(status.getProtocolVersion().toString());
        response.setStatusCode(status.getStatusCode());
        response.setStatusReason(status.getReasonPhrase());
        Map<String, String> headers = new HashMap<>(rsp.getAllHeaders().length);
        for (Header h : rsp.getAllHeaders()) {
            headers.put(h.getName(), h.getValue());
        }
        response.setHeaders(headers);

        HttpEntity entity = rsp.getEntity();
        if (entity != null) {
            response.setResponseBody(toBytes(entity.getContent()));
        }
        return response;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connectionManager.shutdown();
    }

    static class GzipDecompressingEntity extends HttpEntityWrapper {

        GZIPInputStream contentStream;

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException,
                IllegalStateException {

            if (contentStream == null) {
                contentStream = new GZIPInputStream(wrappedEntity.getContent());
            }
            // the wrapped entity's getContent() decides about repeatability
            return contentStream;
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
