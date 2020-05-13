package com.c8y.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Base64;

import static com.c8y.mqtt.C8yMqttClient.BROKER_URL;

class HttpClientHelper {

    private static final String FINGERPRINT = "";

    private static final String CERT_IN_PEM_FORMAT = "";

    static void postRootCertificate(String tenant, String username, String password) {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = createBody();
        String url = createPostUrl(tenant);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthorization(username, password))
                .post(requestBody)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (response.code() == 201) {
                System.out.println("Root certificate uploaded for tenant: " + tenant);
            } else {
                System.out.println("Error occurred during uploading root certificate: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error during executing request: " + e);
        }
    }

    @NotNull
    private static RequestBody createBody() {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("certInPemFormat", CERT_IN_PEM_FORMAT);
        body.put("status", "ENABLED");
        body.put("autoRegistrationEnabled", "true");
        return RequestBody.create(body.toString(), JSON);
    }

    static void deleteRootCertificate(String tenant, String username, String password) {
        OkHttpClient httpClient = new OkHttpClient();
        String url = createDeleteUrl(tenant);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getAuthorization(username, password))
                .delete()
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (response.code() == 204) {
                System.out.println("Root certificate deleted for tenant: " + tenant);
            } else {
                System.out.println("Error occurred during deleting root certificate: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error during deleting root certificate: " + e);
        }

    }

    @NotNull
    private static String getAuthorization(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @NotNull
    private static String createPostUrl(String tenant) {
        String domain = "http://" + StringUtils.substringBetween(BROKER_URL, "//", ":");
        return domain + "/tenant/tenants/" + tenant + "/trusted-certificates";
    }

    @NotNull
    private static String createDeleteUrl(String tenant) {
        String domain = "http://" + StringUtils.substringBetween(BROKER_URL, "//", ":");
        return domain + "/tenant/tenants/" + tenant + "/trusted-certificates/" + FINGERPRINT;
    }
}
