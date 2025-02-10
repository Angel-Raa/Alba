package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;
import io.github.angel.raa.utils.AlbaUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Middleware para manejar el idioma de la solicitud.
 * Este middleware permite establecer un idioma predeterminado y verificar si el cliente
 * acepta un idioma específico. Si el cliente no acepta un idioma específico, se enviará
 * una respuesta de error 406 (Not Acceptable).
 * Ejemplo de uso:
 * <pre>
 *     {@code
 * new Server(8080)
 *     .use(new LanguageMiddleware()
 *         .setDefaultLanguage("es")
 *         .addSupportedLanguage("en")
 *         .addSupportedLanguage("es")
 *         .setLanguageHeader("Accept-Language")
 *     )
 *     .get("/hello", (request) -> {
 *         String language = request.getHeader("Accept-Language");
 *         return new Response(200, new JSONObject().put("message", "Hello, World!").put("language", language));
 *     });
 *  }
 *  </pre>
 *
 * @see Request#getHeader(String)
 * @see Response#build()
 * @see AlbaUtils#isNotBlank(String)
 */
public class LanguageMiddleware implements Middleware {
    private final Set<String> supportedLanguages = new HashSet<>();
    private String defaultLanguage = "en";
    private String languageHeader = "Accept-Language";

    public LanguageMiddleware() {

    }

    /**
     * Agrega un idioma soportado al middleware.
     * Si el idioma no es válido, se lanzará una IllegalArgumentException.
     *
     */
    public LanguageMiddleware addSupportedLanguage(String language) {
        if (!AlbaUtils.isNotBlank(language)) {
            throw new IllegalArgumentException("Language cannot be null or empty");
        }
        supportedLanguages.add(language);
        return this;
    }

    /**
     * Agrega varios idiomas soportados al middleware.
     * Si alguno de los idiomas no es válido, se lanzará una IllegalArgumentException.
     *
     */
    public LanguageMiddleware addSupportedLanguages(Set<String> languages) {
        if (languages.stream().anyMatch(land -> !AlbaUtils.isNotBlank(land))) {
            throw new IllegalArgumentException("Language cannot be null or empty");
        }
        this.supportedLanguages.addAll(languages);
        return this;
    }

    /**
     * Agrega varios idiomas soportados al middleware.
     * Si alguno de los idiomas no es válido, se lanzará una IllegalArgumentException.
     *
     */
    public LanguageMiddleware addSupportedLanguages(String... languages) {
        if (Arrays.stream(languages).anyMatch(land -> !AlbaUtils.isNotBlank(land))) {
            throw new IllegalArgumentException("Language cannot be null or empty");
        }
        this.supportedLanguages.addAll(Arrays.stream(languages).collect(Collectors.toSet()));
        return this;
    }

    /**
     * Establece el encabezado de idioma.
     * Si el encabezado no es válido, se lanzará una IllegalArgumentException.
     *
     */
    public LanguageMiddleware setLanguageHeader(String languageHeader) {
        if (!AlbaUtils.isNotBlank(languageHeader)) {
            throw new IllegalArgumentException("Language header cannot be null or empty");
        }
        this.languageHeader = languageHeader;
        return this;
    }

    /**
     * Establece el idioma por defecto.
     * Si el idioma no es válido, se lanzará una IllegalArgumentException.
     *
     */
    public LanguageMiddleware setDefaultLanguage(String defaultLanguage) {
        if (!AlbaUtils.isNotBlank(defaultLanguage)) {
            throw new IllegalArgumentException("Default language cannot be null or empty");
        }
        this.defaultLanguage = defaultLanguage;
        return this;
    }


    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String acceptLanguageHeader = request.getHeader(languageHeader);
        String preferredLanguage = determinePreferredLanguage(acceptLanguageHeader);
        request.setAttribute("language", preferredLanguage);
        return chain.next(request, response);
    }

    /**
     * Determina el idioma preferido basándose en el encabezado de idioma..
     */
    private String determinePreferredLanguage(String acceptLanguageHeader) {
        if (!AlbaUtils.isNotBlank(acceptLanguageHeader)) return defaultLanguage;
        // Dividir el encabezado en idiomas y sus calificaciones (q-values)
        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(acceptLanguageHeader);

        for (Locale.LanguageRange range : languageRanges) {
            String languageTag = range.getRange();
            if (supportedLanguages.contains(languageTag)) {
                return languageTag;
            }
        }
        return defaultLanguage;
    }
}
