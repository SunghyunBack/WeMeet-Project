const Report = bulletinForm.querySelector('.report');

Report.addEventListener('click', e => {
    e.preventDefault();
    const index = Report.dataset.index;
    const confirmReport = confirm('신고하시겠습니까?'); // 확인 메시지 표시

    if (confirmReport) {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        xhr.open('POST', `./report?index=${index}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'success_report':
                            alert('신고가 되었습니다');
                            location.href = `/article/read?index=` + index;
                            break;
                        case 'failure':
                            alert('자신의 게시물에 신고를 할 수 없습니다');
                            break;
                        case 'failure_report':
                            alert('중복으로 신고를 할 수 없습니다');
                            break;
                        default:
                            alert('중복으로 게시물에 신고를 할 수 없습니다');
                    }
                } else {
                    alert('서버 통신에 실패했습니다');
                }
            }
        };
        xhr.send();
    }
});

//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//
