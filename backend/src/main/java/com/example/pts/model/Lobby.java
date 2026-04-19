package com.example.pts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lobbies")
public class Lobby {
    @Id
    private String id;
    private String name;
    private String password;
    private String creatorId;
    private List<String> memberUsernames = new ArrayList<>();
    private boolean isStarted = false;
    private int maxPlayers = 4;
    private List<String> chatHistory = new ArrayList<>();
}
