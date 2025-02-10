package io.github.angel.raa.templates;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Procesador de atributos para generar un token CSRF.
 *
 * Ejemplo de uso:
 * <pre>{@code
 *    <form th:action="@{/login}" method="post">
 *       <input type="hidden" name="${csrf}" value="${csrf}">
 *       <button type="submit">Iniciar sesión</button>
 *    </form>
 * }</pre>
 *
 */
public class CsrfAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private static final String ATTR_NAME = "csrf";
    private static final int PRECEDENCE = 1000;
    public CsrfAttributeTagProcessor(final String dialectPrefix) {
        super(
                TemplateMode.HTML, // Modo de plantilla (HTML)
                dialectPrefix,     // Prefijo del dialecto (opcional)
                null,              // No aplicable a ningún elemento específico
                false,             // No aplicable a ningún atributo específico
                ATTR_NAME,         // Nombre del atributo que activa este procesador
                true,              // Aplicar a todos los elementos que tengan este atributo
                PRECEDENCE,        // Precedencia del procesador
                true               // Eliminar el atributo después de procesarlo
        );
    }


    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final IElementTagStructureHandler structureHandler
    ) {
        // Obtener el token CSRF del contexto
        final String csrfToken = (String) context.getVariable("csrfToken");

        // Agregar el campo CSRF al formulario
        if (csrfToken != null) {
            structureHandler.setAttribute("name", "_csrf");
            structureHandler.setAttribute("type", "hidden");
            structureHandler.setAttribute("value", csrfToken);
        }
    }
}
