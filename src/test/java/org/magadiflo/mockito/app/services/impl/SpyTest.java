package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.Test;
import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.repositories.IQuestionRepository;
import org.magadiflo.mockito.app.repositories.impl.ExamRepositoryImpl;
import org.magadiflo.mockito.app.repositories.impl.QuestionRepositoryImpl;
import org.magadiflo.mockito.app.services.IExamService;
import org.magadiflo.mockito.app.source.Data;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpyTest {
    @Test
    void testSpyRealCalls() {
        IExamRepository examRepository = spy(ExamRepositoryImpl.class);
        IQuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        IExamService examService = new ExamenServiceImpl(examRepository, questionRepository);

        Exam exam = examService.findExamByNameWithQuestions("Aritmética");

        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
        assertFalse(exam.getQuestions().isEmpty());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 3 (real)"));
    }

    @Test
    void testSpyWithSimulatedCalls() {
        IExamRepository examRepository = spy(ExamRepositoryImpl.class);
        IQuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        IExamService examService = new ExamenServiceImpl(examRepository, questionRepository);

        doReturn(Data.EXAMS).when(examRepository).findAll();
        doReturn(Data.QUESTIONS).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Aritmética");

        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
        assertFalse(exam.getQuestions().isEmpty());
        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 3"));
    }
}
