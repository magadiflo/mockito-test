package org.magadiflo.mockito.app.repositories.impl;

import org.magadiflo.mockito.app.models.Exam;
import org.magadiflo.mockito.app.repositories.IExamRepository;

import java.util.List;

public class ExamRepositoryImpl implements IExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of();
    }
}
