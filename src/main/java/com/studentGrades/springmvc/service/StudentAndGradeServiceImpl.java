package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.dao.HistoryGradeDao;
import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.CollegeStudent;
import com.studentGrades.springmvc.models.HistoryGrade;
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
    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;
    @Autowired
    private HistoryGradeDao historyGradeDao;
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
            mathGradeDao.deleteGradeByStudentId(studentID);
            scienceGradeDao.deleteGradeByStudentId(studentID);
            historyGradeDao.deleteGradeByStudentId(studentID);
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
            } else if (subject.equalsIgnoreCase("History")) {
                historyGrade.setId(0);
                historyGrade.setStudentId(1);
                historyGrade.setGrade(grade);
                historyGradeDao.save(historyGrade);
                return true;
            }
        }
        return false;
    }

    @Override
    public int deleteGrade(int gradeId, String subject) {
        int studentId  = 0;
        if(subject.equalsIgnoreCase("Math")){
            Optional<MathGrade> mathGrade = mathGradeDao.findById(gradeId);
            if(mathGrade.isPresent()){
                studentId = mathGrade.get().getStudentId();
                mathGradeDao.deleteById(gradeId);
                return studentId;
            }
        } else if (subject.equalsIgnoreCase("Science")) {
            Optional<ScienceGrade> scienceGrade = scienceGradeDao.findById(gradeId);
            if(scienceGrade.isPresent()){
                studentId = scienceGrade.get().getStudentId();
                scienceGradeDao.deleteById(gradeId);
                return studentId;
            }
        }else if (subject.equalsIgnoreCase("History")) {
            Optional<HistoryGrade> historyGrade = historyGradeDao.findById(gradeId);
            if(historyGrade.isPresent()){
                studentId = historyGrade.get().getStudentId();
                historyGradeDao.deleteById(gradeId);
                return studentId;
            }
        }
        return studentId;
    }
}
