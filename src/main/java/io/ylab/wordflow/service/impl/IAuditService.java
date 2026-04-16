package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.entity.AuditLog;
import io.ylab.wordflow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IAuditService implements io.ylab.wordflow.service.IAuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAnalysis(String action, String parameters, UUID analysisId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AuditLog log = AuditLog.builder()
                .username(username)
                .timestamp(LocalDateTime.now())
                .action(action)
                .parameters(parameters)
                .analysisId(analysisId)
                .build();
        auditLogRepository.save(log);
    }
}
