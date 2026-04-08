package io.ylab.wordflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_error")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "analysis_id")
    private AnalysisEntity analysis;

    private String file;
    private String message;
}