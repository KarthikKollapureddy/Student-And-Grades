package com.studentGrades.springmvc.service;

import org.springframework.stereotype.Service;

@Service
public interface StudentService {

    void createStudent(String firstName, String lastName, String email);
}
