// 예제 리스트를 반환하는 함수
async function getExplorePosts() {
  try {

    // 현재 페이지의 URL 가져오기
    const currentUrl = new URL(window.location.href);

// URLSearchParams 객체 생성
    const params = new URLSearchParams(currentUrl.search);

    let url = '/api/explore';
    if (params.has('tag')) {
      url += `?tag=${encodeURIComponent(params.get('tag'))}`;
    }

// 특정 파라미터가 존재하는지 확인
    const hasParam = params.has('tag');

    const response = await fetch(
        url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const data = await response.json();

    if (!response.ok) {
      alert("request not handled")
      throw new Error(data.message || "error");
    }
    console.log(data.list.length)
    return data.list;
  } catch (error) {
    console.error(error);
  }
}

// 리스트를 받아서 그리드에 아이템을 채우는 함수
async function populateGrid() {
  // TODO getExplorePosts 함수 구현하기
  const list = await getExplorePosts();
  const gridContainer = document.getElementById('grid-container');
  list.forEach(item => {
    const img = document.createElement('img');
    img.src = item.imageUrl;
    img.alt = item.title;
    img.className = 'grid-item';
    // 이미지 클릭 이벤트 리스너 추가
    img.addEventListener('click', () => {
      openPopup(item.imageUrl);
    });
    gridContainer.appendChild(img);
  });
}

// 팝업을 열고 이미지를 표시하는 함수
function openPopup(imageUrl) {
  const popup = document.getElementById('popup');
  const popupImage = document.getElementById('popup-image');
  popupImage.src = imageUrl;
  popup.style.display = 'flex';
}

// 팝업을 닫는 함수
function closePopup() {
  const popup = document.getElementById('popup');
  popup.style.display = 'none';
}

// 팝업 닫기 버튼에 이벤트 리스너 추가
document.getElementById('popup-close').addEventListener('click', closePopup);
document.getElementById('popup').addEventListener('click', (event) => {
  if (event.target.id === 'popup') {
    closePopup();
  }
});

// 페이지 로드 시 그리드를 채움
window.onload = populateGrid;