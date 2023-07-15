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

    public static final List<Exam> EXAMS_ID_NULL = List.of(
            new Exam(null, "Aritmética"),
            new Exam(null, "Geometría"),
            new Exam(null, "Álgebra"));

    public static final List<String> QUESTIONS = List.of("Pregunta 1", "Pregunta 2", "Pregunta 3",
            "Pregunta 4", "Pregunta 5", "Pregunta 6", "Pregunta 7", "Pregunta 8", "Pregunta 9",
            "Pregunta 10");

    public static final Exam EXAM = new Exam(9L, "Docker");
    public static final Exam EXAM_WHITOUT_ID = new Exam(null, "Kubernetes");
}
