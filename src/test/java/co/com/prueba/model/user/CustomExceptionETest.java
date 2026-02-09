package co.com.prueba.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionETest {

    @Test
    void testCustomExceptionE_NoArgsConstructor() {
        CustomExceptionE exception = new CustomExceptionE();
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getStatus());
    }

    @Test
    void testCustomExceptionE_AllArgsConstructor() {
        String message = "Error message";
        String status = "400";
        
        CustomExceptionE exception = new CustomExceptionE(message, status);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomExceptionE_GetterMessage() {
        String message = "Test message";
        CustomExceptionE exception = new CustomExceptionE(message, "500");
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCustomExceptionE_GetterStatus() {
        String status = "404";
        CustomExceptionE exception = new CustomExceptionE("Not found", status);
        
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomExceptionE_SetMessage() {
        CustomExceptionE exception = new CustomExceptionE();
        String message = "New message";
        
        exception.setMessage(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCustomExceptionE_SetStatus() {
        CustomExceptionE exception = new CustomExceptionE();
        String status = "401";
        
        exception.setStatus(status);
        
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomExceptionE_Builder() {
        String message = "Builder message";
        String status = "403";
        
        CustomExceptionE exception = CustomExceptionE.builder()
                .message(message)
                .status(status)
                .build();
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testCustomExceptionE_Builder_WithNullValues() {
        CustomExceptionE exception = CustomExceptionE.builder()
                .message(null)
                .status(null)
                .build();
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getStatus());
    }

    @Test
    void testCustomExceptionE_Builder_WithEmptyStrings() {
        CustomExceptionE exception = CustomExceptionE.builder()
                .message("")
                .status("")
                .build();
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertEquals("", exception.getStatus());
    }

    @Test
    void testCustomExceptionE_Builder_PartialFields() {
        CustomExceptionE exception = CustomExceptionE.builder()
                .message("Only message")
                .build();
        
        assertNotNull(exception);
        assertEquals("Only message", exception.getMessage());
        assertNull(exception.getStatus());
    }

    @Test
    void testCustomExceptionE_Setters_Chain() {
        CustomExceptionE exception = new CustomExceptionE();
        
        exception.setMessage("First message");
        exception.setStatus("200");
        exception.setMessage("Updated message");
        exception.setStatus("201");
        
        assertEquals("Updated message", exception.getMessage());
        assertEquals("201", exception.getStatus());
    }

    @Test
    void testCustomExceptionE_AllMethods() {
        // Test constructor sin argumentos
        CustomExceptionE exception1 = new CustomExceptionE();
        assertNotNull(exception1);
        
        // Test constructor con argumentos
        CustomExceptionE exception2 = new CustomExceptionE("Error", "500");
        assertEquals("Error", exception2.getMessage());
        assertEquals("500", exception2.getStatus());
        
        // Test builder
        CustomExceptionE exception3 = CustomExceptionE.builder()
                .message("Builder error")
                .status("400")
                .build();
        assertEquals("Builder error", exception3.getMessage());
        assertEquals("400", exception3.getStatus());
        
        // Test setters
        exception1.setMessage("Setter message");
        exception1.setStatus("404");
        assertEquals("Setter message", exception1.getMessage());
        assertEquals("404", exception1.getStatus());
    }
}
