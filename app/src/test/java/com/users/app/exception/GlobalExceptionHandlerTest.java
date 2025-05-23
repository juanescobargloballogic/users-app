package com.users.app.exception;

import com.users.app.dto.ApiErrorResponse;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException_shouldReturnBadRequest() {
        RuntimeException ex = new RuntimeException("Generic runtime error");

        ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().get(0).getDetail()).isEqualTo("Generic runtime error");
    }

    @Test
    void testHandleUserAlreadyExists_shouldReturnConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");

        ResponseEntity<ApiErrorResponse> response = handler.handleUserAlreadyExists(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().get(0).getDetail()).isEqualTo("User already exists");
    }

    @Test
    void testHandleNotFoundException_shouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Resource not found");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError().get(0).getDetail()).isEqualTo("Resource not found");
    }

    @Test
    void testHandleUnauthorizedException_shouldReturnUnauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Not authorized");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnauthorizedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError().get(0).getDetail()).isEqualTo("Not authorized");
    }

    @Test
    void testHandleException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected server error");

        ResponseEntity<ApiErrorResponse> response = handler.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().get(0).getDetail()).isEqualTo("Unexpected server error");
    }
}
