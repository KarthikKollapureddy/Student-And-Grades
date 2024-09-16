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
import java.util.stream.Stream;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentAndGradeService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudentList = studentAndGradeService.getGradeBook();
        m.addAttribute("students", collegeStudentList);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (!studentAndGradeService.isStudentNotNull(id))
            return "error";
        studentAndGradeService.configureStudentInformation(id, m);

        return "studentInformation";
    }

    @PostMapping()
    public String createStudent(@ModelAttribute CollegeStudent student, Model m) {
        studentAndGradeService.createStudent(student.getFirstname(),
                student.getLastname(),
                student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {
        System.out.println("delete student!");
        if (!studentAndGradeService.isStudentNotNull(id)) {
            System.out.println("deleted");
            return "error";
        } else {
            studentAndGradeService.deleteStudentById(id);
            Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
            m.addAttribute("students", collegeStudents);
            return "index";
        }
    }

    @PostMapping("/grades")
    public String addGrades(@RequestParam int studentId,
                            @RequestParam double grade,
                            @RequestParam String gradeType,
                            Model m) {
        if (studentAndGradeService.isStudentNotNull(studentId)) {
            studentAndGradeService.createGrade(grade, studentId, gradeType);
            studentAndGradeService.configureStudentInformation(studentId, m);
            return "studentInformation";
        }
        return "error";

    }
    @GetMapping("/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable int id,
                              @PathVariable String gradeType,
                              Model m){
        int studentID = studentAndGradeService.deleteGrade(id,gradeType);
        if(studentID == 0)
            return "error";
        studentAndGradeService.configureStudentInformation(studentID, m);
        return "studentInformation";
    }
}
