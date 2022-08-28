package com.jcs.education.course.service.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "courses")
@EqualsAndHashCode(of = "id")
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "course_name")
    private String name;

    private Integer categoryId;

    @JoinColumn(name = "courseId")
    @OneToMany(fetch = FetchType.EAGER)
    private Set<CourseTagEntity> courseTagEntityList;
}
