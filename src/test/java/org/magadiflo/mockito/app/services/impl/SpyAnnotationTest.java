package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.impl.ExamRepositoryImpl;
import org.magadiflo.mockito.app.repositories.impl.QuestionRepositoryImpl;
import org.magadiflo.mockito.app.source.Data;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpyAnnotationTest {
    @Spy
    ExamRepositoryImpl examRepository;
    @Spy
    QuestionRepositoryImpl questionRepository;
    @InjectMocks
    ExamenServiceImpl examService;

    @Test
    void testSpyRealCalls() {
        Exam exam = this.examService.findExamByNameWithQuestions("Aritmética");

        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
        assertFalse(exam.getQuestions().isEmpty());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 3 (real)"));
    }

    @Test
    void testSpyWithSimulatedCalls() {
        doReturn(Data.getExams()).when(this.examRepository).findAll();
        doReturn(Data.getQuestions()).when(this.questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = this.examService.findExamByNameWithQuestions("Aritmética");

        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
        assertFalse(exam.getQuestions().isEmpty());
        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 3"));
    }
}
