package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(value = "/application.properties")
@SpringBootTest
public class StudentAndGradesServiceTest {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentDao studentDao;
    @DisplayName("Test create student")
    @Test
    public void createStudentService(){
        studentService.createStudent("Eric", "Eric", "eric@yahoo.in");
        CollegeStudent student = studentDao.findByEmailAddress("eric@yahoo.in");
        assertEquals("eric@yahoo.in", student.getEmailAddress(),
                "email of the saved student must be same!");
    }
}
