package com.studentGrades.springmvc.service;

import com.studentGrades.springmvc.dao.HistoryGradeDao;
import com.studentGrades.springmvc.dao.MathGradeDao;
import com.studentGrades.springmvc.dao.ScienceGradeDao;
import com.studentGrades.springmvc.dao.StudentDao;
import com.studentGrades.springmvc.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private StudentGrades studentGrades;
    @Override
    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDao.save(student);
        System.out.println("Student saved to DB!");
    }

    @Override
    public boolean isStudentNotNull(int studentID) {
        Optional<CollegeStudent> student = studentDao.findById(studentID);
        return student.isPresent();
    }

    @Override
    public void deleteStudentById(int studentID) {
        if(isStudentNotNull(studentID)){
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
    public Gradebook getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();

        Iterable<MathGrade> mathGrades = mathGradeDao.findAll();

        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findAll();

        Iterable<HistoryGrade> historyGrades = historyGradeDao.findAll();

        Gradebook gradebook = new Gradebook();

        for (CollegeStudent collegeStudent : collegeStudents) {
            List<Grade> mathGradesPerStudent = new ArrayList<>();
            List<Grade> scienceGradesPerStudent = new ArrayList<>();
            List<Grade> historyGradesPerStudent = new ArrayList<>();

            for (MathGrade grade : mathGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    mathGradesPerStudent.add(grade);
                }
            }
            for (ScienceGrade grade : scienceGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    scienceGradesPerStudent.add(grade);
                }
            }

            for (HistoryGrade grade : historyGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    historyGradesPerStudent.add(grade);
                }
            }

            studentGrades.setMathGradeResults(mathGradesPerStudent);
            studentGrades.setScienceGradeResults(scienceGradesPerStudent);
            studentGrades.setHistoryGradeResults(historyGradesPerStudent);

            GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(collegeStudent.getId(), collegeStudent.getFirstname(), collegeStudent.getLastname(),
                    collegeStudent.getEmailAddress(), studentGrades);

            gradebook.getStudents().add(gradebookCollegeStudent);
        }

        return gradebook;
    }

    @Override
    public boolean createGrade(double grade, int studentId, String subject) {
        if(!isStudentNotNull(studentId)){
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

    @Override
    public GradebookCollegeStudent getGradebookCollegeStudent(int studentId) {
        Optional<CollegeStudent> student = studentDao.findById(studentId);
        if (student.isPresent()) {
            Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(studentId);
            Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(studentId);
            Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(studentId);

            List<Grade> mathGradesList = new ArrayList<>();
            List<Grade> scienceGradesList = new ArrayList<>();
            List<Grade> historyGradesList = new ArrayList<>();

            mathGrades.forEach(mathGradesList::add);
            scienceGrades.forEach(scienceGradesList::add);
            historyGrades.forEach(historyGradesList::add);

            studentGrades.setMathGradeResults(mathGradesList);
            studentGrades.setScienceGradeResults(scienceGradesList);
            studentGrades.setHistoryGradeResults(historyGradesList);

            GradebookCollegeStudent collegeStudent = new GradebookCollegeStudent(
                    studentId,
                    student.get().getFirstname(),
                    student.get().getLastname(),
                    student.get().getEmailAddress(),
                    studentGrades);

            return collegeStudent;
        }
        return null;
    }

    @Override
    public void configureStudentInformation(int studentID, Model m) {
        GradebookCollegeStudent studentEntity = getGradebookCollegeStudent(studentID);
        m.addAttribute("student",studentEntity);
        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getMathGradeResults()
            ));
        }else
            m.addAttribute("mathAverage", "N/A");
        if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getScienceGradeResults()
            ));
        }else
            m.addAttribute("scienceAverage", "N/A");
        if(studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0){
            m.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getHistoryGradeResults()
            ));
        }else
            m.addAttribute("historyAverage", "N/A");
    }
}
