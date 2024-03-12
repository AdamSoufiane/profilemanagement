package com.example.profilemanagement.adapters.primary.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class ProfileControllerException extends RuntimeException {

    private static final long serialVersionUID = 7526472295622776147L;
    private int errorCode;
    private List<String> errors;

    public ProfileControllerException(String message) {
        super(message);
    }

    public ProfileControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileControllerException(Throwable cause) {
        super(cause);
    }

    public ProfileControllerException(String message, Throwable cause, int errorCode, List<String> errors) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errors = errors;
    }
}
