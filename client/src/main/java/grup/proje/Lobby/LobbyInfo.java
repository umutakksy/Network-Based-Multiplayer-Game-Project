package grup.proje.Lobby;

import java.util.Map;

public class LobbyInfo {
    public String id;
    public String hostName;
    public String playerCount;
    public String status;
    public boolean hasPassword;

    public LobbyInfo(String id, String hostName, String playerCount, String status, boolean hasPassword) {
        this.id = id;
        this.hostName = hostName;
        this.playerCount = playerCount;
        this.status = status;
        this.hasPassword = hasPassword;
    }

    public static LobbyInfo fromMap(Map<String, Object> map) {
        return new LobbyInfo(
            (String) map.get("id"),
            (String) map.get("name"),
            map.get("memberUsernames") != null ? ((java.util.List)map.get("memberUsernames")).size() + "/" + map.get("maxPlayers") : "0/4",
            (Boolean) map.getOrDefault("isStarted", false) ? "Savaşta" : "Lobide",
            map.get("password") != null && !((String)map.get("password")).isEmpty()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LobbyInfo) {
            LobbyInfo other = (LobbyInfo) obj;
            return this.id.equals(other.id);
        }
        return false;
    }
}