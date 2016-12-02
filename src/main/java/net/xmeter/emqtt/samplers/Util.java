package net.xmeter.emqtt.samplers;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.apache.log.Priority;

public class Util implements Constants{
	
	public static String generateClientId(String prefix) {
		int leng = prefix.length();
		int postLeng = MAX_CLIENT_ID_LENGTH - leng;
		UUID uuid = UUID.randomUUID();
		String string = uuid.toString().replace("-", "");
		String post = string.substring(0, postLeng);
		return prefix + post;
	}
	
	public static SSLContext getContext(boolean clientAuth) throws Exception {
		if (!clientAuth) {
		System.out.println("****setSslContext1: ");
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
	
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, new SecureRandom());
			System.out.println("****setSslContext1: done");
			return sslContext;
		} else {
		System.out.println("****setSslContext2: ");
			String CA_KEYSTORE_PASS = "123456";
			String CLIENT_KEYSTORE_PASS = "123456";
			
			InputStream is_cacert = Util.class.getResourceAsStream("/cacert.jks");
			InputStream is_client = Util.class.getResourceAsStream("/client.p12");

		System.out.println("is_cacert=" + is_cacert + ", is_client=" + is_client);
			KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType()); // jks
			tks.load(is_cacert, CA_KEYSTORE_PASS.toCharArray());

			KeyStore cks = KeyStore.getInstance("PKCS12");
			cks.load(is_client, CLIENT_KEYSTORE_PASS.toCharArray());

			SSLContext sslContext = SSLContexts.custom()
						.loadTrustMaterial(tks, new TrustSelfSignedStrategy()) // use it to customize
			            .loadKeyMaterial(cks, CLIENT_KEYSTORE_PASS.toCharArray()) // load client certificate
			            .build();
			System.out.println("****setSslContext2: done");
			return sslContext;
		}
	}
}
