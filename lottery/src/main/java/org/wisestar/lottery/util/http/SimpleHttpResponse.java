package org.wisestar.lottery.util.http;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Wrapped response from the Simple Http Client
 *
 * @author zhangxu
 */
public class SimpleHttpResponse {

    private String protocol;
    private String protocolVersion;
    private int statusCode;
    private String statusReason;
    private Map<String, String> headers;
    private String responseText;
    private byte[] responseBody;

    private String autoGetString(InputStream stream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int len = 0;
            boolean done = false;
            boolean isAscii = true;
            nsDetector det = new nsDetector(nsPSMDetector.ALL);
            CharsetDetResult charsetResult = new CharsetDetResult();
            det.Init(charsetResult);
            while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);

                // Check if the stream is only ascii.
                if (isAscii) {
                    isAscii = det.isAscii(buffer, len);
                }

                // DoIt if non-ascii and not done yet.
                if (!isAscii && !done) {
                    done = det.DoIt(buffer, len, false);
                }
            }
            det.DataEnd();
            if (isAscii) {
                charsetResult.found = true;
            }
            if (!charsetResult.found) {
                String[] prob = det.getProbableCharsets();
                if (prob.length > 0) {
                    charsetResult.charset = prob[0];
                }
            }
            if (charsetResult.charset == null || charsetResult.charset.isEmpty()) {
                return new String(baos.toByteArray());
            } else {
                return new String(baos.toByteArray(), charsetResult.charset);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return null;
        }
    }

    private class CharsetDetResult implements nsICharsetDetectionObserver {

        public boolean found = false;
        public String charset = null;

        @Override
        public void Notify(String charset) {
            this.found = true;
            this.charset = charset;
        }
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getResponseText() {
        if (responseBody == null) {
            return null;
        }
        if (responseText == null) {
            responseText = autoGetString(new ByteArrayInputStream(responseBody));
        }
        return responseText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }
}
