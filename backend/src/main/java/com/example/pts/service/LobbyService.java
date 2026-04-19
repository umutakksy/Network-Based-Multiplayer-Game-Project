package com.example.pts.service;

import com.example.pts.model.Lobby;
import com.example.pts.repository.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LobbyService {

    @Autowired
    private LobbyRepository lobbyRepository;

    public Optional<Lobby> getLobby(String id) {
        return lobbyRepository.findById(id);
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Lobby createLobby(Lobby lobby) {
        lobby.setStarted(false);
        if (lobby.getMemberUsernames() == null) {
            lobby.setMemberUsernames(new java.util.ArrayList<>());
        }
        if (lobby.getCreatorId() != null && !lobby.getMemberUsernames().contains(lobby.getCreatorId())) {
            lobby.getMemberUsernames().add(lobby.getCreatorId());
        }
        Lobby savedLobby = lobbyRepository.save(lobby);
        broadcastLobbyList();
        return savedLobby;
    }

    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    public String joinLobby(String lobbyId, String password, String username) {
        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (optionalLobby.isEmpty()) return "Lobi bulunamadı!";

        Lobby lobby = optionalLobby.get();
        if (lobby.getPassword() != null && !lobby.getPassword().isEmpty() && !lobby.getPassword().equals(password)) {
            return "Hatalı şifre!";
        }

        if (lobby.getMemberUsernames().size() >= lobby.getMaxPlayers()) {
            return "Lobi dolu!";
        }

        if (!lobby.getMemberUsernames().contains(username)) {
            lobby.getMemberUsernames().add(username);
            lobbyRepository.save(lobby);
            notifyLobbyChange(lobbyId);
            broadcastLobbyList();
        }

        return "SUCCESS";
    }

    public void leaveLobby(String lobbyId, String username) {
        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (optionalLobby.isPresent()) {
            Lobby lobby = optionalLobby.get();
            
            // Eğer çıkan kişi kurucu ise lobi kapansın
            if (lobby.getCreatorId() != null && lobby.getCreatorId().equalsIgnoreCase(username)) {
                System.out.println("Kurucu (" + username + ") lobiden ayrıldı, lobi kapatılıyor: " + lobbyId);
                lobbyRepository.delete(lobby);
            } else {
                lobby.getMemberUsernames().remove(username);
                if (lobby.getMemberUsernames().isEmpty()) {
                    lobbyRepository.delete(lobby);
                } else {
                    lobbyRepository.save(lobby);
                    notifyLobbyChange(lobbyId);
                }
            }
            broadcastLobbyList();
        }
    }

    private void notifyLobbyChange(String lobbyId) {
        Optional<Lobby> lobby = lobbyRepository.findById(lobbyId);
        lobby.ifPresent(l -> messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, l));
    }

    public void sendChatMessage(String lobbyId, String sender, String message) {
        String payload = "CHAT;" + sender + ";" + message;
        Optional<Lobby> lobbyOpt = lobbyRepository.findById(lobbyId);
        if (lobbyOpt.isPresent()) {
            Lobby lobby = lobbyOpt.get();
            lobby.getChatHistory().add(payload);
            lobbyRepository.save(lobby);
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/chat", payload);
        }
    }

    private void broadcastLobbyList() {
        messagingTemplate.convertAndSend("/topic/lobbies", lobbyRepository.findAll());
    }
}
