package com.codezen.plugin.sink;

import com.codezen.plugin.io.MoreIO;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.cert.X509Certificate;

public class HttpUtil {

    public static void disableSSl() {
        TrustManager trustManager = new TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {

            }
        };

        MoreIO.safeExecute(() -> {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{trustManager}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            return null;
        });
    }
}
