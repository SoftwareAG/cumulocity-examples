package c8y.example.svensonparse;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
@Slf4j
@AllArgsConstructor
public class SvensonParsingService {
    private final InventoryApi inventoryApi;
    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;

    private static final int ONE_MINUTE = 60 * 1000;

    private final Gson gson = new GsonBuilder().create();

    @EventListener(MicroserviceSubscriptionAddedEvent.class)
    public void onSubscriptionEvent(MicroserviceSubscriptionAddedEvent event) {
        MicroserviceCredentials credentials = event.getCredentials();
        microserviceSubscriptionsService.runForTenant(credentials.getTenant(), () -> {
//            for(int num = 1; num <=400; num++) {
//                log.info("Making child addition api calls - {}", num);
//                childAdditionApiCall(576557);
//                childAdditionApiCall(227557);
////                childAdditionApiCall(317556);
//            }
            log.info("Getting a software with filtering and pagination 400 times");
            SoftwareFilter filter = new SoftwareFilter("ide", null, null, null);

            int pageSize = 200;
            int currentPage = 5;


            for(int num = 1; num <= 400; num++) {
                try {
                    List<Software> softwares = fetchSoftwareList(filter, 317556, pageSize, currentPage);
                    int totalPages = getTotalPages(softwares.size(), pageSize);
                    log.info("Softwares successfully collected - {}. Total number of softwares are {}, total pages are {}", num, softwares.size(), totalPages);
//                    Thread.sleep(3000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

//    private void childAdditionApiCall(int deviceId) {
//        Iterator<ManagedObjectReferenceRepresentation> childAdditionsIt = inventoryApi.getManagedObjectApi(GId.asGId(deviceId))
//                .getChildAdditions()
////                // using type directly as query param doesn't work
//                .get(new QueryParam(() -> "query", "$filter=type+eq+'c8y_InstalledSoftwareList'"))
//                .elements(5000)
//                .iterator();
//    }

//    private void childAdditionApiCall(int deviceId) {
//        String baseUrl = "http://loriot-poll-tst.stage.c8y.io";
//        String path = "/inventory/managedObjects/" + deviceId;
//        String username = "management/admin";
//        String password = "Pyi1co1s@123";
//        try (CloseableHttpClient httpClient = createHttpClient()) {
//            String provider = String.format("%s:%s", username, password);
//            HttpGet request = new HttpGet(baseUrl + path);
//            request.setHeader("Accept", "application/json");
//            request.setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthorization(provider));
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                if (response.getStatusLine().getStatusCode() != 200) {
//                    throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
//                }
//
//                try (InputStream inputStream = response.getEntity().getContent()) {
//                    List<Software> softwareList = extractSoftwareList(inputStream);
//                    // Now you have the first 1000 entries in softwareList
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static CloseableHttpClient createHttpClient() throws IOException {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(ONE_MINUTE)
                        .setConnectTimeout(ONE_MINUTE)
                        .setConnectionRequestTimeout(ONE_MINUTE)
                        .build())
                .setConnectionManagerShared(true)
                .build();
    }


    private String getBasicAuthorization(String provider) {
        return "Basic " + new String(Base64.getEncoder().encode((provider).getBytes()));
    }

    public List<Software> fetchSoftwareListGson(SoftwareFilter filter, int deviceId, int pageSize, int currentPage) throws Exception {
        String apiUrl = "http://loriot-poll-tst.stage.c8y.io/inventory/managedObjects/" + deviceId + "/childAdditions";
        String username = "management/admin";
        String password = "Pyi1co1s@123";

        String provider = String.format("%s:%s", username, password);

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(HttpHeaders.AUTHORIZATION, getBasicAuthorization(provider));

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            JsonObject responseObject = new com.google.gson.JsonParser().parse(reader).getAsJsonObject();
            JsonArray references = responseObject.getAsJsonArray("references");

            JsonArray c8ySoftwareList = new JsonArray();
            // Loop through references to find the c8y_SoftwareList
            for (JsonElement referenceElement : references) {
                JsonObject referenceObject = referenceElement.getAsJsonObject();
                JsonObject managedObject = referenceObject.getAsJsonObject("managedObject");

                if (managedObject.has("c8y_SoftwareList")) {
                    c8ySoftwareList = managedObject.getAsJsonArray("c8y_SoftwareList");
                }
            }

            List<Software> filteredList = StreamSupport.stream(c8ySoftwareList.spliterator(), false)
                    .map(jsonElement -> gson.fromJson(jsonElement, Software.class))
                    .filter(filter)
                    .collect(Collectors.toList());

            SoftwarePagination pagination = new SoftwarePagination(pageSize, currentPage);
            return pagination.paginate(filteredList);
        }
    }

    private List<Software> fetchSoftwareList(SoftwareFilter filter, int deviceId, int pageSize, int currentPage) throws Exception {
        String API_URL = "http://loriot-poll-tst.stage.c8y.io/inventory/managedObjects/" + deviceId + "/childAdditions";

        ObjectMapper mapper = new ObjectMapper();
        List<Software> filteredSoftwares = new ArrayList<>();

        String username = "management/admin";
        String password = "Pyi1co1s@123";

        String provider = String.format("%s:%s", username, password);

        // Call the API and get the JSON response
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(HttpHeaders.AUTHORIZATION, getBasicAuthorization(provider));

        JsonNode rootNode = mapper.readTree(conn.getInputStream());
        JsonNode referencesNode = rootNode.path("references");

        // Loop through each managedObject
        for (JsonNode referenceNode : referencesNode) {
            JsonNode managedObjectNode = referenceNode.path("managedObject");
            JsonNode softwareListNode = managedObjectNode.path("c8y_SoftwareList");

            // Parse and filter the software list
            for (JsonNode softwareNode : softwareListNode) {
                Software software = mapper.treeToValue(softwareNode, Software.class);

                // Apply the filter
                if (filter.test(software)) {
                    filteredSoftwares.add(software);
                }
            }
        }

        // Apply pagination
        SoftwarePagination pagination = new SoftwarePagination(pageSize, currentPage);
        return pagination.paginate(filteredSoftwares);
    }

    public int getTotalPages(int totalItems, int pageSize) {
        SoftwarePagination pagination = new SoftwarePagination(pageSize, 1);
        return pagination.getTotalPages(totalItems);
    }


    private static List<Software> extractSoftwareList(InputStream inputStream) throws IOException {
        List<Software> softwareList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(inputStream);

        // Traverse the JSON structure to get to the c8y_SoftwareList array
        while (!parser.isClosed()) {
            JsonToken token = parser.nextToken();

            if (JsonToken.FIELD_NAME.equals(token) && "c8y_SoftwareList".equals(parser.getCurrentName())) {
                token = parser.nextToken(); // Start of array
                if (token == JsonToken.START_ARRAY) {
                    while (parser.nextToken() != JsonToken.END_ARRAY && softwareList.size() < 1000) {
                        Software software = mapper.readValue(parser, Software.class);
                        softwareList.add(software);
                    }
                }
            }
        }

        parser.close();
        return softwareList;
    }
}
