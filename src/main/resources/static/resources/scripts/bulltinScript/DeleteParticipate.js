const ParticipateDeleteButton = bulletinForm.querySelector('[name="ParticipateDelete"]');
ParticipateDeleteButton.addEventListener('click', e => {
    e.preventDefault();
    const index = ParticipateDeleteButton.dataset.index;

    const confirmResult = confirm('참여를 취소하시겠습니까?');
    if (confirmResult === true) {
        const xhr = new XMLHttpRequest();
        xhr.open('PATCH', `./Participate?index=${index}`)
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'success':
                            alert('참여가 취소되었습니다');
                            location.href = `/article/read?index=` + index;
                            break;
                        case 'failure':
                            alert('먼저 참여를 해주세요');
                            break;
                        default:
                            alert('무슨오류일까?');
                    }
                } else {
                    alert('먼저 참여를 해주세요');
                }
            }
        };
        xhr.send();
    } else if (confirmResult === false) {
        alert('참여를 취소합니다');
        return;
    }
});
