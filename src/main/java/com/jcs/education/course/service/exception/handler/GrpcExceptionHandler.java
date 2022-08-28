package com.jcs.education.course.service.exception.handler;

import com.google.rpc.Status;
import com.jcs.education.course.service.exception.EntityNotFoundException;
import com.jcs.education.course.service.exception.RequestValidationException;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import net.devh.boot.grpc.server.advice.GrpcAdvice;

import static com.google.rpc.Code.INVALID_ARGUMENT_VALUE;
import static com.google.rpc.Code.NOT_FOUND_VALUE;

@GrpcAdvice
public class GrpcExceptionHandler {

    @net.devh.boot.grpc.server.advice.GrpcExceptionHandler(RequestValidationException.class)
    public StatusRuntimeException handleValidationException(RequestValidationException e) {
        Status invalidAgrStatus = Status.newBuilder()
                .setCode(INVALID_ARGUMENT_VALUE)
                .setMessage(e.getMessage())
                .build();
        return StatusProto.toStatusRuntimeException(invalidAgrStatus);
    }

    @net.devh.boot.grpc.server.advice.GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleNotFoundException(EntityNotFoundException e) {
        Status notFoundStatus = Status.newBuilder()
                .setCode(NOT_FOUND_VALUE)
                .setMessage(e.getMessage())
                .build();
        return StatusProto.toStatusRuntimeException(notFoundStatus);
    }
}
