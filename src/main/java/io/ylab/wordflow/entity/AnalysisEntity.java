package io.ylab.wordflow.entity;

import io.ylab.wordflow.enums.AnalysisStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность, представляющая анализ текстов.
 * Хранит параметры запроса, статус выполнения, временные метки,
 * количество обработанных файлов, а также связанные коллекции слов и ошибок.
 *
 * <p>Связи:
 * <ul>
 *   <li>{@code words} – список слов с частотами (OneToMany, каскадное удаление)</li>
 *   <li>{@code errors} – список ошибок по файлам (OneToMany, каскадное удаление)</li>
 * </ul>
 *
 * <p>Поля:
 * <ul>
 *   <li>{@code id} – UUID, первичный ключ (генерируется автоматически)</li>
 *   <li>{@code directory} – путь к директории с текстовыми файлами</li>
 *   <li>{@code minLength} – минимальная длина слова</li>
 *   <li>{@code top} – количество топ слов</li>
 *   <li>{@code mode} – режим обработки (single/multi)</li>
 *   <li>{@code threads} – количество потоков</li>
 *   <li>{@code stopWordsFile} – путь к файлу стоп-слов (может быть null)</li>
 *   <li>{@code outputFile} – путь для JSON-вывода (может быть null)</li>
 *   <li>{@code status} – статус выполнения (PENDING, RUNNING, COMPLETED, FAILED)</li>
 *   <li>{@code startTime} – время начала анализа</li>
 *   <li>{@code endTime} – время окончания анализа</li>
 *   <li>{@code executionTimeMs} – длительность в миллисекундах</li>
 *   <li>{@code processedFiles} – количество обработанных файлов</li>
 * </ul>
 */
@Entity
@Table(name="analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class AnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ToString.Include
    private UUID id;

    private String directory;
    private Integer minLength;
    private Integer top;
    private String mode;
    private Integer threads;
    private String stopWordsFile;
    private String outputFile;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long executionTimeMs;
    private Integer processedFiles;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<WordCountEntity> words = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ErrorEntity> errors = new ArrayList<>();

}
