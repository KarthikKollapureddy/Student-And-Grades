package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.HistoryGradeDao;
import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.*;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(value = "/application-test.properties")
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
    @Value("${sql.script.create.student}")
    private String sqlCreateStudent;
    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;
    @Value("${sql.script.create.mathGrade}")
    private String sqlCreateMathGrade;
    @Value("${sql.script.create.scienceGrade}")
    private String sqlCreateScienceGrade;
    @Value("${sql.script.create.historyGrade}")
    private String sqlCreateHistoryGrade;
    @Value("${sql.script.delete.mathGrade}")
    private String sqlDeleteMathGrade;
    @Value("${sql.script.delete.scienceGrade}")
    private String sqlDeleteScienceGrade;
    @Value("${sql.script.delete.historyGrade}")
    private String sqlDeleteHistoryGrade;
    @BeforeEach
    public void setupDataBase(){
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }
    @AfterEach
    public void cleanUpAfterTransaction(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
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
    public void test_isStudentNotNull(){
        assertTrue(studentAndGradeService.isStudentNotNull(1));
        assertFalse(studentAndGradeService.isStudentNotNull(0));
    }
    @DisplayName("Test to Delete Student")
    @Test
    public void test_DeleteStudent(){
        Optional<CollegeStudent> deletedStudent = studentDao.findById(1);
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGrade = scienceGradesDao.findById(1);
        Optional<HistoryGrade> historyGrade = historyGradeDao.findById(1);
//        check if student already exists.
        assertTrue(deletedStudent.isPresent(),"Student should be present");
        assertTrue(mathGrade.isPresent());
        assertTrue(scienceGrade.isPresent());
        assertTrue(historyGrade.isPresent());
//        check if student is deleted after Deleting from DB
        studentAndGradeService.deleteStudentById(1);

        deletedStudent = studentDao.findById(1);
        mathGrade = mathGradeDao.findById(1);
        scienceGrade = scienceGradesDao.findById(1);
        historyGrade = historyGradeDao.findById(1);
        assertFalse(deletedStudent.isPresent(),"Student should not be present");
        assertFalse(mathGrade.isPresent());
        assertFalse(scienceGrade.isPresent());
        assertFalse(historyGrade.isPresent());
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

        assertEquals(2,((Collection<MathGrade>) mathGrades).size());
        assertEquals(2,((Collection<ScienceGrade>) scienceGrades).size());
        assertEquals(2,((Collection<HistoryGrade>) historyGrades).size());
    }

    @Test
    public void test_failingStudentGrades_for_InvalidGrade_And_Invalid_Student(){
        assertFalse(studentAndGradeService.createGrade(-80.03,1,"History"));// invalid Grade
        assertFalse(studentAndGradeService.createGrade(1234,1,"History"));// invalid grade
        assertFalse(studentAndGradeService.createGrade(80.00,-2,"History"));// invalid studentId
        assertFalse(studentAndGradeService.createGrade(00.00,2,"Chemistry"));// invalid subject
    }

    @Test
    public void test_DeleteGrades(){
        // should rerun studentID id grade is successfully deleted associated with the student
        assertEquals(1, studentAndGradeService.deleteGrade(1,"math"),
                "Return studentID");
        assertEquals(1, studentAndGradeService.deleteGrade(1,"science"),
                "Return studentID");
        assertEquals(1, studentAndGradeService.deleteGrade(1,"history"),
                "Return studentID");
    }

    @Test
    public void test_failingDeleteGrades_invalidGradeId(){
        assertEquals(0, studentAndGradeService.deleteGrade(-1,"math"));
        assertEquals(0, studentAndGradeService.deleteGrade(-5,"science"));
        assertEquals(0, studentAndGradeService.deleteGrade(-2,"history"));
    }
    @Test
    public void test_failingDeleteGrades_invalidSubject(){
        assertEquals(0, studentAndGradeService.deleteGrade(-1,"math1"));
        assertEquals(0, studentAndGradeService.deleteGrade(-5,"science1"));
        assertEquals(0, studentAndGradeService.deleteGrade(-2,"history1"));
    }

    @Test
    public void test_StudentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService
                .getGradebookCollegeStudent(1);
        assertEquals("Eric",gradebookCollegeStudent.getFirstname());
        assertEquals("Roby",gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@yahoo.in",gradebookCollegeStudent.getEmailAddress());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());

    }
    @Test
    public void test_studentInformation_With_InvalidStudentID(){
        assertNull(studentAndGradeService
                .getGradebookCollegeStudent(33));
    }
}
