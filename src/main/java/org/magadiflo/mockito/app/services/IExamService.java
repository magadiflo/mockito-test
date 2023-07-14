package org.magadiflo.mockito.app.services;

import org.magadiflo.mockito.app.models.Exam;

public interface IExamService {
    Exam findExamByName(String name);
}
