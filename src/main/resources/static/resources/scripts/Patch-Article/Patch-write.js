const articleForm = document.getElementById('Article-Form');


let tagCounter = 0; //전역변수 태그카운터
let tags = [];
var thumbnailPlace = document.querySelector('.thumbnail-place');
var thumbnailUpload = document.querySelector('.thumbnail-upload');
var thumbnailChange = document.querySelector('.thumbnail-change');
const thumbnailTitle = document.querySelector('.thumbnail-title');
const thumbnail1 = document.querySelector('.thumbnail1');


ClassicEditor.create(articleForm['content'], {}); //파일 업로드

// var contents = CKEDITOR.instances.editor.getData();


const ArticleTag = document.querySelector('.article-tag'); //tag를 담을 부모
const explainTag = document.querySelector('.explainTag'); //설명
const Tags = document.querySelector('.tags');

ArticleTag.addEventListener('click', function () {
    if (event.target.classList.contains('tag')) {
        return; // 이미 생성된 태그를 클릭한 경우 생성 코드 실행하지 않음
    }

    explainTag.addEventListener('click', function () {
        explainTag.style.display = 'none';
    })

    explainTag.style.display = 'none'; // 처음에 태그 몇 개 적는지 설명하는 div

    const existingTags = ArticleTag.querySelectorAll('.tag');
    for (let i = 0; i < existingTags.length; i++) {
        const tag = existingTags[i];
        if (tag.value.trim() === '#') {
            return; // 이미 생성된 태그가 있고, 해당 태그의 내용이 비어있으면 클릭해도 동작하지 않음
        }
    }

    const TagContainer = document.createElement('div');
    const TagWarning = document.createElement('div');
    const Tag = document.createElement('input');

    Tag.setAttribute('type', 'text');
    Tag.name = "hashTag"
    Tag.value = '#'; // 처음 생성시 # 추가
    TagContainer.classList.add('tag-container');
    TagWarning.classList.add('tag-warning');
    Tag.classList.add('tag'); // 처음 생성시 tag 클래스 추가
    Tag.maxLength = 7;
    Tag.style.width = '150px';

    Tags.appendChild(TagContainer);
    TagContainer.appendChild(TagWarning);
    TagContainer.appendChild(Tag);

    TagWarning.show = () => {
        TagWarning.classList.add('show');
    }

    TagWarning.hide = () => {
        TagWarning.classList.remove('show');
    }

    TagWarning.textContent = "태그는 7글자이상 쓰지못합니다";


    Tag.addEventListener('keydown', function (event) {
        const trimmedText = Tag.value.trim();
        if (event.key === 'Backspace' && trimmedText === '#') {
            event.preventDefault();
        } else if (event.key === 'Enter' || event.key === 'Delete') {
            event.preventDefault();
        }
    });

    Tag.addEventListener('input', function (event) {
        const trimmedText = Tag.value.trim();
        const characterCount = trimmedText.length;

        if (characterCount > 7) {
            const slicedText = trimmedText.slice(0, 7);
            Tag.value = slicedText;
            TagWarning.show();
            setTimeout(function () {
                TagWarning.hide();
            }, 600);
        }
    });


    Tag.addEventListener('mousedown', function (event) {
        event.preventDefault();
        Tag.focus();
    }); //드래그를 막으면서 input 쓰기 가능하게함

    Tag.addEventListener('input', function (event) {
        const trimmedText = Tag.value.trim();
        if (trimmedText.length === 0) {
            Tag.value = '#';
        }
    });

    if (tags.length > 0) {
        const previousTag = tags[tags.length - 1];
        previousTag.disabled = true;
    }
    tags.push(Tag);
    tagCounter++; // 태그 생성 횟수 증가
    Tag.focus();
});


thumbnailChange.addEventListener('change', function (event) {

    articleForm['upload'].files[0] = event.target.files[0]; // 선택한 파일 가져오기

    // FileReader 객체 사용하여 이미지 읽기
    var reader = new FileReader();
    reader.onload = function (e) {

        // 이미지를 표시할 img 요소 생성
        var image = document.createElement('img');
        image.src = e.target.result; // 읽은 이미지 데이터 설정
        image.classList.add('thumbnail'); // 클래스 추가

        // 기존 썸네일 이미지가 있는 경우 교체
        var existingImage = thumbnailPlace.querySelector('.thumbnail');

        if (existingImage) {
            existingImage.src = image.src;
        } else {
            // 썸네일 영역에 이미지 추가
            thumbnailPlace.appendChild(image);
        }
    };
    reader.readAsDataURL(articleForm['upload'].files[0]); // 이미지 파일을 Data URL로 읽기
    thumbnailTitle.style.display = 'none';
    thumbnail1.style.display = 'none';
    thumbnailUpload.textContent = '썸네일 변경';
});


const beForeButton = document.querySelector('input[type="button"][value="이전"]');
beForeButton.onclick = function (e) {
    e.preventDefault();
    inner.style.display = "block";
    articleForm.style.display = 'none';
};

const articlePatch = articleForm.querySelector('[name="patch"]');


articleForm.onsubmit = e => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    if (articleForm['title'].value === '') {
        alert('제목을 입력해주세요');
        return;
    }

    if (articleForm['content'].value === '') {
        alert('게시판을 입력해주세요');
        return;
    }

    if(Tags.value === ''){
        alert('해쉬태그를 입력해주세요');
        return;
    }

    //폼데이터 추가될떄 무조건 문자열로 처리해주기 떄문에 requestParam으로 처리해줘야됨

    formData.append('place', writeForm['place'].value); //첫번째 장소값
    formData.append('address', writeForm['address'].value); //두번째 장소값
    formData.append('dayStr', writeForm['day'].value);
    formData.append('timeStr', writeForm['time'].value);
    formData.append('limit', writeForm['limit'].value);
    formData.append('latitude', writeForm['lat'].value); //위도
    formData.append('longitude', writeForm['lng'].value); //경도
    formData.append('category', writeForm['category'].value); //카테고리값
    formData.append('title', articleForm['title'].value); //제목값
    formData.append('content', articleForm['content'].value); //ck에디터 내용 가져오기
    formData.append('thumbnailMultipart', articleForm['upload'].files[0]);

    for (let i = 0; i < tags.length; i++) { //태그 반복해서 나타내기
        formData.append('hashtag', tags[i].value);
    }

    const index = articlePatch.dataset.index;

    xhr.open('PATCH', `./patch?index=${index}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'success':
                        alert('수정에 성공했습니다');
                        window.location.href = '/article/read?index=' + index; //수정되면 게시판 바로 이동
                        break;
                    case 'failure':
                       alert('수정에 실패했습니다');
                        break;
                    default:
                       alert('서버 실패');
                }
            } else {
                alert('서버 실패');
            }
        }
    };
    xhr.send(formData);
}









