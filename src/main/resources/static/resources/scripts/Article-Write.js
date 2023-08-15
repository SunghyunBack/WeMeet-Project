const articleForm = document.getElementById('Article-Form');


let tagCounter = 0; //전역변수 태그카운터
let tags = [];
var thumbnailPlace = document.querySelector('.thumbnail-place');
var thumbnailUpload = document.querySelector('.thumbnail-upload');
var thumbnailChange = document.querySelector('.thumbnail-change');
const thumbnailTitle = document.querySelector('.thumbnail-title');
const thumbnail1 = document.querySelector('.thumbnail1');


ClassicEditor.create(articleForm['content'], {}); //파일 업로드

const ArticleTag = document.querySelector('.article-tag'); //tag를 담을 부모
const explainTag = document.querySelector('.explainTag'); //설명
const Tags = document.querySelector('.tags');

ArticleTag.addEventListener('click', function () {

    if (event.target.classList.contains('tag')) {
        return; // 이미 생성된 태그를 클릭한 경우 생성 코드 실행하지 않음
    }

    if (tagCounter >= 5) {
        alert('태그는 최대5개까지만 작성가능합니다');
        return; // 태그 개수가 5개 이상인 경우 동작하지 않음
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
    Tag.name = "hashTag" // 태그의 이름
    Tag.value = '#'; // 처음 생성시 # 추가
    TagContainer.classList.add('tag-container');
    TagWarning.classList.add('tag-warning');
    Tag.classList.add('tag'); // 처음 생성시 tag 클래스 추가
    Tag.maxLength = 7; //최대글자 7글자
    Tag.style.width = "120px"; //태그 크기 120px
    Tag.autocomplete = "off"; //자동완성 기능 Off

    Tags.appendChild(TagContainer); // 자식으로 태그를 담는 div 넣음
    TagContainer.appendChild(TagWarning); //태그 7글자 적을시 경구문
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
    Tag.focus(); //태그 생성시 자동으로 포커스되게
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

const abuse = ['ㅅㅂ','ㅆㅂ','시발','씨발','10새','10새기','10새리','10세리','10쉐이','10쉑','10스','10쌔',
    '10쌔기','10쎄','10알','10창','10탱','18것','18넘','18년','18노','18놈',
    '18뇬','18럼','18롬','18새','18새끼','18색','18세끼','18세리','18섹','18쉑','18스','18아',
    'c파','c팔','fuck', 'ㄱㅐ','ㄲㅏ','ㄲㅑ','ㄲㅣ','ㅅㅂㄹㅁ','ㅅㅐ','ㅆㅂㄹㅁ','ㅆㅍ','ㅆㅣ','ㅆ앙','ㅍㅏ','凸',
    '갈보','갈보년','강아지','같은년','같은뇬','개같은','개구라','개년','개놈',
    '개뇬','개대중','개독','개돼중','개랄','개보지','개뻥','개뿔','개새','개새기','개새끼',
    '개새키','개색기','개색끼','개색키','개색히','개섀끼','개세','개세끼','개세이','개소리','개쑈',
    '개쇳기','개수작','개쉐','개쉐리','개쉐이','개쉑','개쉽','개스끼','개시키','개십새기',
    '개십새끼','개쐑','개씹','개아들','개자슥','개자지','개접','개좆','개좌식','개허접','걔새',
    '걔수작','걔시끼','걔시키','걔썌','걸레','게색기','게색끼','광뇬','구녕','구라','구멍',
    '그년','그새끼','냄비','놈현','뇬','눈깔','뉘미럴','니귀미','니기미','니미','니미랄','니미럴',
    '니미씹','니아배','니아베','니아비','니어매','니어메','니어미','닝기리','닝기미','대가리',
    '뎡신','도라이','돈놈','돌아이','돌은놈','되질래','뒈져','뒈져라','뒈진','뒈진다','뒈질',
    '뒤질래','등신','디져라','디진다','디질래','딩시','따식','때놈','또라이','똘아이','똘아이',
    '뙈놈','뙤놈','뙨넘','뙨놈','뚜쟁','띠바','띠발','띠불','띠팔','메친넘','메친놈','미췬',
    '미췬','미친','미친넘','미친년','미친놈','미친새끼','미친스까이','미틴','미틴넘','미틴년',
    '미틴놈','바랄년','병자','뱅마','뱅신','벼엉신','병쉰','병신','부랄','부럴','불알','불할','붕가',
    '붙어먹','뷰웅','븅','븅신','빌어먹','빙시','빙신','빠가','빠구리','빠굴','빠큐','뻐큐',
    '뻑큐','뽁큐','상넘이','상놈을','상놈의','상놈이','새갸','새꺄','새끼','새새끼','새키',
    '색끼','생쑈','세갸','세꺄','세끼','섹스','쇼하네','쉐','쉐기','쉐끼','쉐리','쉐에기',
    '쉐키','쉑','쉣','쉨','쉬발','쉬밸','쉬벌','쉬뻘','쉬펄','쉽알','스패킹','스팽','시궁창','시끼',
    '시댕','시뎅','시랄','시발','시벌','시부랄','시부럴','시부리','시불','시브랄','시팍',
    '시팔','시펄','신발끈','심발끈','심탱','십8','십라','십새','십새끼','십세','십쉐','십쉐이','십스키',
    '십쌔','십창','십탱','싶알','싸가지','싹아지','쌉년','쌍넘','쌍년','쌍놈','쌍뇬','쌔끼',
    '쌕','쌩쑈','쌴년','썅','썅년','썅놈','썡쇼','써벌','썩을년','썩을놈','쎄꺄','쎄엑',
    '쒸벌','쒸뻘','쒸팔','쒸펄','쓰바','쓰박','쓰발','쓰벌','쓰팔','씁새','씁얼','씌파','씨8',
    '씨끼','씨댕','씨뎅','씨바','씨바랄','씨박','씨발','씨방','씨방새','씨방세','씨밸','씨뱅',
    '씨벌','씨벨','씨봉','씨봉알','씨부랄','씨부럴','씨부렁','씨부리','씨불','씨붕','씨브랄',
    '씨빠','씨빨','씨뽀랄','씨앙','씨파','씨팍','씨팔','씨펄','씸년','씸뇬','씸새끼','씹같','씹년',
    '씹뇬','씹보지','씹새','씹새기','씹새끼','씹새리','씹세','씹쉐','씹스키','씹쌔','씹이','씹자지',
    '씹질','씹창','씹탱','씹퇭','씹팔','씹할','씹헐','아가리','아갈','아갈이','아갈통',
    '아구창','아구통','아굴','얌마','양넘','양년','양놈','엄창','엠병','여물통','염병','엿같','옘병',
    '옘빙','오입','왜년','왜놈','욤병','육갑','은년','을년','이년','이새끼','이새키','이스끼',
    '이스키','임마','자슥','잡것','잡넘','잡년','잡놈','저년','저새끼','접년','젖밥','조까',
    '조까치','조낸','조또','조랭','조빠','조쟁이','조지냐','조진다','조찐','조질래','존나','존나게','존니','존만',
    '존만한','좀물','좁년','좁밥','좃까','좃또','좃만','좃밥','좃이','좃찐','좆같','좆까','좆나',
    '좆또','좆만','좆밥','좆이','좆찐','좇같','좇이','좌식','주글','주글래','주데이','주뎅',
    '주뎅이','주둥아리','주둥이','주접','주접떨','죽고잡','죽을래','죽통','쥐랄','쥐롤',
    '쥬디','지랄','지럴','지롤','지미랄','짜식','짜아식','쪼다','쫍빱','찌랄','창녀','캐년',
    '캐놈','캐스끼','캐스키','캐시키','탱구','팔럼','퍽큐','호로','호로놈','호로새끼',
    '호로색','호로쉑','호로스까이','호로스키','후라들','후래자식','후레']; //욕설 전역 배열


articleForm.onsubmit = e => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    if(articleForm['upload'].value === ''){
        alert('썸네일을 업로드 해주세요');
        return;
    }//썸네일 업로드 안했을시

    if(articleForm['title'].value === ''){
        alert('제목을 입력해주세요');
        return;
    } //제목이 비어있을때

    if (!/^[가-힣a-zA-Z\s\d\W]+$/.test(articleForm['title'].value)) {
        alert('제목을 제대로 입력해주세요');
        articleForm['content'].value = '';
        articleForm['content'].focus();
        articleForm['content'].select();
        return;
    }

    if (abuse.some(x => articleForm['title'].value.indexOf(x) > -1)) {
        alert('제목에 욕설이 포함되있습니다 다시 입력해주세요');
        articleForm['title'].value ='';
        articleForm['title'].focus();
        articleForm['title'].select();
        return;
    }//제목 욕설포함


    if(articleForm['content'].value === ''){
        alert('내용을 입력해주세요');
        return;
    } //게시판 내용이 비어있을때


    if (!/^[가-힣a-zA-Z\s\d\W]+$/.test(articleForm['content'].value)) {
        alert('내용을 제대로 입력해주세요');
        articleForm['content'].value = '';
        articleForm['content'].focus();
        articleForm['content'].select();
        return;
    }



    if (abuse.some(x => articleForm['content'].value.indexOf(x) > -1)) {
        alert('내용에 부적절한 단어가 포함되있습니다 다시 입력해주세요');
        return;
    }//게시판 욕설

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

    xhr.open('POST', '/write');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    const index = response.index;
                    if (index) {
                        window.location.href = '/article/read?index=' + index;
                    } else {
                        alert('게시판 작성에 실패하였습니다. 인덱스 값을 받아오지 못했습니다.');
                    }
                } catch (error) {
                    console.error(error);
                    alert('서버 응답을 처리하는 중 오류가 발생했습니다.');
                }

            } else {
                alert('게시판 작성에 실패하였습니다');
            }
        }
    };
    xhr.send(formData);
};








