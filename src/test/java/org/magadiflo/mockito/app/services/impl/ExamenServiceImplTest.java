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

    }
}