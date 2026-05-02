package grup.proje;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BackendClient {
    private static String BASE_URL;
    private final ObjectMapper mapper = new ObjectMapper();

    static {
        try (InputStream input = BackendClient.class.getClassLoader().getResourceAsStream("client.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("client.properties bulunamadı, varsayılan ayarlar kullanılıyor.");
                BASE_URL = "http://localhost:8088/api";
            } else {
                prop.load(input);
                BASE_URL = prop.getProperty("backend.url", "http://localhost:8088/api");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            BASE_URL = "http://localhost:8088/api";
        }
    }

    private String jwtToken = null;

    private String executeRequest(Request request) throws Exception {
        org.apache.hc.client5.http.fluent.Response response = request.execute();
        org.apache.hc.core5.http.HttpResponse httpResponse = response.returnResponse();
        String content = "";
        if (httpResponse instanceof org.apache.hc.core5.http.ClassicHttpResponse) {
            org.apache.hc.core5.http.HttpEntity entity = ((org.apache.hc.core5.http.ClassicHttpResponse) httpResponse).getEntity();
            if (entity != null) {
                content = org.apache.hc.core5.http.io.entity.EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
        }
        if (httpResponse.getCode() >= 300) {
            if (content.isEmpty()) content = "Hata Kodu: " + httpResponse.getCode();
            throw new Exception(content);
        }
        return content;
    }

    public Map<String, Object> login(String username, String password) throws Exception {
        String json = mapper.writeValueAsString(Map.of("username", username, "password", password));
        String response = executeRequest(Request.post(BASE_URL + "/auth/login")
                .bodyString(json, ContentType.APPLICATION_JSON));
        Map<String, Object> responseMap = mapper.readValue(response, new TypeReference<>() {});
        if (responseMap.containsKey("token")) {
            jwtToken = (String) responseMap.get("token");
        }
        if (responseMap.containsKey("user")) {
            return (Map<String, Object>) responseMap.get("user");
        }
        return responseMap;
    }

    public void register(String username, String password) throws Exception {
        String json = mapper.writeValueAsString(Map.of("username", username, "password", password));
        executeRequest(Request.post(BASE_URL + "/auth/register")
                .bodyString(json, ContentType.APPLICATION_JSON));
    }

    private Request addAuthHeader(Request request) {
        if (jwtToken != null) {
            request.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return request;
    }

    public Map<String, Object> createLobby(String name, String password, String creator, int maxPlayers)
            throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "name", name,
                "password", password,
                "creatorId", creator,
                "maxPlayers", maxPlayers));
        String response = executeRequest(addAuthHeader(Request.post(BASE_URL + "/lobbies/create"))
                .bodyString(json, ContentType.APPLICATION_JSON));
        return mapper.readValue(response, new TypeReference<>() {});
    }

    public List<Map<String, Object>> listLobbies() throws Exception {
        String response = executeRequest(addAuthHeader(Request.get(BASE_URL + "/lobbies/list")));
        return mapper.readValue(response, new TypeReference<>() {});
    }

    public void joinLobby(String lobbyId, String password) throws Exception {
        executeRequest(addAuthHeader(Request.post(BASE_URL + "/lobbies/join/" + lobbyId + "?password="
                + (password == null ? "" : URLEncoder.encode(password, StandardCharsets.UTF_8.name())))));
    }

    public void leaveLobby(String lobbyId) throws Exception {
        executeRequest(addAuthHeader(Request.post(BASE_URL + "/lobbies/leave/" + lobbyId)));
    }

    public void sendChat(String lobbyId, String message) throws Exception {
        executeRequest(addAuthHeader(Request.post(BASE_URL + "/lobbies/" + lobbyId + "/chat?message=" 
                + URLEncoder.encode(message, StandardCharsets.UTF_8.name()))));
    }

    public Map<String, Object> getLobbyDetails(String lobbyId) throws Exception {
        String response = executeRequest(addAuthHeader(Request.get(BASE_URL + "/lobbies/" + lobbyId)));
        return mapper.readValue(response, new TypeReference<>() {});
    }
}
