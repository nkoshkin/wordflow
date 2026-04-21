package io.ylab.wordflow.core.validator;

/**
 * Общий интерфейс для валидации объектов.
 * Предоставляет метод проверки корректности значения.
 *
 * @param <T> тип объекта для валидации (например, {@link String} для пути к файлу или директории)
 */
public interface IValidator<T> {

    /**
     * Проверяет, является ли переданное значение валидным.
     *
     * @param value значение для проверки
     * @throws IllegalArgumentException если значение не соответствует критериям валидности
     */
    void validate(T value);

    /**
     * Проверяет, является ли значение валидным, не выбрасывая исключение.
     *
     * @param value значение для проверки
     * @return {@code true}, если значение валидно; {@code false} в противном случае
     */
    default Boolean isValid(T value){
        try{
            validate(value);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
