package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.repositories.IQuestionRepository;
import org.magadiflo.mockito.app.source.Data;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    private IExamRepository examRepository;
    @Mock
    private IQuestionRepository questionRepository;

    @InjectMocks
    private ExamenServiceImpl examService;

    @Captor
    ArgumentCaptor<Long> captor;

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

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());

        verify(this.examRepository).findAll();
        verify(this.questionRepository, never()).findQuestionsByExamId(anyLong());
    }

    @Test
    void saveExamWithoutQuestions() {
        when(this.examRepository.saveExam(any(Exam.class))).thenReturn(Data.EXAM);
        Exam examDB = this.examService.saveExam(Data.EXAM);

        assertNotNull(examDB);
        assertEquals(9L, examDB.getId());
        assertEquals("Docker", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository, never()).saveQuestions(anyList());
    }

    @Test
    void saveExamWithQuestions() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);

        when(this.examRepository.saveExam(any(Exam.class))).thenReturn(exam);
        doNothing().when(this.questionRepository).saveQuestions(anyList());

        Exam examDB = this.examService.saveExam(exam);

        assertNotNull(examDB);
        assertEquals(9L, examDB.getId());
        assertEquals("Docker", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository).saveQuestions(anyList());
    }

    @Test
    void saveExamWithQuestionsReturnExamWithId() {
        // given
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        when(this.examRepository.saveExam(any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam examToSave = invocation.getArgument(0);
                examToSave.setId(sequence++);
                return examToSave;
            }
        });
        doNothing().when(this.questionRepository).saveQuestions(anyList());

        // when
        Exam examDB = this.examService.saveExam(exam);

        // then
        assertNotNull(examDB);
        assertEquals(8L, examDB.getId());
        assertEquals("Kubernetes", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository).saveQuestions(anyList());
    }

    @Test
    void workingWithExceptions() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(this.questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.examService.findExamByNameWithQuestions("Aritmética");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(isNull());
    }

    @Test
    void argumentMatchersTest() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(1L)));
        verify(this.questionRepository).findQuestionsByExamId(eq(1L));
    }

    @Test
    void argumentMatchersTest2() {
//        when(this.examRepository.findAll()).thenReturn(Data.EXAMS_NEGATIVES);
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentCaptor() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(this.questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(1L, captor.getValue());
    }

    @Test
    void testArgumentCaptorWithAnnotations() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(1L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        doThrow(IllegalArgumentException.class).when(this.questionRepository).saveQuestions(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            this.examService.saveExam(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Data.QUESTIONS : List.of();
        }).when(this.questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = this.examService.findExamByNameWithQuestions("Programación");

        assertEquals(5L, exam.getId());
        assertFalse(exam.getQuestions().isEmpty());
    }

    @Test
    void doAnswerSaveExamWithQuestionsReturnExamWithId() {
        // given
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        doAnswer(invocation -> {
            Exam examDB = invocation.getArgument(0);
            examDB.setId(10L);
            return examDB;
        }).when(this.examRepository).saveExam(any(Exam.class));

        doNothing().when(this.questionRepository).saveQuestions(anyList());

        // when
        Exam examDB = this.examService.saveExam(exam);

        // then
        assertNotNull(examDB);
        assertEquals(10L, examDB.getId());
        assertEquals("Kubernetes", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository).saveQuestions(anyList());
    }
}