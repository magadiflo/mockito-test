package org.magadiflo.mockito.app.services;

import org.magadiflo.mockito.app.models.Exam;

import java.util.Optional;

public interface IExamService {
    Optional<Exam> findExamByName(String name);
    Exam findExamByNameWithQuestions(String name);
}
