package com.bsh.projectwemeet.enums;

public enum DeleteCommentResult {
    FAILURE, //실패
    FAILURE_NO_AUTHORITY, //권한이 없을 경우
    FAILURE_DELETED, //이미 삭제된 댓글
    FAILURE_NOT_LOGIN, //로그인 상태가 아닐 경우
    SUCCESS
}
