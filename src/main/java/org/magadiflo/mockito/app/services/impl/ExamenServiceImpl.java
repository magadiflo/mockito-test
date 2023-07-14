package org.magadiflo.mockito.app.services.impl;

import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;
import org.magadiflo.mockito.app.services.IExamService;

import java.util.NoSuchElementException;

public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;

    public ExamenServiceImpl(IExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el examen: " + name));
    }
}
