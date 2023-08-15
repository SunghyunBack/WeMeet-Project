document.addEventListener('DOMContentLoaded', () => {
    document.body.querySelectorAll('[data-action]').forEach(element => {
        element.addEventListener('click', e => {

            const action = element.dataset.action;
            switch (action) {
                case 'logout':
                    e.preventDefault();
                    location.href = '/logout';
                    break;
            }
        });
    });
});


//
// 제공한 JavaScript 코드는 속성이 있는 요소의 클릭을 수신 대기하는 이벤트 리스너입니다 data-action.
// 클릭 이벤트가 발생하면 속성의 값을 확인 data-action하고 그 값에 따라 특정 동작을 수행합니다.
// 이 경우 요소를 찾아 로그아웃 버튼을 클릭하면 data-action="logout"사용자를 URL로 리디렉션합니다.
// 이 경우 양식을 제출하거나 링크를 따라가는 클릭 이벤트의 기본 동작을 방지하는 데 사용됩니다 /logout.e.preventDefault()
//
// 페이지를 자주 새로 고칠 때 오작동하는 문제에 대해 언급한 문제는 이벤트 리스너가 요소에 연결되는 방식으로 인해 발생할 수 있습니다.
// 페이지가 새로 고쳐지면 JavaScript 코드가 다시 실행되고 이벤트 리스너가 요소에 다시 연결됩니다.
// 이벤트 리스너가 동일한 요소에 여러 번 연결된 경우 단일 클릭에 대해 여러 클릭 이벤트가 트리거되는 등 의도하지 않은 동작이 발생할 수 있습니다.
// 이로 인해 로그아웃 작업이 여러 번 수행되어 바람직하지 않은 동작이나 오류가 발생할 수 있습니다.
//     이 문제를 해결하기 위해 이벤트 리스너가 요소에 한 번만 연결되도록 할 수 있습니다.
//     이를 달성하는 한 가지 방법은 DOM이 완전히 로드된 후 이벤트 리스너가 연결되도록 문서 준비 기능 내에 이벤트 리스너를 추가하는 것입니다.


