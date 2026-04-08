package io.ylab.wordflow.service;

import io.ylab.wordflow.entity.AuditLog;
import io.ylab.wordflow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String parameters, String analysisId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AuditLog log = AuditLog.builder()
                .username(username)
                .timestamp(LocalDateTime.now())
                .action(action)
                .parameters(parameters)
                .analysisId(analysisId != null ? UUID.fromString(analysisId) : null)
                .build();
        auditLogRepository.save(log);
    }
}
