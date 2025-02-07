# **Alba Framework**


**Alba** es un framework minimalista diseñado para crear servidores web en Java. Ofrece una API sencilla y flexible para gestionar rutas, middlewares y respuestas HTTP. Este framework está inspirado en herramientas populares como [Hono.js](https://hono.dev/) y [Express.js](https://expressjs.com/), pero con un enfoque ligero y minimalista, ideal para aplicaciones pequeñas o para aprender cómo funcionan los servidores web desde cero.


## **Características Principales**

- **Rutas Dinámicas**: Soporte para parámetros dinámicos en las rutas (por ejemplo, `/user/:id`).
- **Parámetros de Consulta**: Manejo de parámetros de consulta (`query string`) en las solicitudes HTTP.
- **Validación de Datos**: Middleware integrado para validar datos de entrada usando anotaciones de validación.
- **Middlewares Globales**: Soporte para middlewares globales y específicos por ruta.
- **Obtención de IP del Cliente**: Acceso a la dirección IP del cliente para autenticación o registro.

---

## **Nuevas Funcionalidades**

### **1. Parámetros Dinámicos (`Path Variables`)**

El framework ahora soporta parámetros dinámicos en las rutas. Por ejemplo:

```java
server.get("/user/:id", request -> {
    String userId = request.getPathParam("id"); // Obtiene el valor del parámetro ":id"
    return new Response(200, new JSONObject()
            .put("message", "Usuario encontrado")
            .put("userId", userId));
});
```

También puedes convertir los parámetros dinámicos a tipos específicos:

```java
Long userId = request.getPathParamAsLong("id");
Integer userIdAsInt = request.getPathParamAsInt("id");
Double userIdAsDouble = request.getPathParamAsDouble("id");
```

---

### **2. Parámetros de Consulta (`Query Params`)**

El framework ahora soporta parámetros de consulta (`query string`) en las solicitudes HTTP. Por ejemplo:

```java
server.get("/user/:id", request -> {
    String name = request.getQueryParam("name"); // Obtiene el valor del parámetro "name"
    Long age = request.getQueryParamAsLong("age"); // Convierte el parámetro "age" a Long
    return new Response(200, new JSONObject()
            .put("message", "Usuario encontrado")
            .put("name", name)
            .put("age", age));
});
```

Ejemplo de solicitud:
```
GET /user/19?name=angel&age=25
```

Respuesta:
```json
{
    "message": "Usuario encontrado",
    "name": "angel",
    "age": 25
}
```

---

### **3. Obtención de la IP del Cliente**

Puedes acceder a la dirección IP del cliente desde cualquier controlador:

```java
server.use((request, response, chain) -> {
    System.out.println("Solicitud recibida desde IP: " + request.getClientIp());
    return chain.next(request, response);
});
```

---

### **4. Validación de Datos**

El middleware `ValidationMiddleware` permite validar el cuerpo de las solicitudes automáticamente. Por ejemplo:

```java
server.post("/user", request -> {
    JSONObject body = request.getBody();
    return new Response(200, new JSONObject()
            .put("message", "Usuario creado")
            .put("data", body));
}, new ValidationMiddleware<>(User.class));
```

Clase `User` con validaciones:

```java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

class User {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

---

## **Cómo Usar el Framework**

1. **Ejemplo Básico**:
   Crea un servidor simple con rutas dinámicas y parámetros de consulta:

   ```java
   public class Main {
       public static void main(String[] args) throws IOException {
           Server server = new Server(8080);

           server.get("/user/:id", request -> {
               String userId = request.getPathParam("id");
               String name = request.getQueryParam("name");

               return new Response(200, new JSONObject()
                       .put("message", "Usuario encontrado")
                       .put("userId", userId)
                       .put("name", name));
           });

           server.start();
       }
   }
   ```

---

## **Contribuciones**

Si deseas contribuir al proyecto, sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y envía un pull request.

---

## **Licencia**

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---
