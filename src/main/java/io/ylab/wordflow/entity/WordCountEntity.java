package io.ylab.wordflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "word_count")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "analysis_entity_id")
    private AnalysisEntity analysis;

    private String word;
    private Integer count;

}