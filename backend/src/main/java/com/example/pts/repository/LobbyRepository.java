package com.example.pts.repository;

import com.example.pts.model.Lobby;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LobbyRepository extends MongoRepository<Lobby, String> {
}
