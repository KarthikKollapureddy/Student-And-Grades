package com.studentGrades.springmvc.dao;

import com.studentGrades.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradeDao extends CrudRepository<MathGrade, Integer> {
    Iterable<MathGrade> findGradeByStudentId(int id);
}
