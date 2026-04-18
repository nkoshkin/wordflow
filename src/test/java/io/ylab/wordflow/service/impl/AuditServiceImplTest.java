package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.entity.AuditLog;
import io.ylab.wordflow.repository.AuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link AuditServiceImpl}.
 * Проверяют сохранение записей аудита в репозиторий и корректное извлечение имени пользователя
 * из {@link SecurityContextHolder}.
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuditServiceImpl auditService;

    private static final String TEST_USER = "testUser";

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USER);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Проверяет, что {@link AuditServiceImpl#log(String, String, UUID)} сохраняет запись в репозиторий
     * с корректными полями: имя пользователя, действие, параметры, идентификатор анализа и временная метка.
     */
    @Test
    void log_shouldSaveAuditLogWithCorrectFields() {
        String action = "START_ANALYSIS";
        String parameters = "{\"dir\":\"./texts\"}";
        UUID analysisId = UUID.randomUUID();

        auditService.log(action, parameters, analysisId);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        Mockito.verify(auditLogRepository, times(1)).save(captor.capture());
        AuditLog savedLog = captor.getValue();

        assertThat(savedLog.getUsername()).isEqualTo(TEST_USER);
        assertThat(savedLog.getAction()).isEqualTo(action);
        assertThat(savedLog.getParameters()).isEqualTo(parameters);
        assertThat(savedLog.getAnalysisId()).isEqualTo(analysisId);
        assertThat(savedLog.getTimestamp()).isNotNull();
    }

    /**
     * Проверяет, что имя пользователя корректно извлекается из {@link SecurityContextHolder}
     * при каждом вызове {@link AuditServiceImpl#log(String, String, UUID)}.
     */
    @Test
    void log_shouldRetrieveUsernameFromSecurityContext() {
        String action = "GET_RESULT";
        String parameters = "id=123";
        UUID analysisId = UUID.randomUUID();

        auditService.log(action, parameters, analysisId);

        verify(securityContext, atLeastOnce()).getAuthentication();
        verify(authentication, atLeastOnce()).getName();

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo(TEST_USER);
    }

    /**
     * Проверяет, что параметр {@code analysisId} может быть {@code null}
     * и при этом запись аудита сохраняется корректно.
     */
    @Test
    void log_whenAnalysisIdIsNull_shouldSaveWithoutAnalysisId() {
        String action = "LIST_RESULTS";
        String parameters = "";
        UUID analysisId = null;

        auditService.log(action, parameters, analysisId);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());
        AuditLog savedLog = captor.getValue();

        assertThat(savedLog.getAnalysisId()).isNull();
        assertThat(savedLog.getAction()).isEqualTo(action);
        assertThat(savedLog.getParameters()).isEqualTo(parameters);
    }

    /**
     * Проверяет, что временная метка (timestamp) устанавливается автоматически
     * при сохранении записи аудита.
     */
    @Test
    void log_shouldSetCurrentTimestamp() {
        String action = "START_ANALYSIS";
        String parameters = "{}";
        UUID analysisId = UUID.randomUUID();

        auditService.log(action, parameters, analysisId);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog savedLog = captor.getValue();

        assertThat(savedLog.getTimestamp()).isNotNull();
        assertThat(savedLog.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(savedLog.getTimestamp()).isAfter(LocalDateTime.now().minusSeconds(1));
    }
}