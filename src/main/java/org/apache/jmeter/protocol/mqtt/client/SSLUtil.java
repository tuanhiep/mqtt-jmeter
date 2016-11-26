package org.apache.jmeter.protocol.mqtt.client;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class SSLUtil {
	private static final Logger log = LoggingManager.getLoggerForClass();
	public static SSLContext getContext(){
		try {
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			// set up a TrustManager that trusts everything
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, new SecureRandom());
			return sslContext;
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}
}
