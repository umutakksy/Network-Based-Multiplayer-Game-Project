package grup.proje;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;

import java.io.InputStream;
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

    private static String jwtToken = null;

    public Map<String, Object> login(String username, String password) throws Exception {
        String json = mapper.writeValueAsString(Map.of("username", username, "password", password));
        String response = Request.post(BASE_URL + "/auth/login")
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute().returnContent().asString();
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
        Request.post(BASE_URL + "/auth/register")
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute();
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
        String response = addAuthHeader(Request.post(BASE_URL + "/lobbies/create"))
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute().returnContent().asString();
        return mapper.readValue(response, new TypeReference<>() {});
    }

    public List<Map<String, Object>> listLobbies() throws Exception {
        String response = addAuthHeader(Request.get(BASE_URL + "/lobbies/list"))
                .execute().returnContent().asString();
        return mapper.readValue(response, new TypeReference<>() {});
    }

    public void joinLobby(String lobbyId, String username, String password) throws Exception {
        addAuthHeader(Request.post(BASE_URL + "/lobbies/join/" + lobbyId + "?username=" + username + "&password="
                + (password == null ? "" : password)))
                .execute();
    }

    public void leaveLobby(String lobbyId, String username) throws Exception {
        addAuthHeader(Request.post(BASE_URL + "/lobbies/leave/" + lobbyId + "?username=" + username))
                .execute();
    }

    public void sendChat(String lobbyId, String sender, String message) throws Exception {
        addAuthHeader(Request.post(BASE_URL + "/lobbies/" + lobbyId + "/chat?sender=" + sender + "&message=" + message))
                .execute();
    }

    public Map<String, Object> getLobbyDetails(String lobbyId) throws Exception {
        String response = addAuthHeader(Request.get(BASE_URL + "/lobbies/" + lobbyId))
                .execute().returnContent().asString();
        return mapper.readValue(response, new TypeReference<>() {});
    }
}
