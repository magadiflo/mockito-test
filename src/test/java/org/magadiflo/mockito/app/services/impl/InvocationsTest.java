package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.repositories.IQuestionRepository;
import org.magadiflo.mockito.app.source.Data;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvocationsTest {
    @Mock
    private IExamRepository examRepository;
    @Mock
    private IQuestionRepository questionRepository;

    @InjectMocks
    private ExamenServiceImpl examService;

    @Test
    void orderInvocationsTest() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        InOrder inOrder = inOrder(this.questionRepository);

        inOrder.verify(this.questionRepository).findQuestionsByExamId(1L);
        inOrder.verify(this.questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void orderInvocationsTest2() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);


        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        InOrder inOrder = inOrder(this.examRepository, this.questionRepository);

        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionsByExamId(1L);
        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void numberInvocationsTest() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.questionRepository).findQuestionsByExamId(1L);
        verify(this.questionRepository, times(1)).findQuestionsByExamId(1L);
        verify(this.questionRepository, atLeast(1)).findQuestionsByExamId(1L);
        verify(this.questionRepository, atLeastOnce()).findQuestionsByExamId(1L);
        verify(this.questionRepository, atMost(1)).findQuestionsByExamId(1L);
        verify(this.questionRepository, atMostOnce()).findQuestionsByExamId(1L);
    }

    @Test
    void neverTest() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());

        verify(this.examRepository, times(1)).findAll();
        verify(this.questionRepository, never()).findQuestionsByExamId(anyLong());
        verifyNoInteractions(this.questionRepository);
    }
}
