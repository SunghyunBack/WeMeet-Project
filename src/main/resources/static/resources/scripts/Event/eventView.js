const noticeViewForm = document.getElementById('noticeView');
const noticeDeleteButton = noticeViewForm.querySelector('[rel="delete"]');
const noticePatchButton = noticeViewForm.querySelector('[rel="patch"]');

noticeDeleteButton.addEventListener('click', e => {
    e.preventDefault();
    const index = noticeDeleteButton.dataset.index;
    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', `deleteEvent?index=${index}`); // 수정된 경로를 사용
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseText = xhr.responseText; // 'true' | 'false'
                const confirmResult = confirm('삭제 하시겠습니까?');
                if (confirmResult === true) {
                    if (responseText === 'true') {
                        window.location.replace('/event');
                    } else {
                        alert('알 수 없는 이유로 삭제하지 못하였습니다.\n\n이미 삭제된 공지사항일 수도 있습니다.');
                    }
                } else if (confirmResult === false) {
                    alert('삭제를 취소합니다');
                    return;
                }
            } else {
                alert('관리자만 삭제할수 있습니다.');
            }
        }
    };
    xhr.send();
});


noticePatchButton.addEventListener('click', e => {
    e.preventDefault();

    const indexs = noticePatchButton.dataset.index;

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/eventView/patch?index=${indexs}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const confirmResult = confirm('수정을 하시겠습니까?');
                if (confirmResult === true) {
                    location.href = `/eventView/patch?index=${indexs}`
                } else if (confirmResult === false) {
                    alert('수정을 취소합니다');
                    return;
                }
            } else {
                alert('관리자만 수정할수 있습니다.');
            }
        }
    };
    xhr.send();
}); //이벤트 수정
