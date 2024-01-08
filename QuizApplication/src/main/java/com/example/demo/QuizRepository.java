package com.example.demo;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Quiz;

public interface QuizRepository extends CrudRepository<Quiz, Integer> {

	@Query("SELECT id FROM quiz ORDER BY RAND() limit 1")
	Integer getRandomId();
	
	@Modifying
	@Query("UPDATE QUIZ_SEQUENCE SET ID = LAST_INSERT_ID(ID) + 1")
	void setNewId();
	
	@Query("SELECT id FROM QUIZ_SEQUENCE")
	Integer getId();
}
