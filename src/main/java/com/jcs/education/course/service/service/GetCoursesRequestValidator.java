package com.jcs.education.course.service.service;

import com.jcs.education.course.service.exception.RequestValidationException;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;

public class GetCoursesRequestValidator {

    private GetCoursesRequestValidator() {
        throw new UnsupportedOperationException("GetCoursesRequestValidator is a static utility class");
    }

    static void validateRequest(GetCoursesRequest request) {
        if (request.getCategoryId() == 0) {
            throw new RequestValidationException("category_id must be specified");
        }
    }

}
