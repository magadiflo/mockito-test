package org.magadiflo.mockito.app.services.impl;

import org.mockito.ArgumentMatcher;

public class MiArgsMatchers implements ArgumentMatcher<Long> {
    private Long argument;

    @Override
    public boolean matches(Long argument) {
        this.argument = argument;
        return this.argument != null && this.argument > 0;
    }

    @Override
    public String toString() {
        return String.format("El arg enviado fue %d, se esperaba que fuera un entero positivo", this.argument);
    }
}
