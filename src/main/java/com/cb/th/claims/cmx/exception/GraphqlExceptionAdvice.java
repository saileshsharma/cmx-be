package com.cb.th.claims.cmx.exception;


import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice(annotations = Controller.class)
public class GraphqlExceptionAdvice {

    @GraphQlExceptionHandler(BusinessException.class)
    public GraphQLError handleBusiness(BusinessException ex, DataFetchingEnvironment env) {
        String errorId = UUID.randomUUID().toString();
        return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).extensions(Map.of("code", ex.getCode(), "httpStatus", ex.getHttpStatus(), "errorId", errorId)).build();
    }

    @GraphQlExceptionHandler(ConstraintViolationException.class)
    public GraphQLError handleConstraint(ConstraintViolationException ex, DataFetchingEnvironment env) {
        List<Map<String, String>> fieldErrors = ex.getConstraintViolations().stream().map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage())).toList();

        return GraphqlErrorBuilder.newError(env).message("Validation failed").extensions(Map.of("code", "VALIDATION_FAILED", "httpStatus", 400, "fieldErrors", fieldErrors)).build();
    }

    @GraphQlExceptionHandler(Exception.class)
    public GraphQLError handleOther(Exception ex, DataFetchingEnvironment env) {
        String errorId = UUID.randomUUID().toString();
        // log errorId + ex here if desired
        return GraphqlErrorBuilder.newError(env).message("Unexpected error. Contact support with errorId.").extensions(Map.of("code", "INTERNAL_ERROR", "httpStatus", 500, "errorId", errorId)).build();
    }
}
