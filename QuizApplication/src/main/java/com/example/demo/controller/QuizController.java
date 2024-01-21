package com.example.demo.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Quiz;
import com.example.demo.form.QuizForm;
import com.example.demo.service.QuizService;

@Controller
@RequestMapping("/quiz")
public class QuizController {
	@Autowired
	QuizService service;
	
	/* form-backing bean (HTMLのフォームにバインドするbean） */
	@ModelAttribute
	public QuizForm setUpForm() {
		QuizForm form = new QuizForm();
		//回答の初期値は"○"
		form.setAnswer(true);
		return form;
	}
	
	//http://localhost:8080/quizにアクセスするとshowListの処理が実行される
	@GetMapping
	public String showList(QuizForm quizForm, Model model) {
		//新規登録モードで初期表示
		quizForm.setNewQuiz(true);
		
		Iterable<Quiz> list = service.selectAll();
		model.addAttribute("list", list);
		model.addAttribute("title", "登録用フォーム");
		return "crud";
	}
	
	@PostMapping("/insert")
	public String insert(@Validated QuizForm quizForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes
			) {
		Quiz quiz = new Quiz();
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		
		if(!bindingResult.hasErrors()) {
			service.insertQuiz(quiz);
			redirectAttributes.addFlashAttribute("complete", "登録が完了しました");
			return "redirect:/quiz";
		} else {
			return showList(quizForm, model);
		}
		
		
	}
	
	//下部エリアの編集ボタンを押下するとshowUpdateの処理が実行される
	@GetMapping("/{id}")
	public String showUpdate(QuizForm quizForm, @PathVariable Integer id, Model model) {
		//idに対応するquizを取得
		Optional<Quiz> quizOpt = service.selectOneById(id);
		//mapメソッド（中間操作）
		//値がある時にだけmakeQuizFormでquiz→quizFormに変換した結果を返す
		Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
		
		if(quizFormOpt.isPresent()) {
			quizForm = quizFormOpt.get();
		}
		
		makeUpdateModel(quizForm, model);
		return "crud";
	}
	
	private void makeUpdateModel(QuizForm quizForm, Model model) {
		model.addAttribute("id", quizForm.getId());
		quizForm.setNewQuiz(false);
		model.addAttribute("quizForm", quizForm);
		model.addAttribute("title", "更新用フォーム");
	}
	
	@PostMapping("/update")
	public String update(
			@Validated QuizForm quizForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		Quiz quiz = makeQuiz(quizForm);
		
		if(!result.hasErrors()) {
			service.updateQuiz(quiz);
			redirectAttributes.addFlashAttribute("complete", "更新が完了しました");
			return "redirect:/quiz/" + quiz.getId(); //redirect:ではURLを指定する
		} else {
			makeUpdateModel(quizForm, model);
			return "crud"; //ビューを返す時はredirect:は付けない
		}
	}
	
	@PostMapping("/delete")
	public String delete(
			@RequestParam("id") String id,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		service.deleteQuizById(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
		return "redirect:/quiz";
	}
	
	//Form→Entityへの変換
	private Quiz makeQuiz(QuizForm quizForm) {
		Quiz quiz = new Quiz();
		quiz.setId(quizForm.getId());
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		return quiz;
	}
	
	//Entity→Formへの変換
	private QuizForm makeQuizForm(Quiz quiz) {
		QuizForm quizForm = new QuizForm();
		quizForm.setId(quiz.getId());
		quizForm.setQuestion(quiz.getQuestion());
		quizForm.setAnswer(quiz.getAnswer());
		quizForm.setAuthor(quiz.getAuthor());
		//更新モードに変更
		quizForm.setNewQuiz(false);
		return quizForm;
	}
	
	@GetMapping("/play")
	public String showQuiz(
			QuizForm quizForm,
			Model model
			) {
		Optional<Quiz> quizOpt = service.selectOneRandomQuiz();
		
		if(quizOpt.isPresent()) {
			Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
			quizForm = quizFormOpt.get();
		} else {
			model.addAttribute("msg", "問題が登録されていません。");
			return "play";
		}
		
		model.addAttribute("quizForm", quizForm);
		return "play";
	}
	
	@PostMapping("/check")
	public String answer(
			QuizForm quizForm,
			@RequestParam Boolean answer,
			Model model
			) {
		if(service.checkQuiz(quizForm.getId(), answer)) {
			model.addAttribute("msg", "正解です！");
		} else {
			model.addAttribute("msg", "残念、不正解です…");
		}
		
		return "answer";
	}
	

}
