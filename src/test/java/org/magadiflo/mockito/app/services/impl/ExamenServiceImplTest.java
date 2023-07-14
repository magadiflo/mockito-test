package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.services.IExamService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamenServiceImplTest {

    @Test
    void findExamByName() {
        IExamRepository examRepository = mock(IExamRepository.class);
        IExamService examService = new ExamenServiceImpl(examRepository);

        List<Exam> exams = List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17"));
        when(examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isPresent());
        assertEquals(1L, optionalExam.get().getId());
        assertEquals("Aritmética", optionalExam.get().getName());
    }

    @Test
    @DisplayName("Retorna un optional vacío ya que no existe ningún elemento en la lista")
    void findExamByNameReturnOptionalEmpty() {
        IExamRepository examRepository = mock(IExamRepository.class);
        IExamService examService = new ExamenServiceImpl(examRepository);

        List<Exam> exams = List.of();
        when(examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isEmpty());
    }
}