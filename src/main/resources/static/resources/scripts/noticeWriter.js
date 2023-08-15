const noticeWriteForm = document.getElementById('notice-writer');


ClassicEditor.create(noticeWriteForm['content'], {
    simpleUpload: {
        uploadUrl: '/uploadImage'
    }
});


noticeWriteForm.warning = noticeWriteForm.querySelector('[rel="warning"]');
noticeWriteForm.warning.show = (text) => {
    noticeWriteForm.warning.innerText = text;
    noticeWriteForm.warning.classList.add('visible');
}
noticeWriteForm.warning.hide = () => {
    noticeWriteForm.warning.classList.remove('visible');
}


noticeWriteForm.onsubmit = e => {
    noticeWriteForm.warning.hide();
    if (noticeWriteForm['title'].value == '') {
        noticeWriteForm['title'].focus();
        noticeWriteForm.warning.show('제목을 입력해 주세요');
        return false;
    }
    if (noticeWriteForm['content'].value == '') {
        noticeWriteForm['content'].focus();
        noticeWriteForm.warning.show('내용을 입력해 주세요');
        return false;
    }
}


