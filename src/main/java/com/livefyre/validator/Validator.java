package com.livefyre.validator;

public interface Validator<T> {
    public String validate(T data);
}
