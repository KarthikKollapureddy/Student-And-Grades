package com.studentGrades.springmvc.controller;

import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.Grade;
import com.studentGrades.springmvc.models.Gradebook;
import com.studentGrades.springmvc.models.GradebookCollegeStudent;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentAndGradeService;


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudentList = studentAndGradeService.getGradeBook();
		m.addAttribute("Students",collegeStudentList);
		return "index";
	}

	@GetMapping("/studentInformation/{id}")
		public String studentInformation(@PathVariable int id, Model m) {
		if(!studentAndGradeService.isStudentNotNull(id))
				return "error";
		GradebookCollegeStudent studentEntity = studentAndGradeService.getGradebookCollegeStudent(id);
		m.addAttribute("student",studentEntity);
		if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
			m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getMathGradeResults()
			));
		}else
			m.addAttribute("mathAverage", "N/A");
		if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
			m.addAttribute("scienceAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getScienceGradeResults()
			));
		}else
			m.addAttribute("scienceAverage", "N/A");
		if(studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0){
			m.addAttribute("HistoryAverage", studentEntity.getStudentGrades().findGradePointAverage(
					studentEntity.getStudentGrades().getHistoryGradeResults()
			));
		}else
			m.addAttribute("HistoryAverage", "N/A");

		return "studentInformation";
		}

	@PostMapping()
	public String createStudent(@ModelAttribute CollegeStudent student, Model m){
		studentAndGradeService.createStudent(student.getFirstname(),
				student.getLastname(),
				student.getEmailAddress());
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}
	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id, Model m){
		System.out.println("delete student!");
		if(!studentAndGradeService.isStudentNotNull(id))
		{System.out.println("deleted");
			return "error";}
		else{
		studentAndGradeService.deleteStudentById(id);
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
		m.addAttribute("students",collegeStudents);
		return "index";
		}
	}
	@PostMapping("/grades")
	public String addGrades(@RequestParam int studentId,
							@RequestParam double grade,
							@RequestParam String gradeType,
							Model m){
		if(studentAndGradeService.isStudentNotNull(studentId)){
			studentAndGradeService.createGrade(grade,studentId,gradeType);
			GradebookCollegeStudent studentEntity = studentAndGradeService.getGradebookCollegeStudent(studentId);
			if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
				m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
						studentEntity.getStudentGrades().getMathGradeResults()
				));
			}else
				m.addAttribute("mathAverage", "N/A");
			if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
				m.addAttribute("scienceAverage", studentEntity.getStudentGrades().findGradePointAverage(
						studentEntity.getStudentGrades().getScienceGradeResults()
				));
			}else
				m.addAttribute("scienceAverage", "N/A");
			if(studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0){
				m.addAttribute("HistoryAverage", studentEntity.getStudentGrades().findGradePointAverage(
						studentEntity.getStudentGrades().getHistoryGradeResults()
				));
			}else
				m.addAttribute("HistoryAverage", "N/A");
			return "studentInformation";
		}
		return "error";

	}
}
