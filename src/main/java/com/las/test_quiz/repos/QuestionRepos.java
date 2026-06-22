package com.las.test_quiz.repos;

import com.las.test_quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepos extends JpaRepository<Question, Long> {
}
