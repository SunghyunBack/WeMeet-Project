const deleteButton = bulletinForm.querySelector('[name="delete"]');
const patchButton = bulletinForm.querySelector('[name="patch"]');
const writeButton = bulletinForm.querySelector('[name="write"]');
const finishButton = bulletinForm.querySelector('[name="finish"]');
const Logout = bulletinForm.querySelector('[name="logout"]');


writeButton.onclick = function(e) {
    if (!e.target.form) {
        // 이벤트가 버튼 자체에서 발생한 경우에만 이동하도록 함
        e.preventDefault();
        window.location.href = "/write";
    }
    return;
}

Logout.addEventListener('click', e => {
    const action = Logout.dataset.action;
    switch (action) {
        case 'logout':
            e.preventDefault();
            location.href = '/logout';
            break;
    }
});


//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//게시글 수정 및 삭제 참여 리뷰파트//
deleteButton.addEventListener('click', e => {
    e.preventDefault();
    const index = deleteButton.dataset.index;
    const confirmResult = confirm('삭제하시겠습니까?');
    if (confirmResult === true) {
        const xhr = new XMLHttpRequest();
        xhr.open('DELETE', `./read?index=${index}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseText = xhr.responseText;
                    if (responseText === 'true') {
                        alert('삭제되었습니다');
                        location.href = '/article?p=1&category=';
                        return;
                    } else {
                        alert('작성한 사용자가 아니므로 삭제하지 못합니다');
                        return;
                    }
                } else {
                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                    return;
                }
            }
        };
        xhr.send();
    } else if (confirmResult === false) {
        alert('삭제를 취소합니다');
        return;
    }
}); //게시판 삭제

patchButton.addEventListener('click', e => {
    e.preventDefault();

    const index = patchButton.dataset.index;

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `./patch?index=${index}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const confirmResult = confirm('수정을 하시겠습니까?');
                if (confirmResult === true) {
                    location.href = `/article/patch?index=${index}`
                } else if (confirmResult === false) {
                    alert('수정을 취소합니다');
                    return;
                }
            } else {
                alert('작성한 사용자가 아니라 수정이 불가능합니다.');
            }
        }
    };
    xhr.send();
}); //게시판 수정


//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//

finishButton.addEventListener('click', e => {
    e.preventDefault();
    const index = finishButton.dataset.index;
    const confirmResult = confirm('게시물을 완료 하시겠습니까?');
    if (confirmResult === true) {
        const xhr = new XMLHttpRequest();
        xhr.open('PATCH', `./review?index=${index}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'success':
                            alert('게시물을 완료하였습니다.')
                            const xhr = new XMLHttpRequest();
                            xhr.open('GET', `./review?index=${index}`);
                            xhr.onreadystatechange = () => {
                                if (xhr.readyState === XMLHttpRequest.DONE) {
                                    if (xhr.status >= 200 && xhr.status < 300) {
                                        location.href = `/article/review?index=${index}`
                                    } else {
                                        alert('사용자님의 게시물이 아니라 완료가 불가능합니다.');
                                    }
                                }
                            };
                            xhr.send();
                            break;
                        case 'failure':
                            alert('사용자님의 게시물이 아닙니다.');
                            break;
                        default:
                            alert('default 값');
                    }
                } else {
                    alert('서버에서 문제가 생겼습니다.');
                }
            }
        };
        xhr.send();
    } else if (confirmResult === false) {
        alert('완료를 취소합니다');
        return;
    }
})
//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//게시글 수정 리뷰파트//
