package com.example.pts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
    
    private int score;
}
