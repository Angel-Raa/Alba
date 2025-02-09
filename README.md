# **Alba Framework**

**Alba** es un framework minimalista diseñado para crear servidores web en Java. Ofrece una API sencilla y flexible para gestionar rutas, middlewares y respuestas HTTP. Este framework está inspirado en herramientas populares como [Hono.js](https://hono.dev/) y [Express.js](https://expressjs.com/), pero con un enfoque ligero y minimalista, ideal para aprender cómo funcionan los servidores web desde cero.

---

## **Nuevas Funcionalidades: Integración con Thymeleaf**

### 1. **Soporte para Thymeleaf**

Alba ahora incluye soporte nativo para **Thymeleaf**, Las plantillas deben almacenarse en la carpeta `resources/templates/` y se configuran mediante el archivo `alba.properties`.

#### **Configuración en `alba.properties`**
```properties
# Configuración del Motor de Plantillas
alba.template.engine=thymeleaf
alba.template.cache=true
alba.template.prefix=templates/
alba.template.suffix=.html
```

- **alba.template.engine**: Especifica el motor de plantillas a usar (en este caso, `thymeleaf`).
- **alba.template.cache**: Habilita o deshabilita el caché de plantillas.
- **alba.template.prefix**: Prefijo para la ubicación de las plantillas (carpeta `templates/`).
- **alba.template.suffix**: Sufijo para los archivos de plantilla (.html).

---

### 2. **Ejemplos de Uso**

#### **A. Uso en Métodos de Clase**

Puedes usar Thymeleaf en métodos de clase anotados con `@Get`, `@Post`, etc. Aquí tienes un ejemplo:

```java
@Get("/home")
public Response getHome(Request request) {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Bienvenido a Alba");
    model.put("message", "¡Hola desde Thymeleaf!");
    return new Response().addTemplate("index.html", model);
}
```

- Se crea un modelo (`Map<String, Object>`) con datos dinámicos (`title` y `message`).
- La plantilla `index.html` se procesa con el modelo y se devuelve como respuesta.

#### **B. Uso con Funciones Lambda**

También puedes usar funciones lambda para manejar rutas y devolver plantillas:

```java
server.get("/hello", res -> {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Hola Mundo");
    model.put("message", "Este es un ejemplo de función lambda con Thymeleaf.");
    Response response = new Response(200, null);
    response.addTemplate("index.html", model);
    return response;
});
```

- La ruta `/hello` devuelve una página HTML generada dinámicamente con Thymeleaf.
- El modelo contiene los datos que se inyectan en la plantilla.

---

### 3. **Estructura de la Carpeta `templates/`**

Las plantillas deben almacenarse en la carpeta `resources/templates/`. Por ejemplo:

```plaintext
src/
├── main/
│   ├── resources/
│   │   ├── templates/
│   │   │   └── index.html
```

#### **Ejemplo de Plantilla (`index.html`)**

Aquí tienes un ejemplo de una plantilla Thymeleaf:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title th:text="${title}">Título por defecto</title>
</head>
<body>
    <h1 th:text="${message}">Mensaje por defecto</h1>
</body>
</html>
```

- **`${title}`** y **`${message}`** son variables del modelo que se inyectan en la plantilla.
- Si no se proporciona un valor para estas variables, se mostrarán los valores por defecto.

---

### 4. **Métodos Relevantes en la Clase `Response`**

#### **A. Método `addTemplate`**

El método `addTemplate` permite renderizar una plantilla con un modelo y devolverla como respuesta.

```java
public Response addTemplate(String templateName, Map<String, Object> model) {
    if (templateName == null || templateName.isEmpty()) {
        throw new IllegalArgumentException("El nombre de la plantilla no puede ser nulo o vacío");
    }
    addHeader("Content-Type", "text/html; charset=UTF-8");
    this.body = templateProcessor.render(templateName, model);
    return this;
}
```

- **templateName**: Nombre de la plantilla (sin prefijo ni sufijo).
- **model**: Datos a inyectar en la plantilla.

---

## **Middlewares**

### A. **Middleware CORS (CorsMiddleware)**

Este middleware permite configurar políticas de acceso entre dominios (Cross-Origin Resource Sharing).

#### **Configuración Básica:**

```java
CorsMiddleware corsMiddleware = new CorsMiddleware();
corsMiddleware.addAllowedOrigins("https://example.com")
              .addAllowedMethods("GET", "POST")
              .setAllowCredentials(true)
              .setMaxAge(3600);
server.use(corsMiddleware);
```

- Permite configurar orígenes permitidos, métodos HTTP y credenciales.

---

### B. **Middleware de Restricción de IPs (IpRestrictionMiddleware)**

Restringe el acceso a ciertas rutas o al servidor completo basándose en las direcciones IP de los clientes.

#### **Configuración Básica:**

```java
IpRestrictionMiddleware ipRestrictionMiddleware = new IpRestrictionMiddleware()
        .addAllowedIps(Arrays.asList("192.168.1.10", "192.168.1.20"))
        .addBlockedIps(Collections.singletonList("192.168.1.15"))
        .setAllowAllByDefault(false); // Bloquea todas las IPs por defecto
server.use(ipRestrictionMiddleware);
```

- Permite crear una lista blanca y negra de IPs.

---

### C. **Middleware de Idioma (LanguageMiddleware)**

Detecta el idioma preferido del cliente (basado en el encabezado `Accept-Language`) y configura el idioma de la aplicación en función de eso.

#### **Configuración Básica:**

```java
LanguageMiddleware languageMiddleware = new LanguageMiddleware()
        .addSupportedLanguages(Set.of("es", "en", "fr"))
        .setDefaultLanguage("en")
        .setLanguageHeader("Accept-Language");
server.use(languageMiddleware);
```

- Detecta automáticamente el idioma preferido del cliente.

---

### D. **Tipado Automático del Cuerpo de la Solicitud (getBodyAs)**

La clase `Request` ahora incluye un método `getBodyAs` que convierte automáticamente el cuerpo JSON de una solicitud en un objeto Java tipado.

#### **Uso:**

```java
public Response createUser(Request request) {
    CreateUserRequest body = request.getBodyAs(CreateUserRequest.class);
    return new Response(200, new JSONObject().put("message", "Usuario creado"));
}
```

---

### E. **Agrupación de Rutas (Route Grouping)**

Ahora puedes agrupar rutas relacionadas bajo un prefijo común para reducir la repetición de código.

#### **Configuración Básica:**

```java
server.route("/api/v1/posts", new PostController());
```

---

## **Ejemplo Completo de Uso**

Aquí tienes un ejemplo completo que utiliza todas las nuevas funcionalidades del framework:

```java
import server.Server;
import middleware.CorsMiddleware;
import middleware.IpRestrictionMiddleware;
import middleware.LanguageMiddleware;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);

        // Middleware CORS
        server.use(new CorsMiddleware().addAllowedOrigins("https://example.com"));
        
        // Middleware Restricción IPs
        server.use(new IpRestrictionMiddleware().addAllowedIps(Arrays.asList("192.168.1.10")));
        
        // Middleware Idioma
        server.use(new LanguageMiddleware().setDefaultLanguage("en"));

        // Rutas agrupadas
        server.route("/api/v1/posts", new PostController());

        // Ruta simple
        server.get("/", request -> new Response(200, new JSONObject().put("message", "Welcome")));
        
        server.start();
    }
}
```

---

## **Cómo Usar el Framework**

### 1. **Ejemplo Básico: Crear un Servidor Simple**

```java
public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.get("/user/:id", request -> {
            String userId = request.getPathParam("id");
            return new Response(200, new JSONObject().put("userId", userId));
        });
        server.start();
    }
}
```

---

## **Rutas Disponibles**

- **GET**
- **POST**
- **PUT**
- **DELETE**

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
