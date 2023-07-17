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
    void orderInvocationsTest(){
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        InOrder inOrder = inOrder(this.questionRepository);

        inOrder.verify(this.questionRepository).findQuestionsByExamId(1L);
        inOrder.verify(this.questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void orderInvocationsTest2(){
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);


        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        InOrder inOrder = inOrder(this.examRepository, this.questionRepository);

        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionsByExamId(1L);
        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionsByExamId(5L);
    }
}