package com.jcs.education.course.service.service;

import com.jcs.education.common.proto.Course;
import com.jcs.education.common.proto.Tag;
import com.jcs.education.course.service.entity.CourseEntity;
import com.jcs.education.course.service.entity.CourseTagEntity;
import com.jcs.education.course.service.exception.EntityNotFoundException;
import com.jcs.education.course.service.proto.v1.GetCourseDetailsRequest;
import com.jcs.education.course.service.proto.v1.GetCourseDetailsResponse;
import com.jcs.education.course.service.proto.v1.GetCoursesRequest;
import com.jcs.education.course.service.proto.v1.GetCoursesResponse;
import com.jcs.education.course.service.repository.CourseRepository;
import com.jcs.education.course.unit.service.proto.v1.EducationCourseUnitServiceGrpc.EducationCourseUnitServiceBlockingStub;
import com.jcs.education.course.unit.service.proto.v1.GetCourseUnitsRequest;
import com.jcs.education.course.unit.service.proto.v1.GetCourseUnitsResponse;
import com.jcs.education.utility.service.proto.v1.EducationUtilityServiceGrpc.EducationUtilityServiceBlockingStub;
import com.jcs.education.utility.service.proto.v1.GetCourseUtilitiesRequest;
import com.jcs.education.utility.service.proto.v1.GetCourseUtilitiesResponse;
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

    @GrpcClient("education-utility-service")
    EducationUtilityServiceBlockingStub utilityService;

    @GrpcClient("education-course-unit-service")
    EducationCourseUnitServiceBlockingStub courseUnitService;

    public GetCoursesResponse getCourses(GetCoursesRequest request) {
        CourseServiceValidator.validateGetCoursesRequest(request);

        List<CourseEntity> courseEntityList = courseRepository.findByCategoryId(request.getCategoryId());

        if (courseEntityList.isEmpty()) {
            return GetCoursesResponse.getDefaultInstance();
        }

        Set<Integer> tagIds = courseEntityList.stream()
                .map(CourseEntity::getCourseTagEntityList)
                .flatMap(Collection::stream)
                .map(CourseTagEntity::getTagId)
                .collect(Collectors.toSet());

        Map<Integer, Tag> tagMap = getCourseUtilities(tagIds)
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

    public GetCourseDetailsResponse getCourseUnits(GetCourseDetailsRequest request) {
        CourseServiceValidator.validateGetCourseDetailsRequest(request);

        CourseEntity courseEntity = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Failed to found course entity. id = " + request.getCourseId()));

        List<Integer> tagsIds = courseEntity.getCourseTagEntityList().stream()
                .map(CourseTagEntity::getTagId)
                .toList();

        GetCourseUtilitiesResponse getCourseUtilitiesResponse = getCourseUtilities(tagsIds);
        GetCourseUnitsResponse getCourseUnitsResponse = getCourseUnits(request.getCourseId());

        return GetCourseDetailsResponse.newBuilder()
                .setCourse(Course.newBuilder()
                        .setCourseId(courseEntity.getId())
                        .setName(courseEntity.getName())
                        .addAllTags(getCourseUtilitiesResponse.getTagsList())
                        .build())
                .addAllCourseUnits(getCourseUnitsResponse.getCourseUnitsList())
                .build();
    }

    private GetCourseUtilitiesResponse getCourseUtilities(Collection<Integer> tagsIds) {
        GetCourseUtilitiesRequest request = GetCourseUtilitiesRequest.newBuilder()
                .addAllTagIds(tagsIds)
                .build();

        return utilityService.getCourseUtilities(request);
    }

    private GetCourseUnitsResponse getCourseUnits(Integer courseId) {
        GetCourseUnitsRequest request = GetCourseUnitsRequest.newBuilder()
                .setCourseId(courseId)
                .build();

        return courseUnitService.getCourseUnits(request);
    }
}
