package com.studentGrades.springmvc;

import com.studentGrades.springmvc.models.CollegeStudent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(value = "/application.properties")
@SpringBootTest
public class StudentAndGradesServiceTest {
    @Test
    public void createStudentService(){
        studentService.createStudent("Eric", "Eric", "eric@yahoo.in");
        CollegeStudent student = studentDao.findByEmail("eric@yahoo.in");
        assertEquals("eric@yahoo.in", student.getEmailAddress(),
                "email of the saved student must be same!");
    }
}
