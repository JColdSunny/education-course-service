package com.jcs.education.course.service.service;

import com.jcs.education.course.service.exception.RequestValidationException;
import com.jcs.education.course.service.proto.v1.GetCourseDetailsRequest;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;

public class CourseServiceValidator {

    private CourseServiceValidator() {
        throw new UnsupportedOperationException("CourseServiceValidator is a static utility class");
    }

    static void validateGetCoursesRequest(GetCoursesRequest request) {
        if (request.getCategoryId() == 0) {
            throw new RequestValidationException("category_id must be specified");
        }
    }

    static void validateGetCourseDetailsRequest(GetCourseDetailsRequest request) {
        if (request.getCourseId() == 0) {
            throw new RequestValidationException("course_id must be specified");
        }
    }

}
