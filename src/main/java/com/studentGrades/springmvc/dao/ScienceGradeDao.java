package com.studentGrades.springmvc.dao;

import com.studentGrades.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Integer> {
    Iterable<ScienceGrade> findGradeByStudentId(int id);
}
