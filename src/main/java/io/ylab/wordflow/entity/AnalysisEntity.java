package io.ylab.wordflow.entity;

import io.ylab.wordflow.enums.AnalysisStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
