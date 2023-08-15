package com.bsh.projectwemeet.enums;

public enum CreateCommentResult {
    FAILURE, // 실패
    FAILURE_NOT_LOGIN, //로그인하지 않았을 때
    SUCCESS, // 글을 작성한 사람과 댓글을 작성한 사람이 다를 때
    SUCCESS_SAME // 글을 작성한 사람과 댓글을 쓰는 사람이 같을 때
}
