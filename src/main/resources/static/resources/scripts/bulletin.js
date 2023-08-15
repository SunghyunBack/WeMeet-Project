const list = document.querySelector('.container');
const listScrollWidth = list.scrollWidth;
const listClientWidth = list.clientWidth;

const bulletinForm = document.getElementById('bulletinForm');
const commentContainer = document.getElementById('commentContainer');
const commentButton = bulletinForm.querySelector('[name="commentButton"]');

let startX = 0;
let nowX = 0;
let endX = 0;
let listX = 0;
let isDragging = false;

const getClientX = (e) => {
    const isTouches = e.touches ? true : false;
    return isTouches ? e.touches[0].clientX : e.clientX;
};

const getTranslateX = () => {
    return parseInt(getComputedStyle(list).transform.split(/[^\-0-9]+/g)[5]);
};

const setTranslateX = (x) => {
    list.style.transform = `translateX(${x}px)`;
};

const onScrollStart = (e) => {
    startX = getClientX(e);
    isDragging = true;
    document.addEventListener('mousemove', onScrollMove);
    document.addEventListener('touchmove', onScrollMove);
    document.addEventListener('mouseup', onScrollEnd);
    document.addEventListener('touchend', onScrollEnd);
};

const onScrollMove = (e) => {
    if (!isDragging) return;
    nowX = getClientX(e);
    const v = listX + nowX - startX;
    if (v > 0) return;
    if (v < -(260 * list.childElementCount)) return;
    console.log(v);
    setTranslateX(v);
};

const bindEvents = () => {
    list.addEventListener('mousedown', onScrollStart);
    list.addEventListener('touchstart', onScrollStart);
};

const onScrollEnd = (e) => {
    if (!isDragging) return;
    isDragging = false;
    endX = getClientX(e);
    listX = getTranslateX();
    document.removeEventListener('mousemove', onScrollMove);
    document.removeEventListener('touchmove', onScrollMove);
    document.removeEventListener('mouseup', onScrollEnd);
    document.removeEventListener('touchend', onScrollEnd);


};

bindEvents();


// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트// 댓글파트//


// 욕설 배열
const abuse = ['ㅅㅂ', 'ㅆㅂ', '시발', '씨발', '10새', '10새기', '10새리', '10세리', '10쉐이', '10쉑', '10스', '10쌔',
    '10쌔기', '10쎄', '10알', '10창', '10탱', '18것', '18넘', '18년', '18노', '18놈',
    '18뇬', '18럼', '18롬', '18새', '18새끼', '18색', '18세끼', '18세리', '18섹', '18쉑', '18스', '18아',
    'c파', 'c팔', 'fuck', 'ㄱㅐ', 'ㄲㅏ', 'ㄲㅑ', 'ㄲㅣ', 'ㅅㅂㄹㅁ', 'ㅅㅐ', 'ㅆㅂㄹㅁ', 'ㅆㅍ', 'ㅆㅣ', 'ㅆ앙', 'ㅍㅏ', '凸',
    '갈보', '갈보년', '강아지', '같은년', '같은뇬', '개같은', '개구라', '개년', '개놈',
    '개뇬', '개대중', '개독', '개돼중', '개랄', '개보지', '개뻥', '개뿔', '개새', '개새기', '개새끼',
    '개새키', '개색기', '개색끼', '개색키', '개색히', '개섀끼', '개세', '개세끼', '개세이', '개소리', '개쑈',
    '개쇳기', '개수작', '개쉐', '개쉐리', '개쉐이', '개쉑', '개쉽', '개스끼', '개시키', '개십새기',
    '개십새끼', '개쐑', '개씹', '개아들', '개자슥', '개자지', '개접', '개좆', '개좌식', '개허접', '걔새',
    '걔수작', '걔시끼', '걔시키', '걔썌', '걸레', '게색기', '게색끼', '광뇬', '구녕', '구라', '구멍',
    '그년', '그새끼', '냄비', '놈현', '뇬', '눈깔', '뉘미럴', '니귀미', '니기미', '니미', '니미랄', '니미럴',
    '니미씹', '니아배', '니아베', '니아비', '니어매', '니어메', '니어미', '닝기리', '닝기미', '대가리',
    '뎡신', '도라이', '돈놈', '돌아이', '돌은놈', '되질래', '뒈져', '뒈져라', '뒈진', '뒈진다', '뒈질',
    '뒤질래', '등신', '디져라', '디진다', '디질래', '딩시', '따식', '때놈', '또라이', '똘아이', '똘아이',
    '뙈놈', '뙤놈', '뙨넘', '뙨놈', '뚜쟁', '띠바', '띠발', '띠불', '띠팔', '메친넘', '메친놈', '미췬',
    '미췬', '미친', '미친넘', '미친년', '미친놈', '미친새끼', '미친스까이', '미틴', '미틴넘', '미틴년',
    '미틴놈', '바랄년', '병자', '뱅마', '뱅신', '벼엉신', '병쉰', '병신', '부랄', '부럴', '불알', '불할', '붕가',
    '붙어먹', '뷰웅', '븅', '븅신', '빌어먹', '빙시', '빙신', '빠가', '빠구리', '빠굴', '빠큐', '뻐큐',
    '뻑큐', '뽁큐', '상넘이', '상놈을', '상놈의', '상놈이', '새갸', '새꺄', '새끼', '새새끼', '새키',
    '색끼', '생쑈', '세갸', '세꺄', '세끼', '섹스', '쇼하네', '쉐', '쉐기', '쉐끼', '쉐리', '쉐에기',
    '쉐키', '쉑', '쉣', '쉨', '쉬발', '쉬밸', '쉬벌', '쉬뻘', '쉬펄', '쉽알', '스패킹', '스팽', '시궁창', '시끼',
    '시댕', '시뎅', '시랄', '시발', '시벌', '시부랄', '시부럴', '시부리', '시불', '시브랄', '시팍',
    '시팔', '시펄', '신발끈', '심발끈', '심탱', '십8', '십라', '십새', '십새끼', '십세', '십쉐', '십쉐이', '십스키',
    '십쌔', '십창', '십탱', '싶알', '싸가지', '싹아지', '쌉년', '쌍넘', '쌍년', '쌍놈', '쌍뇬', '쌔끼',
    '쌕', '쌩쑈', '쌴년', '썅', '썅년', '썅놈', '썡쇼', '써벌', '썩을년', '썩을놈', '쎄꺄', '쎄엑',
    '쒸벌', '쒸뻘', '쒸팔', '쒸펄', '쓰바', '쓰박', '쓰발', '쓰벌', '쓰팔', '씁새', '씁얼', '씌파', '씨8',
    '씨끼', '씨댕', '씨뎅', '씨바', '씨바랄', '씨박', '씨발', '씨방', '씨방새', '씨방세', '씨밸', '씨뱅',
    '씨벌', '씨벨', '씨봉', '씨봉알', '씨부랄', '씨부럴', '씨부렁', '씨부리', '씨불', '씨붕', '씨브랄',
    '씨빠', '씨빨', '씨뽀랄', '씨앙', '씨파', '씨팍', '씨팔', '씨펄', '씸년', '씸뇬', '씸새끼', '씹같', '씹년',
    '씹뇬', '씹보지', '씹새', '씹새기', '씹새끼', '씹새리', '씹세', '씹쉐', '씹스키', '씹쌔', '씹이', '씹자지',
    '씹질', '씹창', '씹탱', '씹퇭', '씹팔', '씹할', '씹헐', '아가리', '아갈', '아갈이', '아갈통',
    '아구창', '아구통', '아굴', '얌마', '양넘', '양년', '양놈', '엄창', '엠병', '여물통', '염병', '엿같', '옘병',
    '옘빙', '오입', '왜년', '왜놈', '욤병', '육갑', '은년', '을년', '이년', '이새끼', '이새키', '이스끼',
    '이스키', '임마', '자슥', '잡것', '잡넘', '잡년', '잡놈', '저년', '저새끼', '접년', '젖밥', '조까',
    '조까치', '조낸', '조또', '조랭', '조빠', '조쟁이', '조지냐', '조진다', '조찐', '조질래', '존나', '존나게', '존니', '존만',
    '존만한', '좀물', '좁년', '좁밥', '좃까', '좃또', '좃만', '좃밥', '좃이', '좃찐', '좆같', '좆까', '좆나',
    '좆또', '좆만', '좆밥', '좆이', '좆찐', '좇같', '좇이', '좌식', '주글', '주글래', '주데이', '주뎅',
    '주뎅이', '주둥아리', '주둥이', '주접', '주접떨', '죽고잡', '죽을래', '죽통', '쥐랄', '쥐롤',
    '쥬디', '지랄', '지럴', '지롤', '지미랄', '짜식', '짜아식', '쪼다', '쫍빱', '찌랄', '창녀', '캐년',
    '캐놈', '캐스끼', '캐스키', '캐시키', '탱구', '팔럼', '퍽큐', '호로', '호로놈', '호로새끼',
    '호로색', '호로쉑', '호로스까이', '호로스키', '후라들', '후래자식', '후레'];

//

function postComment(content, commentIndex, toFocus, refreshCommentAfter) {
    refreshCommentAfter ??= true;
    if (abuse.some(x => bulletinForm['content'].value.indexOf(x) > -1)) {
        alert('내용에 욕설이 포함되있습니다 다시 입력해주세요');
        return;
    }
    const articleIndex = bulletinForm['articleIndex'].value;
    const articleEmail = bulletinForm['articleEmail'].value;
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleIndex', articleIndex);
    formData.append('content', content);
    formData.append('articleEmail', articleEmail);
    formData.append('nickname', bulletinForm['nickname'].value);
    if (commentIndex) {
        formData.append('commentIndex', commentIndex);
    }
    xhr.open('POST', '/comment');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure':
                        alert('댓글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        break;
                    case 'failure_not_login':
                        alert('로그인 상태가 아닙니다. 로그인해주세요.');
                        break;
                    case 'success':
                    case 'success_same':
                        if (toFocus) {
                            toFocus.value = '';
                            toFocus.focus();
                        }
                        if (refreshCommentAfter === true) {
                            refreshComment();
                        }
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.');
            }
        }
    };
    xhr.send(formData);
}

// 시간수정
function formatDate(dateString) {
    const date = new Date(dateString);
    const formattedDate = date.toLocaleString('en', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
    return formattedDate;
}

//
function refreshComment() {
    const articleIndex = bulletinForm['articleIndex'].value;
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/comment?articleIndex=${articleIndex}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const comments = JSON.parse(xhr.responseText);
                commentContainer.innerHTML = ''; // Clear existing comments

                for (const comment of comments) {
                    const div = document.createElement('div');
                    let commentClass = 'comment-left';

                    if (comment.same === true) {
                        commentClass = 'comment-right';
                    }
                    div.classList.add(commentClass);

                    const nicknameDiv = document.createElement('div');
                    nicknameDiv.classList.add('comment-nickname');
                    nicknameDiv.innerText = comment['nickname'];

                    const headDiv = document.createElement('div');
                    headDiv.classList.add('comment-head');
                    headDiv.innerText = formatDate(comment['createdAt']);


                    const bodyDiv = document.createElement('div');
                    bodyDiv.classList.add('comment-body');


                    const deleteButton = document.createElement('button');
                    deleteButton.classList.add('delete-button');
                    deleteButton.innerText = '삭제';
                    deleteButton.dataset.commentIndex = comment.index;
                    deleteButton.addEventListener('click', (e) => {
                        e.preventDefault();
                        if (!confirm('정말로 해당 댓글을 삭제할까요?')) {
                            return;
                        }
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        xhr.open('DELETE', '/comment?index=' + e.target.dataset.commentIndex);
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject.result) {
                                        case 'failure':
                                            alert('알 수 없는 이유로 댓글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                                            break;
                                        case 'failure_deleted':
                                            alert('이미 삭제된 댓글 입니다. 새로고침 후 확인해 주세요.');
                                            break;
                                        case 'failure_no_authority':
                                            alert('삭제할 수 있는 권한이 없습니다.');
                                            break;
                                        case 'failure_not_login':
                                            alert('로그인 후 다시 시도해 주세요');
                                            break;
                                        case 'success':
                                            refreshComment();
                                            break;
                                        default:
                                            alert('서버가 알 수 없는 응답을 반환 했습니다.');
                                    }
                                } else {
                                    alert('서버와 통신할 수 없습니다.');
                                }
                            }
                        };
                        xhr.send(formData);
                    });

                    if (comment['deleted']) {
                        bodyDiv.innerText = '삭제된 댓글입니다.';
                        bodyDiv.style.color = '#a0a0a0';
                        bodyDiv.style.fontStyle = 'italic';
                        bodyDiv.classList.add('del');
                        div.appendChild(nicknameDiv);
                        div.appendChild(bodyDiv);
                    } else {
                        bodyDiv.innerText = comment['content'];
                        const commentBox = document.createElement('div');
                        commentBox.classList.add('comment-box');

                        commentBox.append(headDiv, deleteButton, bodyDiv);
                        div.append(nicknameDiv, commentBox);
                    }

                    commentContainer.appendChild(div);
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send();
}


bulletinForm.onsubmit = function (e) {
    e.preventDefault();
    if (bulletinForm['content'].value == '') {
        alert('댓글을 입력해 주세요.');
        bulletinForm['content'].focus();
        return;
    }
    postComment(bulletinForm['content'].value, undefined, bulletinForm['content']);
};

document.addEventListener('DOMContentLoaded', () => {
    refreshComment();
});

