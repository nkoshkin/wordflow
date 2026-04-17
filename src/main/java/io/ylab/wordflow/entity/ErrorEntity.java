package io.ylab.wordflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность, хранящая ошибку, возникшую при обработке конкретного файла.
 *
 * <p>Связь: {@code analysis} – родительский анализ (ManyToOne).
 *
 * <p>Поля:
 * <ul>
 *   <li>{@code id} – первичный ключ</li>
 *   <li>{@code analysis} – ссылка на анализ (внешний ключ analysis_id)</li>
 *   <li>{@code file} – имя файла, в котором произошла ошибка</li>
 *   <li>{@code message} – текст ошибки</li>
 * </ul>
 */
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