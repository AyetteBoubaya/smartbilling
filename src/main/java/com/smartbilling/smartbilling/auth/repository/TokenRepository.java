package com.smartbilling.smartbilling.auth.repository;

import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValueAndType(String value, TokenType type);

    // Invalide tous les tokens d'un type donné pour un user (ex: après reset)
    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.used = true WHERE t.user = :user AND t.type = :type AND t.used = false")
    void invalidateAllByUserAndType(User user, TokenType type);

    // Supprime les tokens expirés (pour un job de nettoyage)
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.user = :user AND t.type = :type")
    void deleteAllByUserAndType(User user, TokenType type);
}