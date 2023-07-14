package org.magadiflo.mockito.app.repositories;

import org.magadiflo.mockito.app.models.Exam;

import java.util.List;

public interface IExamRepository {
    List<Exam> findAll();
}
