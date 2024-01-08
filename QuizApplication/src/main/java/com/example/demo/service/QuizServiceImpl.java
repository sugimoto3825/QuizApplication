package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.QuizRepository;
import com.example.demo.entity.Quiz;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {
	@Autowired
	QuizRepository repository;

	@Override
	public Iterable<Quiz> selectAll(){
		return repository.findAll();
	}
	
	@Override
	public Optional<Quiz> selectOneById(Integer id){
		return repository.findById(id);
	}
	
	@Override
	public Optional<Quiz> selectOneRandomQuiz(){
		Integer randId = repository.getRandomId();
		
		if(randId == null){
			return Optional.empty();
		}
		
		return repository.findById(randId);
	}
	
	@Override
	public Boolean checkQuiz(Integer id, Boolean myAnswer) {
		Boolean check = false;
		
		Optional<Quiz> optQuiz = selectOneById(id);
		
		if(optQuiz.isPresent()){
			Quiz quiz = optQuiz.get();
			
			if(quiz.getAnswer() == myAnswer) {
				check = true;
			}
		}
		
		return check;
	}
	
	@Override
	public void insertQuiz(Quiz quiz) {
//		//IDの新規採番
//		repository.setNewId();
//		quiz.setId(repository.getId());
		
		//QuizデータのInsert
		repository.save(quiz);
	}
	
	@Override
	public void updateQuiz(Quiz quiz) {
		repository.save(quiz);
	}

	@Override
	public void deleteQuizById(Integer id) {
		repository.deleteById(id);
	}

}
