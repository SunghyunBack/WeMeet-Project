const like = bulletinForm.querySelector('.like');

like.addEventListener('click', e => {
    e.preventDefault();
    const index = like.dataset.index;
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    xhr.open('POST', `./like?index=${index}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'success_like':
                        location.href = `/article/read?index=` + index;
                        break;
                    case 'failure':
                        alert('자신의 게시물에 좋아요를 할수 없습니다');
                        break;
                    case 'failure_like':
                        alert('중복으로 좋아요를 할수 없습니다');
                        break;
                    default:
                        alert('걍 실패임');
                        break;
                }
            } else {
                alert('걍 실패');
            }
        }
    };
    xhr.send();
})