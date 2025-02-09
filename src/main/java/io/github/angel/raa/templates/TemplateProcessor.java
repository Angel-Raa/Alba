package io.github.angel.raa.templates;

import io.github.angel.raa.exceptions.TemplateNotFoundException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

/**
 * Clase que procesa plantillas Thymeleaf.
 * Permite renderizar plantillas HTML con diferentes opciones de modelos.
 */
public class TemplateProcessor {
    /**
     * Motor de plantillas Thymeleaf.
     */
    private final TemplateEngine templateEngine;
    private final TemplateConfig templateConfig;


    public TemplateProcessor() {
        this.templateEngine = new TemplateEngine();
        this.templateConfig = new TemplateConfig();
        configureTemplateEngine();
    }

    /**
     * Configura el motor de plantillas Thymeleaf.
     */
    private void configureTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templateConfig.getPrefix()); // Carpeta donde están las plantillas
        templateResolver.setSuffix(templateConfig.getSuffix());       // Extensión de las plantillas
        templateResolver.setTemplateMode("HTML");  // Modo de plantilla (HTML)
        templateResolver.setCharacterEncoding("UTF-8"); // Codificación
        templateResolver.setCacheable(templateConfig.isCacheEnabled());      // Desactivar caché en desarrollo
        this.templateEngine.setTemplateResolver(templateResolver);
    }


    /**
     * Renderiza una plantilla Thymeleaf con un modelo de datos.
     *
     * @param templateName Nombre de la plantilla (sin extensión).
     * @param model        Mapa de datos para la plantilla.
     * @return String HTML renderizado.
     * @throws TemplateNotFoundException
     */
    public String render(String templateName, Map<String, Object> model) {
        try {
            Context context = new Context();
            context.setVariables(model);
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            throw new TemplateNotFoundException("Plantilla no encontrada: " + templateName);
        }
    }

    /**
     * Renderiza una plantilla Thymeleaf sin modelo de datos.
     *
     * @param templateName Nombre de la plantilla (sin extensión).
     * @return String HTML renderizado.
     * @throws Exception Si ocurre un error durante el procesamiento de la plantilla.
     */
    public String render(String templateName) {
        try {
            return templateEngine.process(templateName, new Context());
        } catch (Exception e) {
            throw new TemplateNotFoundException("Plantilla no encontrada: " + templateName);
        }
    }

    /**
     * Renderiza una plantilla Thymeleaf con una variable de datos.
     *
     * @param templateName Nombre de la plantilla (sin extensión).
     * @param key          Clave de la variable.
     * @param value        Valor de la variable.
     * @return String HTML renderizado.
     * @throws Exception Si ocurre un error durante el procesamiento de la plantilla.
     */
    public String render(String templateName, String key, Object value) {
        try {
            Context context = new Context();
            context.setVariable(key, value);
            return templateEngine.process(templateName, context);

        } catch (Exception e) {
            throw new TemplateNotFoundException("Plantilla no encontrada: " + templateName);
        }
    }
}
