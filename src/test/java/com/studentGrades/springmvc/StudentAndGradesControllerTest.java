package com.studentGrades.springmvc;

import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.GradebookCollegeStudent;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class StudentAndGradesControllerTest {
    private static MockHttpServletRequest request;
    @Mock
    private StudentAndGradeService studentAndGradeService;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentDao studentDao;

    @BeforeAll
    public static void setup(){
        request =  new MockHttpServletRequest();
        request.setParameter("firstname", "John");
        request.setParameter("lastname", "Ericsson");
        request.setParameter("emailAddress", "John@gmail.com");
    }

    @BeforeEach
    public void setupDataBase(){
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) "+
                "values(1, 'Eric', 'Roby', 'eric.roby@yahoo.in')");
    }
    @AfterEach
    public void cleanUpAfterTransaction(){
        jdbc.execute("DELETE FROM student");
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
        MvcResult mvcResult = this.mockMvc.perform(delete("/delete/student/{id}" ,1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mv, "index");

        assertFalse(studentDao.findById(1).isPresent());

    }
}
