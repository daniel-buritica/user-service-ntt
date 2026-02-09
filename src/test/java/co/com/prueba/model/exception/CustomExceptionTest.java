package co.com.prueba.model.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTest {

    @Test
    void testCustomException_Constructor() {
        String message = "Test error message";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomException exception = new CustomException(status, message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomException_GetStatus() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomException exception = new CustomException(status, "Not found");

        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomException_GetMessage() {
        String message = "Custom error message";
        CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCustomException_DifferentStatuses() {
        CustomException badRequest = new CustomException(HttpStatus.BAD_REQUEST, "Bad request");
        CustomException notFound = new CustomException(HttpStatus.NOT_FOUND, "Not found");
        CustomException internalError = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");

        assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internalError.getStatus());
    }

    @Test
    void testCustomException_IsInstanceOfException() {
        CustomException exception = new CustomException(HttpStatus.BAD_REQUEST, "Error");
        
        assertTrue(exception instanceof Exception);
    }

    @Test
    void testCustomException_WithEmptyMessage() {
        String emptyMessage = "";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        CustomException exception = new CustomException(status, emptyMessage);
        
        assertEquals(emptyMessage, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomException_WithNullMessage() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        CustomException exception = new CustomException(status, null);
        
        assertNull(exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomException_AllHttpStatusCodes() {
        CustomException ok = new CustomException(HttpStatus.OK, "OK");
        CustomException created = new CustomException(HttpStatus.CREATED, "Created");
        CustomException noContent = new CustomException(HttpStatus.NO_CONTENT, "No content");
        CustomException unauthorized = new CustomException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        CustomException forbidden = new CustomException(HttpStatus.FORBIDDEN, "Forbidden");
        CustomException conflict = new CustomException(HttpStatus.CONFLICT, "Conflict");
        CustomException unprocessable = new CustomException(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable");
        CustomException tooManyRequests = new CustomException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");

        assertEquals(HttpStatus.OK, ok.getStatus());
        assertEquals(HttpStatus.CREATED, created.getStatus());
        assertEquals(HttpStatus.NO_CONTENT, noContent.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatus());
        assertEquals(HttpStatus.CONFLICT, conflict.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, unprocessable.getStatus());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, tooManyRequests.getStatus());
    }

    @Test
    void testCustomException_StatusIsNotModifiedAfterCreation() {
        HttpStatus initialStatus = HttpStatus.BAD_REQUEST;
        CustomException exception = new CustomException(initialStatus, "Error");
        
        // Verificar que el status no cambia
        assertEquals(initialStatus, exception.getStatus());
        
        // Crear otra excepción con diferente status
        CustomException anotherException = new CustomException(HttpStatus.NOT_FOUND, "Not found");
        
        // Verificar que cada excepción mantiene su propio status
        assertEquals(initialStatus, exception.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, anotherException.getStatus());
    }

    @Test
    void testCustomException_MessageIsNotModifiedAfterCreation() {
        String initialMessage = "Initial message";
        CustomException exception = new CustomException(HttpStatus.BAD_REQUEST, initialMessage);
        
        assertEquals(initialMessage, exception.getMessage());
        
        // Crear otra excepción con diferente mensaje
        CustomException anotherException = new CustomException(HttpStatus.NOT_FOUND, "Different message");
        
        // Verificar que cada excepción mantiene su propio mensaje
        assertEquals(initialMessage, exception.getMessage());
        assertEquals("Different message", anotherException.getMessage());
    }
}
