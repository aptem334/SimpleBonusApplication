package org.simplebonus.exception.handler;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("error")
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private final String errorCode;
    private final String errorDescription;
}
