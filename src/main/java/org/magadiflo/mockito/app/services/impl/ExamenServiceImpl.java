package org.magadiflo.mockito.app.services.impl;

import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.services.IExamService;

import java.util.Optional;

public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;

    public ExamenServiceImpl(IExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst();
    }
}
