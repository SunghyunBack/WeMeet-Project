const eventWriteForm = document.getElementById('event-writer');


ClassicEditor.create(eventWriteForm['content'], {
    simpleUpload: {
        uploadUrl: '/eventUploadImage'
    }
});


eventWriteForm.onsubmit = e => {
    if (eventWriteForm['title'].value == '') {
        e.preventDefault();
        eventWriteForm['title'].focus();
        alert('제목을 입력해 주세요');
        return true;
    }
    if (eventWriteForm['content'].value == '') {
        e.preventDefault();
        eventWriteForm['content'].focus();
        alert('내용을 입력해 주세요');
        return true;
    }
}


