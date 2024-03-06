package com.c8y.mtls;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class X509RestClient {

	// Configuration
	private static final String KEYSTORE_NAME = "";
	private static final String KEYSTORE_PASSWORD = "";
	private static final String KEYSTORE_FORMAT = "";
	private static final String TRUSTSTORE_NAME = "";
	private static final String TRUSTSTORE_PASSWORD = "";
	private static final String TRUSTSTORE_FORMAT = "";
	private static final String PLATFORM_URL = "";
	private static final String X_SSL_CERT_CHAIN = "x-ssl-cert-chain";
	private static final String DEVICE_ACCESS_TOKEN_PATH = "/devicecontrol/deviceAccessToken";
	private static final String LOCAL_DEVICE_CHAIN = "";
	
	//PEM format
	// example. LOCAL_DEVICE_CHAIN = "-----BEGIN CERTIFICATE----- MIIDaDCCAlCgAwIBAgIUTPAh+EgbLfzb2JL/q7wT90ypqgcwDQYJKoZIhvcNAQEL BQAwTzELMAkGA1UEBhMCRVUxCzAJBgNVBAgMAlBMMRswGQYDVQQKDBJJb3QgRGV2 aWNlIEZhY3RvcnkxFjAUBgNVBAMMDUlvdERldkZhY3RvcnkwHhcNMjMxMjA4MDc0 MDU5WhcNMzMxMjA1MDc0MDU5WjBPMQswCQYDVQQGEwJFVTELMAkGA1UECAwCUEwx GzAZBgNVBAoMEklvdCBEZXZpY2UgRmFjdG9yeTEWMBQGA1UEAwwNSW90RGV2RmFj dG9yeTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKVSPuT68PMLjBX7 1jZpOX0SJx99pwCoIxpBAOyR7aC4CX6mpdTaogjKaHQeBZ84Hvfe7J2TWsNhGtYN JuBXy1hdUuzEt3eyQXKZu/kKQhNJ1UubSJdfmxWH1dZflUFFyq7DsBtfiyvrATFm TyoJiWNHzTOB7KiPjY/0jUXdqu2k+BCNDph+6rAGwIhaZcjTInYejfeZpP2yunoc yJ9AdAEYd0m6bawHUKUTtQpu2lcJ/a0al32KuKv1mSy1o+wGb26ah3Pk4OFtFgUf +SiVjWwGJjaPU080JZ6SBJmyKJeOe25fY9BqABJQTK/baKuaBEfPD274zi7ok2FL GET0dq8CAwEAAaM8MDowDAYDVR0TBAUwAwEB/zALBgNVHQ8EBAMCAuQwHQYDVR0O BBYEFBmEY6IFiPgNX8BHkSig8ucNHyDiMA0GCSqGSIb3DQEBCwUAA4IBAQBNKF67 2MTGo5Vd3BzK1DbBQFDt2N/Hf6S1w9j5r5sqxrNOahKumlRGQ591IubwiCGvY1HJ u+WnhN8eBVfM7qQMrrYSkwTuMEvo+CoOklliZU47ZAqPCdltRLKnIH+LkHiX/Gp9 memWXdThKMcMn8Zamt3LQ6/yypKjxuZRmHg3d9gqvh2K1497k+Yxx4R92EOR+GFw /QjUb+l7+eYxsjT0yvB/GXCWTgKH9KYZ5gr+FvmfCui196OHil2VZHBRuumsTR2r TmG3EC9g5UGWQ/xTU5xCktrldo3CyzDVFOFziowOKmIHzfUUqrS/2fIPHmijTKCw ASFcQhNJJ0F/lfjm -----END CERTIFICATE-----";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		
		TrustManagerFactory trustManagerFactory = getTrustManagerFactory();
		KeyManagerFactory keyManagerFactory = getKeyManagerFactory();
		SSLContext sslContext = getSSLContext(trustManagerFactory, keyManagerFactory);
		HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
		
		HttpResponse<String> response = httpClient.send(buildRequest(), HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
	}

	/**
	 * Gets the SSL context.
	 *
	 * @param trustManagerFactory the trust manager factory
	 * @param keyManagerFactory the key manager factory
	 * @return the SSL context
	 */
	private static SSLContext getSSLContext(TrustManagerFactory trustManagerFactory, KeyManagerFactory keyManagerFactory) {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			return sslContext;
		} catch (NoSuchAlgorithmException | KeyManagementException exception) {
			throw new RuntimeException("Error in creating SSLContext", exception);
		}
	}
	
	
	private static HttpRequest buildRequest() {
		try {
			return HttpRequest.newBuilder().uri(new URI(PLATFORM_URL + DEVICE_ACCESS_TOKEN_PATH))
					.POST(HttpRequest.BodyPublishers.noBody()).header("Accept", "application/json")
					.header(X_SSL_CERT_CHAIN, LOCAL_DEVICE_CHAIN).build();
		} catch (URISyntaxException uRISyntaxException) {
			throw new RuntimeException("Error in creating SSLContext", uRISyntaxException);
		}
	}

	/**
	 * Gets the trust manager factory.
	 *
	 * @return the trust manager factory
	 */
	private static TrustManagerFactory getTrustManagerFactory() {
		try {
			KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_FORMAT);
			InputStream trustStoreFile = new FileInputStream(TRUSTSTORE_NAME);
			trustStore.load(trustStoreFile, TRUSTSTORE_PASSWORD.toCharArray());
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);
			return trustManagerFactory;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException exception) {
			throw new RuntimeException("Error in generating TrustManagerFactory value", exception);
		}
	}

	/**
	 * Gets the key manager factory.
	 *
	 * @return the key manager factory
	 */
	private static KeyManagerFactory getKeyManagerFactory() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KEYSTORE_FORMAT);
			InputStream trustStoreFile = new FileInputStream(KEYSTORE_NAME);
			trustStore.load(trustStoreFile, KEYSTORE_PASSWORD.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(trustStore, KEYSTORE_PASSWORD.toCharArray());
			return keyManagerFactory;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableKeyException exception) {
			throw new RuntimeException("Error creating KeyManagerFactory", exception);
		}
	}
}
