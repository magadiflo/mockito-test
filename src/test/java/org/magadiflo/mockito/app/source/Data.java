package org.magadiflo.mockito.app.source;

import org.magadiflo.mockito.app.models.Exam;

import java.util.List;

public class Data {
    public static List<Exam> getExams() {
        return List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17")
        );
    }

    public static List<Exam> getExamsNegatives() {
        return List.of(
                new Exam(-1L, "Aritmética"),
                new Exam(-2L, "Geometría"),
                new Exam(-3L, "Álgebra")
        );
    }

    public static List<Exam> getExamsIdNull() {
        return List.of(
                new Exam(null, "Aritmética"),
                new Exam(null, "Geometría"),
                new Exam(null, "Álgebra")
        );
    }

    public static List<String> getQuestions() {
        return List.of("Pregunta 1", "Pregunta 2", "Pregunta 3",
                "Pregunta 4", "Pregunta 5", "Pregunta 6",
                "Pregunta 7", "Pregunta 8", "Pregunta 9",
                "Pregunta 10");
    }

    public static Exam getExam() {
        return new Exam(9L, "Docker");
    }

    public static Exam getExamWithoutId() {
        return new Exam(null, "Kubernetes");
    }
}
