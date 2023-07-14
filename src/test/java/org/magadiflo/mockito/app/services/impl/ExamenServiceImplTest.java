package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.repositories.IQuestionRepository;
import org.magadiflo.mockito.app.services.IExamService;
import org.magadiflo.mockito.app.source.Data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamenServiceImplTest {
    private IExamRepository examRepository;
    private IQuestionRepository questionRepository;
    private IExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = mock(IExamRepository.class);
        this.questionRepository = mock(IQuestionRepository.class);

        this.examService = new ExamenServiceImpl(this.examRepository, this.questionRepository);
    }

    @Test
    void findExamByName() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        Optional<Exam> optionalExam = this.examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isPresent());
        assertEquals(1L, optionalExam.get().getId());
        assertEquals("Aritmética", optionalExam.get().getName());
    }

    @Test
    @DisplayName("Retorna un optional vacío ya que no existe ningún elemento en la lista")
    void findExamByNameReturnOptionalEmpty() {
        List<Exam> exams = List.of();
        when(this.examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = this.examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isEmpty());
    }

    @Test
    void findExamByNameWithQuestions() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 10"));
    }

    @Test
    void throwNoSuchElementExceptionIfNotExistsExam() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());
    }

    @Test
    void findExamByNameWithQuestionsUsingVerify() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 10"));

        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void throwNoSuchElementExceptionIfNotExistsExamUsingVerify() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());

        verify(this.examRepository).findAll();
        verify(this.questionRepository, never()).findQuestionsByExamId(anyLong());
    }

}