package com.jcs.education.course.service.repository;

import com.jcs.education.course.service.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    List<CourseEntity> findByCategoryId(Integer categoryId);

}
