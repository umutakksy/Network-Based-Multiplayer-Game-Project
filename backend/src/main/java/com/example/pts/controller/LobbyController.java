package com.example.pts.controller;

import com.example.pts.model.Lobby;
import com.example.pts.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @PostMapping("/create")
    public ResponseEntity<Lobby> createLobby(@RequestBody Lobby lobby) {
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
            @RequestParam String username) {
        
        String result = lobbyService.joinLobby(lobbyId, password, username);
        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Lobiye giriş yapıldı");
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{lobbyId}/chat")
    public ResponseEntity<Void> sendChat(
            @PathVariable String lobbyId,
            @RequestParam String sender,
            @RequestParam String message) {
        lobbyService.sendChatMessage(lobbyId, sender, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leave/{lobbyId}")
    public ResponseEntity<String> leaveLobby(@PathVariable String lobbyId, @RequestParam String username) {
        lobbyService.leaveLobby(lobbyId, username);
        return ResponseEntity.ok("Lobiden ayrılındı");
    }
}
