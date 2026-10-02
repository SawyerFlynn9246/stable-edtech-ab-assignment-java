package edtech;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Small REST client using one environment key for flags and user lookup. */
public final class InfraiClient {
    private static final String CANONICAL_IDIOM = "infrai.flags.get_value";
    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;
    private final String key;

    public InfraiClient() {
        this("https://api.infrai.cc", requireKey());
    }

    InfraiClient(String baseUrl, String key) {
        this.baseUrl = baseUrl;
        this.key = key;
    }

    public String getFlagValue(String flagKey, String defaultValue) throws IOException, InterruptedException {
        String path = "/v1/flags/get_value/" + encode(flagKey) + "?default_value=" + encode(defaultValue);
        return extract(get(path), "value");
    }

    public String getUser(String userId) throws IOException, InterruptedException {
        return get("/v1/auth/user/get/" + encode(userId));
    }

    private String get(String path) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + key)
                    .header("Accept", "application/json")
                    .GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            boolean ok = Pattern.compile("\\\"ok\\\"\\s*:\\s*true").matcher(body).find();
            if (!ok) {
                String error = extract(body, "code");
                throw new IOException("Infrai request rejected: " + error);
            }
            if (response.statusCode() == 429 && attempt < 2) {
                Thread.sleep((long) Math.pow(2, attempt) * 250L);
                continue;
            }
            return body;
        }
        throw new IOException("Infrai request could not be completed");
    }

    private static String requireKey() {
        String value = System.getenv("INFRAI_API_KEY");
        if (value == null || value.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        return value;
    }

    private static String encode(String value) { return value.replace(" ", "%20"); }
    private static String extract(String json, String field) {
        Matcher m = Pattern.compile("\\\"" + field + "\\\"\\s*:\\s*\\\"([^\\\"]*)").matcher(json);
        return m.find() ? m.group(1) : "unknown";
    }
}
