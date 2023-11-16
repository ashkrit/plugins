package com.codezen.plugin.ssl;

import com.codezen.plugin.io.MoreIO;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class HttpUtil {


    public static void disableSSL() {

        TrustManager manager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };


        MoreIO.safeExecute(() -> {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{manager}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            return null;
        });
    }


}
