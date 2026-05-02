package com.example.pts.controller;

import com.example.pts.model.Lobby;
import com.example.pts.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @PostMapping("/create")
    public ResponseEntity<Lobby> createLobby(@RequestBody Lobby lobby, Principal principal) {
        lobby.setCreatorId(principal.getName());
        return ResponseEntity.ok(lobbyService.createLobby(lobby));
    }

    @GetMapping("/list")
    public List<Lobby> listLobbies() {
        return lobbyService.getAllLobbies();
    }

    @GetMapping("/{lobbyId}")
    public ResponseEntity<Lobby> getLobby(@PathVariable String lobbyId) {
        return lobbyService.getLobby(lobbyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(
            @PathVariable String lobbyId, 
            @RequestParam(required = false) String password, 
            Principal principal) {
        
        String username = principal.getName();
        String result = lobbyService.joinLobby(lobbyId, password, username);
        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Lobiye giriş yapıldı");
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{lobbyId}/chat")
    public ResponseEntity<Void> sendChat(
            @PathVariable String lobbyId,
            @RequestParam String message,
            Principal principal) {
        String sender = principal.getName();
        lobbyService.sendChatMessage(lobbyId, sender, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leave/{lobbyId}")
    public ResponseEntity<String> leaveLobby(@PathVariable String lobbyId, Principal principal) {
        String username = principal.getName();
        lobbyService.leaveLobby(lobbyId, username);
        return ResponseEntity.ok("Lobiden ayrılındı");
    }

    @PostMapping("/{lobbyId}/ready")
    public ResponseEntity<String> setPlayerReady(
            @PathVariable String lobbyId,
            @RequestParam boolean isReady,
            Principal principal) {
        String username = principal.getName();
        lobbyService.setPlayerReady(lobbyId, username, isReady);
        return ResponseEntity.ok(isReady ? "Hazır duruma geçtiniz" : "Hazır olmayan duruma geçtiniz");
    }

    @PostMapping("/{lobbyId}/start")
    public ResponseEntity<String> startGame(@PathVariable String lobbyId, Principal principal) {
        Optional<Lobby> lobbyOpt = lobbyService.getLobby(lobbyId);
        
        if (lobbyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Lobi bulunamadı");
        }

        Lobby lobby = lobbyOpt.get();
        
        // Sadece lobi kurucusu oyunu başlatabilir
        if (!lobby.getCreatorId().equals(principal.getName())) {
            return ResponseEntity.badRequest().body("Sadece lobi kurucusu oyunu başlatabilir");
        }

        if (lobbyService.startGame(lobbyId)) {
            return ResponseEntity.ok("Oyun başlatıldı");
        } else {
            return ResponseEntity.badRequest().body("Tüm oyuncuların hazır olması gerekir");
        }
    }
}
