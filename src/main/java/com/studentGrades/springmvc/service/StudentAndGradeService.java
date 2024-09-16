package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.GradebookCollegeStudent;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public interface StudentAndGradeService {

    void createStudent(String firstName, String lastName, String email);

    boolean isStudentNotNull(int studentID);

    void deleteStudentById(int studentID);

    Iterable<CollegeStudent> getGradeBook();

    boolean createGrade(double grade, int studentId, String subject);

    int deleteGrade(int gradeId, String subject);

    GradebookCollegeStudent getGradebookCollegeStudent(int studentId);

    void configureStudentInformation(int studentID, Model m);
}
