//무한 스크롤 구현을 위한 변수 선언
let currentPage = 1;
const itemsPerPage = 24;
let isLoading = false;

// 게시물 리스트를 반환하는 함수
async function getExplorePosts(page = 1) {
  try {
    // 현재 페이지의 URL 가져오기
    const currentUrl = new URL(window.location.href);

    // URLSearchParams 객체 생성
    const params = new URLSearchParams(currentUrl.search);

    let url = `/api/explore?page=${page}&perPage=${itemsPerPage}`;
    if (params.has('tag')) {    // 태그 파라미터가 존재하는지 확인

      url += `&tag=${encodeURIComponent(params.get('tag'))}`;
    }

    console.log("api 호출");
    const response = await fetch(
        url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const data = await response.json();

    if (!response.ok) {
      console.error('Request failed:', data.message || 'error');
      throw new Error(data.message || 'error');
    }

    // 클라이언트에게 데이터가 잘 들어왔는지 확인
    console.log('클라이언트가 받은 데이터:', data);

    return data.list || [];

    // return data.list;
  } catch (error) {
    console.error('Error while Getting posts:', error);
    return []; // 오류 발생 시 빈 배열 반환
  }
}

// 게시물 리스트를 받아서 그리드에 아이템을 채우는 함수
async function populateGrid() {

  //만약 스크롤중 더 많은 게시물을 로드 해야 될경우 로딩 스피너를 보여줌
  if (isLoading) {
    console.log('이미 로딩중입니다!');
    return;
  }
  console.log('로딩 시작!');
  isLoading = true;

  try {
    const list = await getExplorePosts(currentPage);
    const gridContainer = document.getElementById('grid-container');

    // 만약 리스트의 길이가 itemsPerPage보다 작으면 더 이상 로드할 데이터가 없음
    if (list.length === 0) {
      console.log('모든 데이터를 불러왔습니다. 더 이상 로드하지 않습니다.');
      // 플래그 설정하여 무한 스크롤을 멈춤
      isLoading = true;  // 로드를 멈추기 위해 isLoading을 true로 유지한다.
      hideSpinner();
      return;
    }

    list.forEach(item => {
      const img = document.createElement('img');
      img.src = item.imageUrl;
      img.alt = item.content; //혹시 에레로 이미지가 없으면 게시물 내용이 대신 나타나게 함
      img.className = 'grid-item';

      // 이미지 클릭 이벤트 리스너 추가
      img.addEventListener('click', () => {
        window.location.href = `posts/detail/${item.postId}`;
      });

      gridContainer.appendChild(img);
    });
    currentPage++;
  } catch (error) {
    console.error('Error populating grid:', error);
  } finally {
    isLoading = false;
    console.log('로딩 끝!');
  }
}

// IntersectionObserver 생성 및 설정
function createObserver() {
  const options = {
    root: null,
    rootMargin: '0px 0px 100px 0px',
    threshold: 0.1
  };
  const spinner = document.getElementById('loading-spinner');
  const observer = new IntersectionObserver(handleIntersect, options);

  if (spinner) {
    observer.observe(spinner);
    console.log('옵저버가 이제 스피너 이벤트를 대기합니다');
  } else {
    console.error('loading-spinner element not found');
  }
}

function handleIntersect(entries) {
  entries.forEach(entry => {
    console.log('handleIntersect 함수 호출, 옵저버가 이제 스피너 이벤트를 대기합니다');
    console.log('entry.boundingClientRect:', entry.boundingClientRect);
    console.log(entry.isIntersecting);

    if (entry.isIntersecting && !isLoading) {
      console.log('스피너가 인식되어 더 많은 게시물을 로드 합니다');
      populateGrid().catch(
          error => console.error('Error in populateGrid:', error));
    }
  });
}

// function showSpinner() {
//   document.getElementById('loading-spinner').style.display = 'block';
//   console.log('showSpinner() 함수 실행');
// }

function hideSpinner() {
  document.getElementById('loading-spinner').style.display = 'none';
  console.log('hideSpinner() 함수 실행');

}

// 페이지 로드 시작 시 그리드를 채움
window.onload = async () => {
  try {

    console.log('화면 생성 시작!');
    await populateGrid().catch(
        error => console.error('Error populating grid:', error));
    console.log('화면 생성 완료!');

    console.log('옵저버 생성 시작!');
    await createObserver();
    console.log('옵저버 생성 완료!');

  } catch (error) {
    console.error('Error during initialization:', error);
  }
  console.log('window.onload 실행완료');
};