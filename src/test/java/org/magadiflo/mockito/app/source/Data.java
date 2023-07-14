package org.magadiflo.mockito.app.source;

import org.magadiflo.mockito.app.models.Exam;

import java.util.List;

public class Data {
    public static final List<Exam> EXAMS = List.of(
            new Exam(1L, "Aritmética"),
            new Exam(2L, "Geometría"),
            new Exam(3L, "Álgebra"),
            new Exam(4L, "Trigonometría"),
            new Exam(5L, "Programación"),
            new Exam(6L, "Bases de Datos"),
            new Exam(7L, "Estructura de datos"),
            new Exam(8L, "Java 17"));
}
