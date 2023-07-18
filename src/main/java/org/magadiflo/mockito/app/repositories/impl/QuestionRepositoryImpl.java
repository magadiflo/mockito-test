package org.magadiflo.mockito.app.repositories.impl;

import org.magadiflo.mockito.app.repositories.IQuestionRepository;

import java.util.List;

public class QuestionRepositoryImpl implements IQuestionRepository {
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        return List.of("Pregunta 1 (real)", "Pregunta 2 (real)", "Pregunta 3 (real)",
                "Pregunta 4 (real)", "Pregunta 5 (real)");
    }

    @Override
    public void saveQuestions(List<String> questions) {

    }
}
