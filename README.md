# **Alba Framework**


**Alba** es un framework minimalista diseñado para crear servidores web en Java. Ofrece una API sencilla y flexible para gestionar rutas, middlewares y respuestas HTTP. Este framework está inspirado en herramientas populares como [Hono.js](https://hono.dev/) y [Express.js](https://expressjs.com/), pero con un enfoque ligero y minimalista, ideal para aplicaciones pequeñas o para aprender cómo funcionan los servidores web desde cero.


#### **Nuevas Funcionalidades**

##### **A. Middleware CORS (`CorsMiddleware`)**
El middleware CORS permite configurar políticas de acceso entre dominios (Cross-Origin Resource Sharing).

- **Configuración Básica**:
  ```java
  CorsMiddleware corsMiddleware = new CorsMiddleware();
  corsMiddleware.addAllowedOrigins("https://example.com")
                .addAllowedMethods("GET", "POST")
                .setAllowCredentials(true)
                .setMaxAge(3600);
  server.use(corsMiddleware);
  ```

- **Características**:
  - Permite todos los orígenes (`"*"`) o solo orígenes específicos.
  - Restringe los métodos HTTP permitidos.
  - Soporta credenciales (cookies, autenticación).
  - Configura el tiempo de caché para solicitudes preflight.

---

##### **B. Middleware de Restricción de IPs (`IpRestrictionMiddleware`)**
Este middleware restringe el acceso a ciertas rutas o al servidor completo basándose en las direcciones IP de los clientes.

- **Configuración Básica**:
  ```java
  IpRestrictionMiddleware ipRestrictionMiddleware = new IpRestrictionMiddleware()
          .addAllowedIps(Arrays.asList("192.168.1.10", "192.168.1.20"))
          .addBlockedIps(Collections.singletonList("192.168.1.15"))
          .setAllowAllByDefault(false); // Bloquea todas las IPs por defecto
  server.use(ipRestrictionMiddleware);
  ```

- **Características**:
  - Lista blanca de IPs permitidas.
  - Lista negra de IPs bloqueadas.
  - Soporte para rangos CIDR (por ejemplo, `192.168.1.0/24`).
  - Comportamiento configurable: permitir o bloquear todas las IPs por defecto.

---

##### **C. Middleware de Idioma (`LanguageMiddleware`)**
Este middleware detecta el idioma preferido del cliente (basado en el encabezado `Accept-Language`) y configura el idioma de la aplicación en función de eso.

- **Configuración Básica**:
  ```java
  LanguageMiddleware languageMiddleware = new LanguageMiddleware()
          .addSupportedLanguages(Set.of("es", "en", "fr"))
          .setDefaultLanguage("en")
          .setLanguageHeader("Accept-Language");
  server.use(languageMiddleware);
  ```

- **Características**:
  - Detecta automáticamente el idioma preferido del cliente.
  - Soporta múltiples idiomas.
  - Configura un idioma predeterminado si el idioma preferido no está soportado.
  - Permite personalizar el nombre del encabezado de idioma.

---

##### **D. Tipado Automático del Cuerpo de la Solicitud (`getBodyAs`)**
La clase `Request` ahora incluye un método `getBodyAs` que permite convertir automáticamente el cuerpo JSON de una solicitud en un objeto Java tipado.

- **Uso**:
  ```java
  public Response createUser(Request request) {
      CreateUserRequest body = request.getBodyAs(CreateUserRequest.class);
      return new Response(200, new JSONObject().put("message", "Usuario creado"));
  }
  ```

- **Características**:
  - Usa Jackson para deserializar el cuerpo JSON.
  - Maneja errores automáticamente si el cuerpo no coincide con la estructura esperada.
  - Compatible con cualquier clase Java.

---

##### **E. Agrupación de Rutas (`route`)**
Ahora puedes agrupar rutas relacionadas bajo un prefijo común para reducir la repetición de código.

- **Configuración Básica**:
  ```java
  server.route("/api/v1/posts", new PostController());
  ```

- **Características**:
  - Asocia un controlador completo con un grupo de rutas.
  - Reduce la cantidad de código necesario para registrar múltiples rutas relacionadas.
  - Compatible con cualquier controlador que implemente la interfaz `RouteGroup`.

---

#### **3. Ejemplo Completo**

Aquí tienes un ejemplo completo que utiliza todas las nuevas funcionalidades:

```java
import server.Server;
import middleware.CorsMiddleware;
import middleware.IpRestrictionMiddleware;
import middleware.LanguageMiddleware;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);

        // Middleware CORS
        CorsMiddleware corsMiddleware = new CorsMiddleware();
        corsMiddleware.addAllowedOrigins("https://example.com")
                      .addAllowedMethods("GET", "POST")
                      .setAllowCredentials(true)
                      .setMaxAge(3600);
        server.use(corsMiddleware);

        // Middleware de Restricción de IPs
        IpRestrictionMiddleware ipRestrictionMiddleware = new IpRestrictionMiddleware()
                .addAllowedIps(Arrays.asList("192.168.1.10", "192.168.1.20"))
                .setAllowAllByDefault(false);
        server.use(ipRestrictionMiddleware);

        // Middleware de Idioma
        LanguageMiddleware languageMiddleware = new LanguageMiddleware()
                .addSupportedLanguages(Set.of("es", "en", "fr"))
                .setDefaultLanguage("en");
        server.use(languageMiddleware);

        // Rutas agrupadas
        server.route("/api/v1/posts", new PostController());

        // Ruta simple
        server.get("/", request -> {
            String language = (String) request.getAttribute("language");
            Map<String, String> messages = Map.of(
                    "es", "Bienvenido",
                    "en", "Welcome",
                    "fr", "Bienvenue"
            );
            String message = messages.getOrDefault(language, "Welcome");
            return new Response(200, new JSONObject().put("message", message));
        });

        // Iniciar el servidor
        server.start();
    }
}
```

---

#### **4. Clase `Request`**

La clase `Request` ha sido actualizada para incluir nuevas funcionalidades:

- **Método `getBodyAs`**:
  - Convierte el cuerpo JSON en un objeto Java tipado.
  - Ejemplo:
    ```java
    CreateUserRequest body = request.getBodyAs(CreateUserRequest.class);
    ```

- **Método `getAttribute`**:
  - Obtiene atributos configurados por middleware (por ejemplo, el idioma seleccionado).

- **Métodos para Parámetros de Ruta y Consulta**:
  - `getPathParam`, `getQueryParam`, etc., facilitan el acceso a parámetros dinámicos y de consulta.

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

## **Rutas**

### **GET**
Define rutas para obtener información.

```java
  server.get("/hey", request -> {
            Response response = new Response(200, new JSONObject().put("message", "Hola Mundo desde JSON"));

            response.addHeader("Content-Type", "application/json");
            return response;
});
```

### **POST**
Define rutas para recibir datos.

```java
 server.post("/port", request ->  new Response(200, new JSONObject().put("message", "Okey con POST")));
```

### **PUT**
Define rutas para actualizar datos.

```java
 server.put("/put", request ->  new Response(200, new JSONObject().put("message", "Okey con put")));
```

### **DELETE**
Define rutas para eliminar datos.

```java
server.delete("/delete", request ->  new Response(200, new JSONObject().put("message", "Okey con DELETE")));
```

---

## **Middlewares**

Los middlewares permiten ejecutar lógica antes de que se procese una solicitud. Pueden ser globales (aplicados a todas las rutas) o específicos para una ruta.

### Middleware global

```java
server.use((request, response, chain) -> {
    System.out.println("Middleware global ejecutado");
    chain.next(request, response); // Pasar al siguiente middleware o al controlador de ruta
});
```

### Middleware específico para una ruta

```java
server.get("/ruta", request -> {
    return new Response(200, new JSONObject().put("message", "Middleware específico ejecutado"));
}).use((request, response, chain) -> {
    System.out.println("Middleware específico ejecutado");
    chain.next(request, response);
});
```

---

### **Middlewares disponibles**

#### **1. CORS Middleware**
Permite configurar políticas de Cross-Origin Resource Sharing (CORS).

##### Ejemplo de uso

###### Configuración básica
```java
// Crear middleware CORS con valores predeterminados
CorsMiddleware corsMiddleware = new CorsMiddleware();
// Agregar middleware global
server.use(corsMiddleware);
```

###### Configuración personalizada
```java
// Crear middleware CORS con configuración personalizada
List<String> allowedOrigins = Arrays.asList("https://example.com", "https://api.example.com");
List<String> allowedMethods = Arrays.asList("GET", "POST");
List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization");

CorsMiddleware corsMiddleware = new CorsMiddleware(allowedOrigins, allowedMethods, allowedHeaders);
// Agregar middleware global
server.use(corsMiddleware);
```

---

#### **2. Logger Middleware**
Registra cada solicitud entrante con un timestamp y detalles adicionales si el nivel de log es `DEBUG`.

##### Ejemplo de uso
```java
// Agregar LoggerMiddleware como middleware global
server.use(new LoggerMiddleware());
```

---

#### **3. CSRF Protection Middleware**
Protege tu aplicación contra ataques de Cross-Site Request Forgery (CSRF) generando un token único por sesión.

##### Ejemplo de uso
```java
// Agregar CSRF Protection Middleware
server.use(new CsrfProtectionMiddleware());

// Definir una ruta POST protegida por CSRF
server.post("/submit", request -> {
    return new Response(200, new JSONObject().put("message", "Datos recibidos"));
});
```

---

#### **4. Timeout Middleware**
Limita el tiempo máximo que una solicitud puede tardar en procesarse.

##### Ejemplo de uso
```java
// Agregar Timeout Middleware con un límite de 5 segundos
server.use(new TimeoutMiddleware(5000));

// Definir una ruta que simule un procesamiento largo
server.get("/slow", request -> {
    try {
        Thread.sleep(6000); // Simular un retraso de 6 segundos
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    return new Response(200, new JSONObject().put("message", "Procesamiento completado"));
});
```

---

## **Respuestas**

Las respuestas pueden ser configuradas con diferentes tipos de contenido, códigos de estado y cabeceras personalizadas.

### Ejemplo básico

```java
Response response = new Response(200, new JSONObject().put("key", "value"));
response.addHeader("Content-Type", "application/json");
response.setStatus(200);
return response;
```

### Respuesta con texto plano

```java
Response response = new Response(200, "Texto plano");
response.addHeader("Content-Type", "text/plain");
return response;
```

### Respuesta con redirección

```java
Response response = new Response(302);
response.addHeader("Location", "https://example.com");
return response;
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
