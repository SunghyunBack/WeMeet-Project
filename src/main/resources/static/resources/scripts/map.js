const writeForm = document.getElementById('writeForm');
const placeInput = writeForm.querySelector('.place');
const addressSecondaryInput = writeForm.querySelector('.addressSecondary');
const dayInput = document.querySelector('.day');
const today = new Date().toISOString().split('T')[0];
const nextButton = document.querySelector(".next");
const inner = document.querySelector('.inner');


// 마커를 담을 배열입니다
var markers = [];


var mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };

// 지도를 생성합니다
var map = new kakao.maps.Map(mapContainer, mapOption);

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

// 검색 결과 목록이나 마커를 클릭했을 때 장소명을 표출할 인포윈도우를 생성합니다
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

const searchForm = document.getElementById('searchForm');

searchForm.onsubmit = function(e) {
    e.preventDefault();
    ps.keywordSearch( searchForm['keyword'].value, placesSearchCB);
    return false;
}

// 키워드로 장소를 검색합니다


// 키워드 검색을 요청하는 함수입니다
// function searchPlaces() {
//     var k = document.getElementById('keyword').value;
//     alert(k);
//     if (!k.replace(/^\s+|\s+$/g, '')) {
//         alert('키워드를 입력해주세요!');
//         return false;
//     }
//
//     // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
//     ps.keywordSearch( k, placesSearchCB);
// }

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);

    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

        alert('검색 결과가 존재하지 않습니다.');
        return;

    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;

    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlaces(places) {

    var listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        bounds = new kakao.maps.LatLngBounds(),
        listStr = '';

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    for ( var i=0; i<places.length; i++ ) {

        // 마커를 생성하고 지도에 표시합니다
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i),
            itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(placePosition);

        // 마커와 검색결과 항목에 mouseover 했을때
        // 해당 장소에 인포윈도우에 장소명을 표시합니다
        // mouseout 했을 때는 인포윈도우를 닫습니다
        (function(marker, title) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            itemEl.onmouseover =  function () {
                displayInfowindow(marker, title);
            };

            itemEl.onmouseout =  function () {
                infowindow.close();
            };
        })(marker, places[i].place_name);



        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
    map.setBounds(bounds);
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {

    var el = document.createElement('li'),
        itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
            '<div class="info">' +
            '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
            '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>';
    }

    itemStr += '  <span class="tel">' + places.phone  + '</span>' +
        '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';


    //목록 클릭 이벤트
    el.addEventListener('click', function() {

        writeForm['place'].value=places.place_name;
        writeForm['address'].value=places.address_name;
        // addressPrimaryInput.value = places.place_name;
        // addressSecondaryInput.value = places.address_name;
        // placeInput.disabled = true;

        // Move and zoom to the corresponding marker
        map.setLevel(2); // Adjust the zoom level as needed
        map.setCenter(new kakao.maps.LatLng(places.y, places.x));


        // 클릭시 마커의 좌표(위도,경도)를 가져온다(hidden으로 숨겨둠)
        // writeForm.querySelector('input[name="lat"]').value = places.y;
        // writeForm.querySelector('input[name="lng"]').value = places.x;
        writeForm['lat'].value=places.y;
        writeForm['lng'].value=places.x;

    });

    return el;
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, idx, title) {
    var imageSrc =
            "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png", // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(36, 37), // 마커 이미지의 크기
        imgOptions = {
            spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin: new kakao.maps.Point(0, idx * 46 + 10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37), // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(
            imageSrc,
            imageSize,
            imgOptions
        ),
        marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage,
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker); // 배열에 생성된 마커를 추가합니다

    //마커 클릭 이벤트
    //마커를 클릭시 마커의 번호와 일치하는 목록을 클릭하는 이벤트
    (function (marker, idx, title) {
        kakao.maps.event.addListener(marker, "click", function () {
            var listItem = document.getElementById("placesList").childNodes[idx];
            if (listItem) {
                listItem.click();
            }
        });
    })(marker, idx, title);

    return marker;
}// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

// 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
// 인포윈도우에 장소명을 표시합니다
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

// 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}



// 현재의 날짜보다 과거의 날짜 선택 불가능
dayInput.setAttribute('min', today);

nextButton.addEventListener("click", function(event) {
    event.preventDefault();

    if (writeForm['place'].value === '') {
        // 주소 미입력
        alert('지도에서 장소를 검색해 주세요.');
        return;
    }
    if (writeForm['day'].value === '') {
        //날짜 미입력
        alert('날짜를 설정해주세요');
        return;
    }
    if (writeForm['time'].value === '') {
        //약속시간 미입력
        alert('약속시간을 설정해 주세요.');
        return;
    }
    if (writeForm['limit'].value === '') {
        //참여인원 미입력
        alert('인원제한을 설정해주세요');
        return;
    }
    if (writeForm['category'].value === '') {
        //카테고리 미설정
        alert('카테고리를 설정해주세요');
        return;
    }

    inner.style.display = "none";
    articleForm.style.display='block';
});


//창 크기에 따른 placeholder 수정



if (window.innerWidth <= 805) {
    placeInput.placeholder="장소";
} else {
    placeInput.placeholder="장소(지도에서 장소를 검색해 주세요)";
}

window.addEventListener('resize', () => {
    if (window.innerWidth <= 805) {
        placeInput.placeholder="장소";
    } else {
        placeInput.placeholder="장소(지도에서 장소를 검색해 주세요)";
    }
});


function goBack(){
    window.history.back();
}