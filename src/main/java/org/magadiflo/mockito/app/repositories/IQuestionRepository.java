package org.magadiflo.mockito.app.repositories;

import java.util.List;

public interface IQuestionRepository {
    List<String> findQuestionsByExamId(Long id);

    void saveQuestions(List<String> questions);
}
