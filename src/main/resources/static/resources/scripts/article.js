const Article = document.getElementById('Article');
const category = Article.querySelectorAll('.category-item');
const articleLink = Article.querySelectorAll('.articleLink');

let url = window.location.href; //현재 컴퓨터가 실행되고있는 URL을 가져온다.

let categoryParam = new URL(url).searchParams.get("category"); //url에서 category라는 매개변수의 값을 가져온다

    if (!categoryParam) { //전체
        articleLink[0].classList.add('underline');
    } else if (categoryParam === '운동') {
        articleLink[1].classList.add('underline');
    } else if (categoryParam === '푸드') {
        articleLink[2].classList.add('underline');
    } else if (categoryParam === '문화') {
        articleLink[3].classList.add('underline');
    } else if (categoryParam === '취미') {
        articleLink[4].classList.add('underline');
    } else if (categoryParam === '온라인') {
        articleLink[5].classList.add('underline');
    } else if (categoryParam === '여행') {
        articleLink[6].classList.add('underline');
    } else if (categoryParam === '완료') {
        articleLink[7].classList.add('underline');
    }



