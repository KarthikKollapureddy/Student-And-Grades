package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.HistoryGradeDao;
import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.HistoryGrade;
import com.studentGrades.springmvc.models.MathGrade;
import com.studentGrades.springmvc.models.ScienceGrade;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(value = "/application.properties")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentAndGradesServiceTest {
    @Autowired
    private StudentAndGradeService studentAndGradeService;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private MathGradeDao mathGradeDao;
    @Autowired
    private ScienceGradeDao scienceGradesDao;
    @Autowired
    private HistoryGradeDao historyGradeDao;
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
        studentAndGradeService.createStudent("Eric", "Eric", "eric@yahoo.in");
        CollegeStudent student = studentDao.findByEmailAddress("eric@yahoo.in");
        assertEquals("eric@yahoo.in", student.getEmailAddress(),
                "email of the saved student must be same!");
    }
    @DisplayName("Test Student is Null")
    @Test
    public void test_isStudentNull(){
        assertTrue(studentAndGradeService.isStudentNull(1));
        assertFalse(studentAndGradeService.isStudentNull(0));
    }
    @DisplayName("Test to Delete Student")
    @Test
    public void test_DeleteStudent(){
        Optional<CollegeStudent> deletedStudent = studentDao.findById(1);
//        check if student already exists.
        assertTrue(deletedStudent.isPresent(),"Student should be present");
//        check if student is deleted after Deleting from DB
        studentAndGradeService.deleteStudentById(1);
        deletedStudent = studentDao.findById(1);
        assertFalse(deletedStudent.isPresent(),"Student should not be present");
    }
    @DisplayName("Test number of Students in DB")
    @Test
    @Order(1)
    @Sql("/insertData.sql") // inserts and executes sql
//    if we want to add multiple entries for this particular test we can make use of
//    @Sql and pass in our sql file( create a sql file under resources )
    public void test_getGradeBook(){
        Iterable<CollegeStudent> iterable = studentAndGradeService.getGradeBook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for(CollegeStudent student : iterable){
            collegeStudents.add(student);
        }
        assertEquals(5,collegeStudents.size(),
                "No. of students should equals 1 as we only have 1 test user");
    }

    @Test
    public void test_StudentGrades(){
        assertTrue(studentAndGradeService.createGrade(86.87, 1, "Math"));
        assertTrue(studentAndGradeService.createGrade(84.43,1,"Science"));
        assertTrue(studentAndGradeService.createGrade(80.03,1,"History"));

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        assertTrue(mathGrades.iterator().hasNext(),"Student has math grades!");
        assertTrue(scienceGrades.iterator().hasNext(),"Student has science grades!");
        assertTrue(historyGrades.iterator().hasNext(),"Student has history grades!");
    }

    @Test
    public void test_failingStudentGrades_for_InvalidGrade_And_Invalid_Student(){
        assertFalse(studentAndGradeService.createGrade(-80.03,1,"History"));// invalid Grade
        assertFalse(studentAndGradeService.createGrade(1234,1,"History"));// invalid grade
        assertFalse(studentAndGradeService.createGrade(80.00,-2,"History"));// invalid studentId
        assertFalse(studentAndGradeService.createGrade(00.00,2,"Chemistry"));// invalid subject
    }
}
