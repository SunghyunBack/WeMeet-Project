const deleteReport = bulletinForm.querySelector('.deleteReport');

deleteReport.addEventListener('click', e => {
    e.preventDefault();
    const index = deleteReport.dataset.index;
    const confirmCancel = confirm('신고를 취소하시겠습니까?'); // 확인 메시지 표시

    if (confirmCancel) {
        const xhr = new XMLHttpRequest();
        xhr.open('DELETE', `./report?index=${index}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'success_report':
                            alert('신고가 취소되었습니다');
                            location.href = `/article/read?index=` + index;
                            break;
                        case 'failure_report':
                            alert('자신의 게시물에 좋아요를 할 수 없습니다');
                            break;
                        default:
                            alert('실패');
                            break;
                    }
                } else {
                    alert('실패');
                }
            }
        };
        xhr.send();
    }
});