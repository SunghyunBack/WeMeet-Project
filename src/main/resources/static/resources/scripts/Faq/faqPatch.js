const faqPatchForm = document.getElementById('faqPatch');
const faqPatch = faqPatchForm.querySelector('[rel="patch"]');


ClassicEditor.create(faqPatchForm['content'], {
    simpleUpload: {
        uploadUrl: '/uploadImage'
    }
});
faqPatchForm.warning = faqPatchForm.querySelector('[rel="warning"]');
faqPatchForm.warning.show = (text) => {
    faqPatchForm.warning.innerText = text;
    faqPatchForm.warning.classList.add('visible');
}
faqPatchForm.warning.hide = () => {
    faqPatchForm.warning.classList.remove('visible');
}

faqPatchForm.onsubmit = e => {
    e.preventDefault()
    faqPatchForm.warning.hide();
    if (faqPatchForm['title'].value === '') {
        faqPatchForm['title'].focus();
        faqPatchForm.warning.show('제목을 입력해 주세요');
        return false;
    }
    if (faqPatchForm['content'].value === '') {
        faqPatchForm['content'].focus();
        faqPatchForm.warning.show('내용을 입력해 주세요');
        return false;
    }
    const index = faqPatch.dataset.index;

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('title',faqPatchForm['title'].value)
    formData.append('content',faqPatchForm['content'].value)
    xhr.open('PATCH', `/faqView/patch?index=${index}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'success':
                        alert('수정에 성공했습니다');
                        window.location.href = '/faqView?index=' + index; //수정되면 게시판 바로 이동
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


