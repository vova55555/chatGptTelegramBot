package org.hoshta.repo;

import org.hoshta.model.UserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Component
public interface UserSessionRepository extends CrudRepository<UserSession, Long> {
    Optional<UserSession> findByChatId(Long chatId);
    @Transactional
    void deleteByChatId(Long chatId);
}
