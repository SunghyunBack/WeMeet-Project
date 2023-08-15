const loginForm = document.getElementById('loginForm');
const showIcon = document.getElementById('showIcon');
const hideIcon = document.getElementById('hideIcon');
const passwordObject = document.querySelector('.passwordObject');


// 아이디와 비밀번호를 입력하지 않은체 로그인 버튼을 누른다면 화면에 아이디와 비밀번호를 입력하라는 숨겨진 문구가 보일수 있도록 하는 함수를 만든다.
loginForm.warning = loginForm.querySelector('[rel="emailWarning"]');
loginForm.warning.show = (text) => {
    loginForm.warning.innerText = text;
    loginForm.warning.classList.add('visible');
};
loginForm.warning.hide = () => loginForm.warning.classList.remove('visible');


// 아이콘 클릭시 input태그의 타입이 password와 text로 바뀌는 코드
showIcon.addEventListener('click', function () {
    showIcon.style.display = 'none';
    hideIcon.style.display = 'block';
    passwordObject.type = 'password';
});
hideIcon.addEventListener('click', function () {
    showIcon.style.display = 'block';
    hideIcon.style.display = 'none';
    passwordObject.type = 'text';
})


loginForm.onsubmit = e => {
    e.preventDefault()
    loginForm.warning.hide();
    if (loginForm['email'].value === '') {
        loginForm['email'].focus();
        loginForm.warning.show('이메일을 입력해 주세요.');
        return false;
    }
    if (!new RegExp('^(?=.{10,50}$)([\\da-zA-Z\\-_\\.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(loginForm['email'].value)) {
        loginForm['email'].focus();
        loginForm['email'].select();
        loginForm.warning.show('올바른 이메일을 입력해 주세요.');
        return false;
    }
    if (loginForm['password'].value === '') {
        loginForm['password'].focus();
        loginForm.warning.show('비밀번호를 입력해 주세요.');
        return false;
    }
    if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]};:\'",<.>/?]{8,50})$').test(loginForm['password'].value)) {
        loginForm['password'].focus();
        loginForm['password'].select();
        loginForm.warning.show('올바른 비밀번호를 입력해 주세요.');
        return false;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', loginForm['email'].value);
    formData.append('password', loginForm['password'].value);
    xhr.open('POST', `/login`);
    xhr.onreadystatechange = () => {
        // readyState메서드는 XMLHttpRequest객체의 상태를나타낸다.
        // 아래의 코드는 readyState프로퍼티의 값이 변경될때마다 readystatechange이벤트가 실행되는데
        // 이때 해당 이벤트에서 readyState프로퍼티의 값을 확인하는것이 좋다.
        //  그중 XMLHttpRequest.DONE은 요청이 완료되었다는 뜻을 가진다.
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status >= 200 && xhr.status < 300) {
            const responseObject = JSON.parse(xhr.responseText);
            switch (responseObject.result) {
                case 'failure':
                    loginForm.warning.show('이메일 혹은 비밀번호가 올바르지 않습니다.');
                    loginForm['email'].focus();
                    loginForm['email'].select();
                    break;
                case 'success':
                    //  html의 remember의 버튼이 체크되어 있으면 localStorage를 사용해 데이터를 저장한다.
                    //  키와 값으로 데이터를 저장한다.
                    if (loginForm['remember'].checked) {
                        localStorage.setItem('email', loginForm['email'].value);
                    }
                    if (document.referrer && document.referrer.indexOf("/") !== -1) {
                        history.back();
                        location.href = document.referrer;
                    } else {
                        location.href = '/';
                    }
                    break;
                default:
                    loginForm.warning.show('서버가 알 수 없는 응답을 반환했습니다. 관리자에게 문의해 주세요.');
            }
        } else {
            loginForm.warning.show('서버가 알 수 없는 응답을 반환했습니다. 관리자에게 문의해 주세요.');

        }

    };
    xhr.send(formData);
};

// 로그인 화면을 불러올때 localStorage로 저장된 email값이 있으면 불러오겠다는 코드이다.
window.onload = function() {
    if (localStorage.getItem('email')) {
        loginForm['email'].value = localStorage.getItem('email');
        loginForm['password'].focus();
    }
}
