package com.studentGrades.springmvc;

import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.GradebookCollegeStudent;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class StudentAndGradesControllerTest {
    @Mock
    private StudentAndGradeService studentAndGradeService;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    private MockMvc mockMvc;

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
}
