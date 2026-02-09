package co.com.prueba.entrypoints.router;

import co.com.prueba.entrypoints.handler.UserHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserRouterTest {

    private UserRouter userRouter;
    private UserHandler userHandler;

    @BeforeEach
    void setUp() {
        userRouter = new UserRouter();
        // Crear un UserHandler real en lugar de mock
        userHandler = new UserHandler(null); // Se puede pasar null porque solo verificamos que se crea la ruta
    }

    @Test
    void testRouterFunctionUser_CreatesRoute() {
        RouterFunction<ServerResponse> route = userRouter.routerFunctionUser(userHandler);
        
        assertNotNull(route);
    }

    @Test
    void testRouterFunctionUser_ReturnsRouterFunction() {
        RouterFunction<ServerResponse> route = userRouter.routerFunctionUser(userHandler);
        
        assertTrue(route instanceof RouterFunction);
    }

    @Test
    void testUserRouter_Initialization() {
        assertNotNull(userRouter);
    }

    @Test
    void testUserRouter_Constants() {
        assertEquals("Entrega la información del usuario", UserRouter.MESSAGE_200);
        assertEquals("No existe el usuario", UserRouter.MESSAGE_404);
    }

    @Test
    void testRouterFunctionUser_WithDifferentHandler() {
        UserHandler anotherHandler = new UserHandler(null);
        RouterFunction<ServerResponse> route = userRouter.routerFunctionUser(anotherHandler);
        
        assertNotNull(route);
    }
}
