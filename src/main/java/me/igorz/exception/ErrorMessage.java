package me.igorz.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorMessage {

    private String error;
    private List<String> errors;

    public ErrorMessage(List<String> errors) {
        this.errors = errors;
    }

    public ErrorMessage(String error) {
        this.error = error;
    }
}
