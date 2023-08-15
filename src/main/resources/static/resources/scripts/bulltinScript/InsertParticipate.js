const ParticipateButton = bulletinForm.querySelector('[name="Participate"]');
ParticipateButton.addEventListener('click', e => {
    e.preventDefault();
    const index = ParticipateButton.dataset.index;

    const confirmResult = confirm('참여하시겠습니까?');
    if (confirmResult === true) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', `./Participate?index=${index}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseText = xhr.responseText;
                    if (responseText === 'true') {
                        alert('참여되었습니다');
                        location.href = `/article/read?index=` + index;
                        return;
                    } else {
                        alert('제한인원을 초과했습니다');
                        return;
                    }
                } else {
                    alert('이미 참여한 사용자입니다');
                    return;
                }
            }
        };
        xhr.send();
    }else if (confirmResult === false) {
        alert('참여를 취소합니다');
        return;
    }
});
