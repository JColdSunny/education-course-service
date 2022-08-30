package com.jcs.education.course.service.service;

import com.jcs.education.common.proto.Course;
import com.jcs.education.common.proto.Tag;
import com.jcs.education.course.service.entity.CourseEntity;
import com.jcs.education.course.service.entity.CourseTagEntity;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;
import com.jcs.education.course.service.proto.v1.GetCoursesResponse;
import com.jcs.education.course.service.repository.CourseRepository;
import com.jcs.education.tag.service.proto.v1.EducationTagServiceGrpc.EducationTagServiceBlockingStub;
import com.jcs.education.tag.service.proto.v1.GetTagsRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        List<CourseEntity> courseEntityList = courseRepository.findByCategoryId(request.getCategoryId());
        Set<Integer> tagIds = courseEntityList.stream()
                .map(CourseEntity::getCourseTagEntityList)
                .flatMap(Collection::stream)
                .map(CourseTagEntity::getTagId)
                .collect(Collectors.toSet());

        Map<Integer, Tag> tagMap = tagService.getTags(GetTagsRequest.newBuilder().addAllTagIds(tagIds).build())
                .getTagsList().stream()
                .collect(Collectors.toMap(Tag::getTagId, Function.identity()));

        List<Course> courses = courseEntityList.stream()
                .map(courseEntity -> {
                    List<Tag> tags = courseEntity.getCourseTagEntityList().stream()
                            .map(courseTagEntity -> tagMap.get(courseTagEntity.getTagId()))
                            .toList();

                    return Course.newBuilder()
                            .setCourseId(courseEntity.getId())
                            .setName(courseEntity.getName())
                            .addAllTags(tags)
                            .build();
                })
                .toList();

        return GetCoursesResponse.newBuilder()
                .addAllCourses(courses)
                .build();
    }

}
