package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.GradebookCollegeStudent;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = "./application.properties")
public class StudentAndGradesControllerTest {
    private static MockHttpServletRequest request;
    @Mock
    private StudentAndGradeService studentAndGradeService;
    @Value("${sql.script.create.student}")
    private String sqlCreateStudent;
    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentDao studentDao;
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
    @Autowired
    private StudentAndGradeService studentService;

    @BeforeAll
    public static void setup(){
        request =  new MockHttpServletRequest();
        request.setParameter("firstname", "John");
        request.setParameter("lastname", "Ericsson");
        request.setParameter("emailAddress", "John@gmail.com");
    }

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

    @DisplayName("Test student and grades controller")
    @Test
    public void test_StudentAndGradesController() throws Exception {
        GradebookCollegeStudent studentOne = new GradebookCollegeStudent("Eric","Phil",
                "eric@gmail.com");
        GradebookCollegeStudent studentTwo = new GradebookCollegeStudent("John","Storm",
                "john@gmail.com");
        List<CollegeStudent> collegeStudentList = new ArrayList<>(
                List.of(studentOne, studentTwo)
        );
        when(studentAndGradeService.getGradeBook()).thenReturn(collegeStudentList);
        assertIterableEquals(collegeStudentList, studentAndGradeService.getGradeBook());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = result.getModelAndView();
        assertNotNull(mv);
        ModelAndViewAssert.assertViewName(mv, "index");
    }
    @Test
    public void test_postRequest() throws Exception{
        CollegeStudent student = new CollegeStudent("Eric",
                "John", "eric@gmail.com");
        List<CollegeStudent> collegeStudentList = new ArrayList<>(List.of(student));
        when(studentAndGradeService.getGradeBook()).thenReturn(collegeStudentList);
        MvcResult mv = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndViewAssert.assertViewName(mv.getModelAndView(), "index");

        assertIterableEquals(collegeStudentList,studentAndGradeService.getGradeBook());

        CollegeStudent verifyCollegeStudent = studentDao
                .findByEmailAddress(request.getParameter("emailAddress"));
        assertNotNull(verifyCollegeStudent);
        assertEquals("John@gmail.com",
                verifyCollegeStudent.getEmailAddress(),
                "Student email is same as expected !");
    }

    @Test
    public void test_DeleteStudent() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = this.mockMvc.perform(get("/delete/student/{id}" ,1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mv, "index");

        assertFalse(studentDao.findById(1).isPresent());

    }

    @Test
    public void test_DeleteHttpRequestWithErrorPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/delete/student/{id}", 0))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mv, "error");
    }

    @Test
    public void test_getStudentInformation_Positive() throws Exception{
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}",1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mv,"studentInformation");
    }
    @Test
    public void test_getStudentInformation_Error() throws Exception{
        assertFalse(studentDao.findById(100).isPresent());
        MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}",100))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mv,"error");
    }

    @Test
    public void test_createGrades_HttpRequest() throws Exception{
        assertTrue(studentDao.findById(1).isPresent());
        GradebookCollegeStudent student = studentService.getGradebookCollegeStudent(1);
        assertEquals(1,student.getStudentGrades().getMathGradeResults().size());
        MvcResult mvcResult = mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("studentId","1")
                .param("grade","87.9")
                .param("gradeType","math")
        ).andExpect(status().isOk()).andReturn();
        student = studentService.getGradebookCollegeStudent(1);
        assertEquals(2,student.getStudentGrades().getMathGradeResults().size());

    }
}
