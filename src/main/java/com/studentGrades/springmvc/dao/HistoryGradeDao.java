package com.studentGrades.springmvc.dao;

import com.studentGrades.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {
    Iterable<HistoryGrade> findGradeByStudentId(int id);
    void deleteGradeByStudentId(int id);
}
