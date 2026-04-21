package io.ylab.wordflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность, хранящая слово и его частоту для конкретного анализа.
 *
 * <p>Связь: {@code analysis} – родительский анализ (ManyToOne).
 *
 * <p>Поля:
 * <ul>
 *   <li>{@code id} – первичный ключ (автоинкремент)</li>
 *   <li>{@code analysis} – ссылка на анализ (внешний ключ analysis_id)</li>
 *   <li>{@code word} – слово</li>
 *   <li>{@code count} – частота встречаемости</li>
 * </ul>
 */
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