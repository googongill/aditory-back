package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SearchErrorCode implements BusinessErrorCode {

        /**
        * 400 Bad Request
        */
        EMPTY_QUERY(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
        INVALID_SEARCH_TYPE(HttpStatus.BAD_REQUEST, "검색 타입이 잘못되었습니다."),
        INVALID_SEARCH_RESULT_TYPE(HttpStatus.BAD_REQUEST, "검색 결과 타입이 잘못되었습니다."),
        INVALID_CATEGORY_SCOPE(HttpStatus.BAD_REQUEST, "카테고리 범위가 잘못되었습니다."),

        /**
        * 404 Not Found
        */
        SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "검색 결과가 없습니다."),




        ;

        private final HttpStatus httpStatus;
        private final String message;

}
