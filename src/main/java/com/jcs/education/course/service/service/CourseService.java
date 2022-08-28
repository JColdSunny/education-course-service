package com.jcs.education.course.service.service;

import com.jcs.education.common.proto.Course;
import com.jcs.education.course.service.entity.CourseTagEntity;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;
import com.jcs.education.course.service.proto.v1.GetCoursesResponse;
import com.jcs.education.course.service.repository.CourseRepository;
import com.jcs.education.tag.service.proto.v1.EducationTagServiceGrpc.EducationTagServiceBlockingStub;
import com.jcs.education.tag.service.proto.v1.GetTagsRequest;
import com.jcs.education.tag.service.proto.v1.GetTagsResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CourseService {
    final CourseRepository courseRepository;

    @GrpcClient("education-tag-service")
    EducationTagServiceBlockingStub tagService;

    public GetCoursesResponse getCourses(GetCoursesRequest request) {
        GetCoursesRequestValidator.validateRequest(request);

        List<Course> courses = courseRepository.findByCategoryId(request.getCategoryId()).stream()
                .map(courseEntity -> {
                    GetTagsResponse getTagsResponse = tagService.getTags(GetTagsRequest.newBuilder()
                            .addAllTagIds(courseEntity.getCourseTagEntityList().stream()
                                    .map(CourseTagEntity::getTagId)
                                    .toList())
                            .build());

                    return Course.newBuilder()
                            .setCourseId(courseEntity.getId())
                            .setName(courseEntity.getName())
                            .addAllTags(getTagsResponse.getTagsList())
                            .build();
                })
                .toList();

        return GetCoursesResponse.newBuilder()
                .addAllCourses(courses)
                .build();
    }

}
