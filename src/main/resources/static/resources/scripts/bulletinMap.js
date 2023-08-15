const mapContainer = document.getElementById('map'),
    mapOption = {
        center: new kakao.maps.LatLng(bulletinForm['lat'].value,bulletinForm['lng'].value),
        level: 3
    };

const map = new kakao.maps.Map(mapContainer, mapOption);


const markerPosition  = new kakao.maps.LatLng(bulletinForm['lat'].value,bulletinForm['lng'].value);


const marker = new kakao.maps.Marker({
    position: markerPosition
});


marker.setMap(map);

var articlePlace = bulletinForm['mapPlace'].value;


// var iwContent = '<div style="padding:5px;">' + articlePlace + '<br><a href="https://map.kakao.com/link/map/Hello World!,33.450701,126.570667" style="color:blue" target="_blank">큰지도보기</a> <a href="https://map.kakao.com/link/to/Hello World!,33.450701,126.570667" style="color:blue" target="_blank">길찾기</a></div>'; // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다
var iwContent = '<div style="padding:5px;">' + articlePlace; // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다

    iwPosition = new kakao.maps.LatLng(bulletinForm['lat'].value,bulletinForm['lng'].value); //인포윈도우 표시 위치입니다

// 인포윈도우를 생성합니다
var infowindow = new kakao.maps.InfoWindow({
    position : iwPosition,
    content : iwContent
});

// 마커 위에 인포윈도우를 표시합니다. 두번째 파라미터인 marker를 넣어주지 않으면 지도 위에 표시됩니다
infowindow.open(map, marker);