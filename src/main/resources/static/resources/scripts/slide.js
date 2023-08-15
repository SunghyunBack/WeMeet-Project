const body = document.getElementById('body');

window.onload = function() {
   document.body.style.transitionDuration = '600ms'; // 애니메이션 지속 시간 설정
    document.body.style.transitionTimingFunction = 'ease'; // 타이밍 함수 설정
    document.body.style.transitionProperty = 'opacity'; // 타이밍 함수 설정
    document.body.style.opacity = '1';
}


document.addEventListener("DOMContentLoaded", function () {
    // 현재 슬라이드의 인덱스를 나타내는 변수
    let sliderIndex = 0;

    // 일정한 시간 간격으로 슬라이드를 자동으로 이동시키는 기능
    setInterval(() => {
        // 다음 슬라이드 인덱스 계산
        sliderIndex++;
        if (sliderIndex >= navigationButtons.length) {
            // 인덱스가 버튼 개수를 초과하면 처음으로 돌아가기
            sliderIndex = 0;
        }
        // 슬라이드를 해당 인덱스로 이동시키는 함수 호출
        slideTo(sliderIndex);
    }, 4400);


    // 슬라이드 너비를 나타내는 변수 (첫 번째 .slide 요소의 너비를 사용)
    let slideWidth = document.querySelector('.slide').clientWidth;

// 슬라이더 전체를 감싸는 부모 요소
    const slider = document.querySelector('.slider');

// 슬라이드 이동을 제어하는 네비게이션 버튼들을 나타내는 변수
    const navigationButtons = document.querySelectorAll('.navigation-button');

// 슬라이드를 해당 인덱스로 이동하는 함수
    function slideTo(index) {
        sliderIndex = index;
        // 슬라이드를 이동시키는 부분: 현재 슬라이드의 인덱스에 따라 transform을 변경
        slider.style.transform = `translateX(${-sliderIndex * slideWidth}px)`;

        // 활성화된 버튼 표시: 현재 슬라이드에 해당하는 버튼에 'active' 클래스 추가, 나머지 버튼은 클래스 제거
        navigationButtons.forEach((button, buttonIndex) => {
            button.classList.toggle('active', buttonIndex === sliderIndex);
        });
    }

// 네비게이션 버튼 클릭 시 해당 페이지로 이동하는 이벤트 리스너 등록
    navigationButtons.forEach((button, index) => {
        button.addEventListener('click', () => {
            slideTo(index);
        });
    });

// 슬라이더 크기를 업데이트하는 함수
    function updateSliderSize() {
        slideWidth = document.querySelector('.slide').clientWidth;
        slideTo(sliderIndex); // 슬라이드 크기가 업데이트되면 현재 슬라이드로 이동
    }

// 뷰포트 크기가 변경될 때 슬라이더 크기를 업데이트
    window.addEventListener('resize', updateSliderSize);

// 페이지 로드 시 슬라이더 크기를 초기화
    updateSliderSize();
});
