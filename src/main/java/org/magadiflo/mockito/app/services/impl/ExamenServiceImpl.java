package org.magadiflo.mockito.app.services.impl;

import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.repositories.IQuestionRepository;
import org.magadiflo.mockito.app.services.IExamService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;
    private final IQuestionRepository questionRepository;

    public ExamenServiceImpl(IExamRepository examRepository, IQuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Optional<Exam> examOptional = this.findExamByName(name);
        if (examOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("¡No existe el exam %s buscado!", name));
        }
        Exam exam = examOptional.get();
        List<String> questions = this.questionRepository.findQuestionsByExamId(exam.getId());
        exam.setQuestions(questions);
        return exam;
    }
}
