package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.service.StudentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(value = "/application.properties")
@SpringBootTest
public class StudentAndGradesServiceTest {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private JdbcTemplate jdbc;
    @BeforeEach
    public void setupDataBase(){
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) "+
                "values(1, 'Eric', 'Roby', 'eric.roby@yahoo.in')");
    }
    @AfterEach
    public void cleanUpAfterTransaction(){
        jdbc.execute("DELETE FROM student");
    }
    @DisplayName("Test create student")
    @Test
    public void createStudentService(){
        studentService.createStudent("Eric", "Eric", "eric@yahoo.in");
        CollegeStudent student = studentDao.findByEmailAddress("eric@yahoo.in");
        assertEquals("eric@yahoo.in", student.getEmailAddress(),
                "email of the saved student must be same!");
    }
    @DisplayName("Test Student is Null")
    @Test
    public void test_isStudentNull(){
        assertTrue(studentService.isStudentNull(1));
        assertFalse(studentService.isStudentNull(0));
    }
    @DisplayName("Test to Delete Student")
    @Test
    public void test_DeleteStudent(){
        Optional<CollegeStudent> deletedStudent = studentDao.findById(1);
//        check if student already exists.
        assertTrue(deletedStudent.isPresent(),"Student should be present");
//        check if student is deleted after Deleting from DB
        studentDao.deleteById(1);
        deletedStudent = studentDao.findById(1);
        assertFalse(deletedStudent.isPresent(),"Student should not be present");

    }
}
