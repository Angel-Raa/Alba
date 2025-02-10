package io.github.angel.raa.templates;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

public class CsrfDialect extends AbstractProcessorDialect {
    protected CsrfDialect() {
        super("CSRF Dialect", "csrf", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new CsrfAttributeTagProcessor(dialectPrefix));
        return processors;
    }
}
