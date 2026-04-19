package com.nexus.network;

public class NetworkService {
    // Senin sunucu adresin
    private static final String SERVER_DOMAIN = "pts.seedhr.com.tr";
    private static final String SERVER_PORT = "8088";

    // WebSocket bağlantı URL'i
    public static final String WS_URL = "ws://" + SERVER_DOMAIN + ":" + SERVER_PORT + "/game-websocket";

    // REST API bağlantı URL'i (Login, Register vb. için)
    public static final String API_URL = "http://" + SERVER_DOMAIN + ":" + SERVER_PORT + "/api/v1";

    public static void connect() {
        System.out.println("Bağlanılıyor: " + WS_URL);
        // Burada WebSocket istemcisi başlatılacak
    }
}
