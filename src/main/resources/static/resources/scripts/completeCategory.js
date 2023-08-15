const Articles = document.getElementById('Article');
const categories = Articles.querySelectorAll('.category-item');
const articleLinks = Articles.querySelectorAll('.articleLink');

let url = window.location.href; //현재 컴퓨터가 실행되고있는 URL을 가져온다.

var categoriesParam = new URL(url).searchParams.get("category"); //url에서 category라는 매개변수의 값을 가져온다

if(!categoriesParam){ //전체
    articleLinks[0].classList.add('underline');
}else if(categoriesParam === '영화'){
    articleLinks[1].classList.add('underline');
}else if(categoriesParam === '게임'){
    articleLinks[2].classList.add('underline');
}else if(categoriesParam === '운동'){
    articleLinks[3].classList.add('underline');
}else if(categoriesParam === '산책'){
    articleLinks[4].classList.add('underline');
}else if(categoriesParam === '식사'){
    articleLinks[5].classList.add('underline');
}else if(categoriesParam === '만남'){
    articleLinks[6].classList.add('underline');
}else if(categoriesParam === '완료'){
    articleLinks[7].classList.add('underline');
}



const boardLists = document.getElementsByClassName("article-list");

const board = Array.from(boardLists);

board.forEach(function (board) {
    let images = board.getElementsByClassName("Profile-Picture");
    if (images.length >= 4) {
        const covers = document.createElement("img");
        covers.src = "resources/images/mainImages/cover.png";
        covers.classList.add("cover");
        // images[3]의 자식으로 cover 추가
        images[3].appendChild(covers);
        for (let i = 4; i < images.length; i++) {
            images[i].style.display = "none";
        }
    }
});


