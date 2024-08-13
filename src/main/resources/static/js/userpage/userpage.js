// DOM 요소 선택 함수
const getElement = (id) => document.getElementById(id);

// DOM 요소 변수 선언
const nickName = getElement("nickename"); // 닉네임
const posts_count = getElement("posts_count"); // 게시물 개수
const pet_name = getElement("pet_name"); // 반려동물 이름
const pet_sex = getElement("pet_sex"); // 반려동물 성별
const pet_age = getElement("pet_age"); // 반려동물 나이
const intro = getElement("intro"); // 소개글
const img = getElement('img'); // 이미지 미리보기
const more = getElement('more'); // ``` 버튼

// 모달 관련 요소
const blockFollow = getElement('block_follow'); // 유저 차단
// const blockFollowCancel = getElement('block_follow_cancel'); // 유저 차단 해제
const modal = document.getElementById('modal');
const overlay = document.getElementById('modal-overlay');
const closeModalButtons = document.querySelectorAll('#block_cancel');

// const follower_count = getElement("follower_count"); // 팔로워 수
// const follow_count = getElement("follow_count");// 팔로우 수

// api  요청  함수
function fetchMyPage(url, func) {
  console.log("fetchMyPage 실행");
  fetch(url, {
    method: "GET", // GET 요청
    headers: {
      'Content-Type': 'application/json'

    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // 에러 메시지 포함하여 alert 호출
        console.log("fetchMyPage 응답 에러 발생 >> " + errorData.message);
        alert('Error: ' + errorData.message);
        window.location.href = "/";
      });
    }
    return response.json();
  }) // 서버에서 보낸 응답을 JSON 형식으로 변환
  .then(data => func(data))
  .catch(error => {
    console.log("서버 오류 발생:" + error);
  });
}

// 유저페이지 정보 로드 함수
function handleUserData(data) {
  // 성공적인 응답 시
  console.log("handleUserData 응답 Success");
  nickName.innerText = data.nickname;
  if (data.postsList === null) {
    posts_count.innerText = 0;
  } else {
    posts_count.innerText = data.postsList.length;
  }
  pet_name.innerText = data.petName;
  if (data.petSex === "none") {
    pet_sex.innerText = " ";
  } else {
    pet_sex.innerText = data.petSex;
  }
  if (data.petAge === null || data.petAge === 0) {
    pet_age.innerText = " ";
  } else {
    pet_age.innerText = data.petAge + "살";
  }
  if (data.fileUrl !== null) {
    console.log("등록된 프로필 사진을 불러옵니다.");
    img.src = data.fileUrl;
  }
  intro.innerText = data.intro;

  // 해당 유저의 게시글이 있을시 렌더링 처리
  if (data.postsList !== null) {
    renderPosts(data.postsList);
  } else { // 게시글이 없을시 관련 문구 출력
    getElement('no_post').style.display = 'flex';
  }
}

// 게시물 목록을 렌더링하는 함수
function renderPosts(posts) {
  console.log("renderPosts 실행");
  const postListElement = document.querySelector('#post_list');

  posts.forEach(post => {
    const img = document.createElement('img'); // <img> 요소 생성
    img.src = post.imageUrl; // 이미지 URL 설정
    img.alt = post.postId;

    // 이미지 클릭시 해당 게시물 상세보기 모달창 열기
    img.addEventListener('click', event => {
      console.log(img.alt + "게시물 클릭 ");
      window.location.href = `/posts/detail/${img.alt}`;
    });

    postListElement.appendChild(img); // <img>를 섹션에 추가
  });
}

// 초기화 함수
function initialize() {
  const nickname = window.location.pathname.split('/').pop();

  // 초기화 - 마이페이지 정보 불러오기
  fetchMyPage(`/api/users/pages?nickname=${encodeURIComponent(nickname)}`,
      handleUserData);

  // 초기화 - ... 버튼시 파일 선택 버튼이 눌림
  more.addEventListener('click', event => {
    event.preventDefault();
    // 모달 창 열기
    overlay.style.display = 'block';
    modal.classList.add('show');
  });
// 모달 닫기
  closeModalButtons.forEach(button => {
    button.addEventListener('click', (event) => {
      event.preventDefault();
      modal.classList.remove('show');
      overlay.style.display = 'none';
    });
  });
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;