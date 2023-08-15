const noticePatchForm = document.getElementById('notice-patch');
const noticePatch = noticePatchForm.querySelector('[rel="patch"]');


ClassicEditor.create(noticePatchForm['content'], {
    simpleUpload: {
        uploadUrl: '/uploadImage'
    }
});
noticePatchForm.warning = noticePatchForm.querySelector('[rel="warning"]');
noticePatchForm.warning.show = (text) => {
    noticePatchForm.warning.innerText = text;
    noticePatchForm.warning.classList.add('visible');
}
noticePatchForm.warning.hide = () => {
    noticePatchForm.warning.classList.remove('visible');
}

noticePatchForm.onsubmit = e => {
    e.preventDefault()
    noticePatchForm.warning.hide();
    if (noticePatchForm['title'].value == '') {
        noticePatchForm['title'].focus();
        noticePatchForm.warning.show('제목을 입력해 주세요');
        return false;
    }
    if (noticePatchForm['content'].value == '') {
        noticePatchForm['content'].focus();
        noticePatchForm.warning.show('내용을 입력해 주세요');
        return false;
    }
    const index = noticePatch.dataset.index;

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('title',noticePatchForm['title'].value)
    formData.append('content',noticePatchForm['content'].value)
    xhr.open('PATCH', `/noticeView/patch?index=${index}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'success':
                        alert('수정에 성공했습니다');
                        window.location.href = '/noticeView?index=' + index; //수정되면 게시판 바로 이동
                        break;
                    case 'failure':
                        alert('수정에 실패했습니다');
                        break;
                    default:
                        alert('알수없는문제가 생겼습니다. 다시한번 시도해주세요.')
                }
            } else {
                alert('서버에 문제가 생겼습니다. 나중에 다시 시도해 주세요')
            }
        }
    };
    xhr.send(formData);
}


