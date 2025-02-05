¡Claro! Vamos a actualizar la documentación para incluir todas las nuevas características y middlewares que hemos implementado hasta ahora. Esto incluye los middlewares de **CORS**, **Logger**, **CSRF Protection**, **Timeout**, y la gestión de atributos de sesión.

---

# **Alba Framework**

**Alba** es un framework minimalista diseñado para crear servidores web en Java. Ofrece una API sencilla y flexible para gestionar rutas, middlewares y respuestas HTTP. Este framework está inspirado en herramientas populares como [Hono.js](https://hono.dev/) y [Express.js](https://expressjs.com/), pero con un enfoque ligero y minimalista, ideal para aplicaciones pequeñas o para aprender cómo funcionan los servidores web desde cero.

## **Características principales**

- **Enrutamiento**: Soporta rutas HTTP como `GET`, `POST`, `PUT` y `DELETE`.
- **Middlewares**: Facilita el uso de middlewares para manipular solicitudes y respuestas.
- **Respuestas personalizadas**: Construcción de respuestas con cuerpos en JSON, manejo de cabeceras personalizadas y códigos de estado HTTP.
- **Fácil de usar**: API clara y minimalista para definir rutas y manejar peticiones.
- **Extensible**: Diseñado para ser ligero y fácil de extender según las necesidades del proyecto.

---

## **Uso básico**

Aquí tienes un ejemplo básico de cómo crear un servidor con **Alba Framework**:

```java
public class App {
    public static void main(String[] args) throws IOException {
        // Crear un servidor en el puerto 8080
        Server server = new Server(8080);

        // Definir una ruta GET para "/hey"
        server.get("/hey", request -> {
            Response response = new Response(200, new JSONObject().put("message", "Hola Mundo desde Alba"));
            response.addHeader("Content-Type", "application/json");
            return response;
        });

        // Iniciar el servidor
        server.start();
    }
}
```

### Explicación del código:
1. **Creación del servidor**: Se instancia un servidor en el puerto `8080`.
2. **Definición de rutas**: Se define una ruta `GET` para `/hey` que devuelve una respuesta JSON.
3. **Inicio del servidor**: El método `start()` inicia el servidor y comienza a escuchar peticiones.

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


## **Licencia**

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

