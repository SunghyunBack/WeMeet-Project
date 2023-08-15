const recoverForm = document.getElementById('recoverForm');
const emailOption = document.querySelector('.option.email');
const passwordOption = document.querySelector('.option.password');
const emailContainer = document.getElementById('email-container');
const passwordContainer = document.getElementById('password-container');
const contactSend = document.getElementById('contactSend');
const emailSend = document.getElementById('emailSend');
const loadingForm = document.getElementById('_loading');

// 이메일, 비밀번호 option에 맞게 아래의 container가 나타나게 하는 코드


loadingForm.show = () =>{
    loadingForm.classList.add('visible');
}
loadingForm.hide = () =>{
    loadingForm.classList.remove('visible');
}



emailOption.addEventListener('change', function () {
    if (this.checked) {
        emailContainer.style.display = 'flex';
        passwordContainer.style.display = 'none';
        contactSend.style.display = 'block';
        emailSend.style.display = 'none';
        recoverForm.cNotification.hide();
        recoverForm.warning.hide();
    }
});

passwordOption.addEventListener('change', function () {
    if (this.checked) {
        emailContainer.style.display = 'none';
        passwordContainer.style.display = 'block';
        contactSend.style.display = 'none';
        emailSend.style.display = 'block';
        recoverForm.warning.hide();
        recoverForm.cNotification.hide();
    }
});

//  입력값들이(연락처, 인증번호, 이메일) 이상할때 나타내기 위한 코드작업
recoverForm.warning = recoverForm.querySelector('[rel="contactWarning"]');
recoverForm.warning.show = (text) => {
    recoverForm.warning.innerText = text;
    recoverForm.warning.classList.add('visible');
};
recoverForm.warning.hide = () => {
    recoverForm.warning.classList.remove('visible');
}
recoverForm.cNotification = recoverForm.querySelector('[rel="cNotification"]');

recoverForm.cNotification.show = (text) => {
    recoverForm.cNotification.innerText = text;
    recoverForm.cNotification.classList.add('visible');
};
recoverForm.cNotification.hide = () => {
    recoverForm.cNotification.classList.remove('visible');
}


//  아이디 찾기 과정에서 연락처 인증버튼을 누르면 인증버튼은 더이상 클릭하지 못하고 클릭하지 못하는 인증번호 인증 버튼이 클릭할수 있게 되는코드
recoverForm['eContactSend'].onclick = () => {
    if (recoverForm['eName'].value === '') {
        recoverForm.warning.show('이름을 입력해 주세요.');
        recoverForm['eName'].focus();
        return;
    }
    if (recoverForm['eContact'].value === '') {
        recoverForm.warning.show('연락처를 입력해 주세요.');
        recoverForm['eContact'].focus();
        return;
    }
    if (!new RegExp('^(010\\d{8})$').test(recoverForm['eContact'].value)) {
        recoverForm.warning.show('올바른 연락처를 입력해 주세요.');
        recoverForm['eContact'].focus();
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('GET',`/recoverAccount/contactCodeRec?name=${recoverForm['eName'].value}&contact=${recoverForm['eContact'].value}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure':
                        recoverForm.warning.show('일치하는 회원을 찾을 수 없습니다.');
                        recoverForm['eContact'].select();
                        break;
                    case 'success':
                        recoverForm.cNotification.show('입력하신 연락처로 인증번호를 전송하였습니다. 5분 이내에 인증을 완료해 주세요.');
                        recoverForm['eContact'].setAttribute('disabled', 'disabled');
                        recoverForm['eContactSend'].setAttribute('disabled', 'disabled');
                        recoverForm['eContactCode'].removeAttribute('disabled');
                        recoverForm['eContactVerify'].removeAttribute('disabled');
                        recoverForm['eContactCode'].focus();
                        recoverForm['eContactSalt'].value = responseObject.salt;
                        break;
                    default:
                        recoverForm.warning.show('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                recoverForm.warning.show('서버와 통신하지 못하였습니다. 잠시후 다시 시도해 주세요.')
            }
        }
    };
    xhr.send();
}

//  6자리 인증번호 누른후 
recoverForm['eContactVerify'].onclick = () => {
    if (recoverForm['eContactCode'] === '') {
        recoverForm.warning.show('인증번호를 입력해주세요');
        recoverForm['eContactCode'].focus();
        return false;
    }
    if (!new RegExp('^(\\d{6})$').test(recoverForm['eContactCode'].value)) {
        recoverForm.warning.show('올바른 인증번호를 입력해 주세요.');
        recoverForm['eContactCode'].focus();
        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', recoverForm['eName'].value);
    formData.append('contact', recoverForm['eContact'].value);
    formData.append('code', recoverForm['eContactCode'].value);
    formData.append('salt', recoverForm['eContactSalt'].value);
    xhr.open('PATCH', `/recoverAccount/contactCodeRec`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure':
                        recoverForm.warning.show('인증번호가 올바르지 않습니다. 다시 한번 확인해 주세요.');
                        recoverForm['eContactCode'].select();
                        break;
                    case 'failure_expired':
                        recoverForm.warning.show('해당 인증번호가 만료되었습니다.');
                        break;
                    case 'success':
                        recoverForm.cNotification.show('인증이 완료되었습니다.');
                        recoverForm['eContactCode'].setAttribute('disabled', 'disabled');
                        recoverForm['eContactVerify'].setAttribute('disabled', 'disabled');
                        break;
                    default:
                        recoverForm.warning.show('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                recoverForm.warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
            }
        }
    };
    xhr.send(formData);
}


// 연락처 인증 혹은 이메일 입력 다한 후 최종적인 다음으로 넘어가는 버튼을 눌렀을때
recoverForm['contactSend'].onclick = e => {
    e.preventDefault();
    if (recoverForm['eName'].value === '') {
        recoverForm.warning.show('이름을 입력해주세요')
        recoverForm.warning.focus();
        return false;
    }
    if (recoverForm['eContact'].value === '') {
        recoverForm.warning.show('연락처를 입력해주세요.');
        recoverForm['eContact'].focus();
        return false;
    }
    if (recoverForm['eContactCode'].value === '') {
        recoverForm.warning.show('인증번호를 입력해주세요');
        recoverForm['eContactCode'].focus();
        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', recoverForm['eName'].value)
    formData.append('contact', recoverForm['eContact'].value);
    formData.append('code', recoverForm['eContactCode'].value);
    formData.append('salt', recoverForm['eContactSalt'].value);
    xhr.open('PATCH', `/recoverAccount/contactCodeRec`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'success':
                        location.href = `/recoverAccount/confirmEmail?name=${recoverForm['eName'].value}&contact=${recoverForm['eContact'].value}&code=${recoverForm['eContactCode'].value}&salt=${recoverForm['eContactSalt'].value}`;
                        break;
                    default:
                        recoverForm.warning.show('서버가 알수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                recoverForm.warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
}


recoverForm.onsubmit = function (e) {
    e.preventDefault();
}


recoverForm['emailSend'].onclick = e =>{
    e.preventDefault();
    const link = document.querySelector('.cover-button');
        if (!new RegExp('^(?=.{10,50}$)([\\da-zA-Z\\-_\\.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(recoverForm['pEmail'].value)) {
            dialogCover.show();
            dialogLayer.show({
                title: 'We-Meet',
                content: '이메일을 입력해주세요.',
                onConfirm: e => {
                    e.preventDefault();
                    dialogCover.hide();
                    dialogLayer.hide();
                }
            });
            return;
        }
        loadingForm.show();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('email', recoverForm['pEmail'].value);
        xhr.open('POST', '/recoverAccount/recoverPassword');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'failure':
                           loadingForm.hide();
                            dialogCover.show();
                            dialogLayer.show({
                                title: 'We-Meet',
                                content: '가입 되지 않은 이메일 입니다',
                                onConfirm: e => {
                                    e.preventDefault();
                                    dialogCover.hide();
                                    dialogLayer.hide();
                                }
                            });
                            break;
                        case 'success':
                            loadingForm.hide();
                            dialogCover.show();
                            dialogLayer.show({
                                title: 'We-Meet',
                                content: '확인을 누를시 인증페이지로 이동합니다.',
                                onConfirm: e => {
                                    e.preventDefault();
                                    location.href = responseObject['redirect'];
                                    dialogCover.hide();
                                    dialogLayer.hide();
                                }
                            });

                            break;
                        default:
                            alert('서버오류');
                    }
                } else {
                   alert('서버오류');
                }
            }
        };
   xhr.send(formData);
};













