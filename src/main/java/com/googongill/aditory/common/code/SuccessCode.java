package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    /**
     * 200 OK
     */
    // User
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),
    GET_USERINFO_SUCCESS(HttpStatus.OK, "회원 정보 조회에 성공했습니다"),
    GET_PROFILE_IMAGE_SUCCESS(HttpStatus.OK, "프로필 사진 조회에 성공했습니다."),
    UPDATE_USER_SUCCESS(HttpStatus.OK, "회원 정보 수정에 성공했습니다."),
    UPDATE_PROFILE_IMAGE_SUCCESS(HttpStatus.OK, "프로필 사진 수정에 성공했습니다."),
    SIGNOUT_SUCCESS(HttpStatus.OK, "회원 탈퇴에 성공했습니다."),
    // Link
    GET_LINK_SUCCESS(HttpStatus.OK, "링크 조회에 성공했습니다."),
    GET_REMINDER_SUCCESS(HttpStatus.OK, "링크 리마인더 조회에 성공했습니다."),
    UPDATE_LINK_SUCCESS(HttpStatus.OK, "링크 수정에 성공했습니다."),
    DELETE_LINK_SUCCESS(HttpStatus.OK, "링크 삭제에 성공했습니다."),
    // Category
    GET_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 조회에 성공했습니다."),
    GET_CATEGORY_PUBLIC_LIST_SUCCESS(HttpStatus.OK, "공개 카테고리 목록 조회에 성공했습니다."),
    UPDATE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 수정에 성공했습니다."),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 삭제에 성공했습니다."),

    GET_MY_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "내 카테고리 목록 조회에 성공했습니다."),
    GET_PUBLIC_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "공개 카테고리 목록 조회에 성공했습니다."),
    GET_TODAY_PUBLIC_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "오늘의 추천 카테고리 목록 조회에 성공했습니다."),
    // CategoryLike
    DELETE_CATEGORY_LIKE_SUCCESS(HttpStatus.OK, "공개 카테고리 좋아요 삭제에 성공했습니다."),
    GET_LIKE_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "좋아요 한 카테고리 목록 조회에 성공했습니다."),
    // Move Link to another category
    MOVE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 이동에 성공했습니다."),
    GET_CATEGORY_LIKE_SUCCESS(HttpStatus.OK, "공개 카테고리 좋아요 조회에 성공했습니다."),
    //Search
    SEARCH_SUCCESS(HttpStatus.OK, "검색에 성공했습니다."),

    /**
     * 201 Created
     */
    // User
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),
    // Link
    SAVE_LINK_SUCCESS(HttpStatus.CREATED, "링크 저장에 성공했습니다."),
    // Category
    SAVE_CATEGORY_SUCCESS(HttpStatus.CREATED, "카테고리 저장에 성공했습니다."),
    COPY_CATEGORY_SUCCESS(HttpStatus.CREATED, "카테고리 복사에 성공했습니다."),
    SAVE_CATEGORY_LIKE_SUCCESS(HttpStatus.OK, "공개 카테고리 좋아요에 성공했습니다."),
    IMPORT_CATEGORY_SUCCESS(HttpStatus.CREATED, "카테고리 가져오기에 성공했습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
