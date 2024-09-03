package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    StudentDao studentDao;
    @Override
    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDao.save(student);
        System.out.println("Student saved to DB!");
    }
}
