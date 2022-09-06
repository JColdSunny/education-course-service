package com.jcs.education.course.service.api;

import com.jcs.education.course.service.proto.v1.EducationCourseServiceGrpc;
import com.jcs.education.course.service.proto.v1.GetCourseDetailsRequest;
import com.jcs.education.course.service.proto.v1.GetCourseDetailsResponse;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;
import com.jcs.education.course.service.proto.v1.GetCoursesResponse;
import com.jcs.education.course.service.service.CourseService;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EducationCourseService extends EducationCourseServiceGrpc.EducationCourseServiceImplBase {
    CourseService courseService;

    @Override
    public void getCourses(GetCoursesRequest request, StreamObserver<GetCoursesResponse> responseObserver) {
        GetCoursesResponse response = courseService.getCourses(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCourseDetails(GetCourseDetailsRequest request, StreamObserver<GetCourseDetailsResponse> responseObserver) {
        GetCourseDetailsResponse response = courseService.getCourseUnits(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
