package com.studentGrades.springmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentGrades.springmvc.dao.HistoryGradeDao;
import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "./application.properties")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class GradeBookRestControllerTest {
    private static MockHttpServletRequest request;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CollegeStudent student;
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
    public static final MediaType APPLICATION_JSON_UTFS = MediaType.APPLICATION_JSON;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "John");
        request.setParameter("lastname", "Ericsson");
        request.setParameter("emailAddress", "John@gmail.com");
    }

    @BeforeEach
    public void setupDataBase() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }

    @Test
    public void test_getStudents_HttpRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTFS))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void test_createStudent_HttpRequest() throws Exception {
        student.setFirstname("first");
        student.setLastname("Last");
        student.setEmailAddress("myEmail@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/")
                        .contentType(APPLICATION_JSON_UTFS)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTFS))
                .andExpect(jsonPath("$", hasSize(2)));
//        check in DB
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("myEmail@gmail.com");
        assertNotNull(verifyStudent);
    }
    @Test
    public void test_DeleteStudent_HttpRequest() throws Exception{
//        initial check if student is present
        assertTrue(studentDao.findById(1).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTFS))
                .andExpect(jsonPath("$",hasSize(0)));
//        check in db
        Iterable<CollegeStudent> students = studentDao.findAll();
        assertFalse(students.iterator().hasNext());
        assertFalse(studentDao.findById(1).isPresent());
    }
    @Test
    public void test_DeleteStudent_InvalidId_HttpRequest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}",199))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status",is(404)))
                .andExpect(jsonPath("$.message",is("Student or Grade was not found"))
                );

    }
    @Test
    public void test_StudentInformation_HttpRequest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/studentInformation/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTFS))
                .andExpect(jsonPath("$.emailAddress",is("eric.roby@yahoo.in")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults",hasSize(1)))
                .andExpect(jsonPath("$.studentGrades.scienceGradeResults",hasSize(1)))
                .andExpect(jsonPath("$.studentGrades.historyGradeResults",hasSize(1)))
                .andExpect(jsonPath("$.fullName",is("Eric Roby"))
                );
    }
    @Test
    public void test_StudentInformation_InvalidId_HttpRequest() throws Exception{
        assertFalse(studentDao.findById(199).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/studentInformation/{id}",199))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status",is(404)))
                .andExpect(jsonPath("$.message",is("Student or Grade was not found"))
                );
    }

    @AfterEach
    public void cleanUpAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}
