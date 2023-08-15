const dialogCover = document.getElementById('dialogCover');
const dialogLayer = document.getElementById('dialogLayer');

dialogCover.show = () => {
    document.body.setAttribute('style', 'overflow: hidden');
    dialogCover.classList.add('visible');
};

dialogCover.hide = () => {
    document.body.removeAttribute('style');
    dialogCover.classList.remove('visible');
};

dialogLayer.show = (params) => {
    dialogLayer.querySelector('[rel="title"]').innerText = params.title;
    dialogLayer.querySelector('[rel="content"]').innerHTML = params.content;
    const cancelButton = dialogLayer.querySelector('[rel="cancel"]');
    if (typeof params['onCancel'] === 'function') {
        // params.onCancel이 함수인 경우
        cancelButton.style.display = 'inline-block';// 취소 버튼을 보이도록 설정합니다.
        cancelButton.onclick = params['onCancel']; // 취소 버튼의 클릭 이벤트에 params.onCancel 함수를 할당합니다.
    } else {
        // params.onCancel이 함수가 아닌 경우
        cancelButton.style.display = 'none'; // 취소 버튼을 숨깁니다.
    }
    dialogLayer.querySelector('[rel="confirm"]').onclick = params['onConfirm'];
    // 확인 버튼의 클릭 이벤트에 params.onConfirm 함수를 할당합니다.
    dialogLayer.classList.add('visible');
    // dialogLayer에 'visible' 클래스를 추가하여 보이도록 설정합니다.
};
dialogLayer.hide = () => {
    dialogLayer.classList.remove('visible');
};
