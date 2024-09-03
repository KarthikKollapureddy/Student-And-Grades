package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.models.CollegeStudent;
import org.springframework.stereotype.Service;

@Service
public interface StudentAndGradeService {

    void createStudent(String firstName, String lastName, String email);

    boolean isStudentNull(int studentID);

    void deleteStudentById(int studentID);

    Iterable<CollegeStudent> getGradeBook();
}
