package io.ylab.wordflow.core.validator;

public interface IValidator<T> {

    void validate(T value);

    default Boolean isValid(T value){
        try{
            validate(value);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
