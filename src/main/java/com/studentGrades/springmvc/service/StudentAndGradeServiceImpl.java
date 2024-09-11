package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.MathGrade;
import com.studentGrades.springmvc.models.ScienceGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeServiceImpl implements StudentAndGradeService {
    @Autowired
    StudentDao studentDao;
    @Autowired
    @Qualifier("mathGrades")
    MathGrade mathGrade;
    @Autowired
    private MathGradeDao mathGradeDao;
    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;
    @Autowired
    private ScienceGradeDao scienceGradeDao;
    @Override
    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDao.save(student);
        System.out.println("Student saved to DB!");
    }

    @Override
    public boolean isStudentNull(int studentID) {
        Optional<CollegeStudent> student = studentDao.findById(studentID);
        return student.isPresent();
    }

    @Override
    public void deleteStudentById(int studentID) {
        if(isStudentNull(studentID)){
            studentDao.deleteById(studentID);
        }
    }

    @Override
    public Iterable<CollegeStudent> getGradeBook() {
        return studentDao.findAll();
    }

    @Override
    public boolean createGrade(double grade, int studentId, String subject) {
        if(!isStudentNull(studentId)){
            return false;
        }
        if(grade >= 0 && grade < 100){
            if (subject.equalsIgnoreCase("Math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDao.save(mathGrade);
                return true;
            } else if (subject.equalsIgnoreCase("Science")) {
                scienceGrade.setId(0);
                scienceGrade.setStudentId(1);
                scienceGrade.setGrade(grade);
                scienceGradeDao.save(scienceGrade);
                return true;
            }
        }
        return false;
    }
}
