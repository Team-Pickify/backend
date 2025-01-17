package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorResponseDTO;
import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request){
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return buildConstraintViolationResponse(e, ErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });

        return buildInvalidMethodArgumentResponse(e,HttpHeaders.EMPTY, ErrorStatus.valueOf("_BAD_REQUEST"), request, errors);
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity handleOnThrowException(GeneralException generalException, HttpServletRequest request) {
        ErrorResponseDTO errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return buildOnThrowExceptionResponse(generalException,errorReasonHttpStatus,null,request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnexpectedException(Exception e, WebRequest request) {
        logger.error(e);
        return buildUnexpectedExceptionResponse(e, ErrorStatus._INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(),request, e.getMessage());
    }

    private ResponseEntity<Object> buildConstraintViolationResponse(Exception e, ErrorStatus errorCommonStatus,
                                                                     HttpHeaders headers, WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), null);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }

    private ResponseEntity<Object> buildInvalidMethodArgumentResponse(Exception e, HttpHeaders headers, ErrorStatus errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),errorCommonStatus.getMessage(),errorArgs);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }

    private ResponseEntity<Object> buildUnexpectedExceptionResponse(Exception e, ErrorStatus errorCommonStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),errorCommonStatus.getMessage(),errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> buildOnThrowExceptionResponse(Exception e, ErrorResponseDTO reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(),reason.getMessage(),null);
        WebRequest webRequest = new ServletWebRequest(request);

        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }
}