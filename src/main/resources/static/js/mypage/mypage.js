// DOM 요소 선택 함수
const getElement = (id) => document.getElementById(id);

// DOM 요소 변수 선언
const nickName = getElement("nickename"); // 닉네임
const posts_count = getElement("posts_count"); // 게시물 개수
const pet_name = getElement("pet_name"); // 반려동물 이름
const pet_sex = getElement("pet_sex"); // 반려동물 성별
const pet_age = getElement("pet_age"); // 반려동물 나이
const intro = getElement("intro"); // 소개글
// const follower_count = getElement("follower_count"); // 팔로워 수
// const follow_count = getElement("follow_count");// 팔로우 수

// 마이페이지 정보를 가져오는 함수
function fetchMyPage(url) {
  console.log("fetchMyPage 실행");
  fetch(url, {
    method: "GET", // GET 요청
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.json()) // 서버에서 보낸 응답을 JSON 형식으로 변환
  .then(data => {
    if (data.code !== "200") { // 오류 발생 시
      alert(data.message);
      console.log("fetchMyPage 응답 에러 발생 >> " + data.message);
      window.location.href = "/";
    } else { // 성공적인 응답 시
      console.log("fetchMyPage 응답 Success");
      nickName.innerText = data.nickname;
      posts_count.innerText = data.postsList.length;
      pet_name.innerText = data.petName;
      pet_sex.innerText = data.petSex;
      pet_age.innerText = data.petAge;
      intro.innerText = data.intro;
      renderPosts(data.postsList);
    }
  })
  .catch(error => {
    console.log("서버 오류 발생:" + error);
  });
}

// 게시물 목록을 렌더링하는 함수
function renderPosts(posts) {
  console.log("renderPosts 실행");
  const postListElement = getElement('post_list');
  postListElement.innerHTML = ''; // 기존 게시물 리스트 비우기

  posts.forEach(post => {
    const li = document.createElement('li');
    li.textContent = post.title; // 게시물 제목 추가
    postListElement.appendChild(li);
  });
}

// 프로필 편집 버튼 설정 함수
function setupProfileEditButton() {
  console.log("setupProfileEditButton 실행");
  getElement("profile_edit_button").addEventListener('click', () => {
    window.location.href = "/mypage/profile/edit/test";
  });
}

// 초기화 함수
function initialize() {
  console.log("initialize 실행");

  setupProfileEditButton();

  // 현재 URL 경로에서 파라미터 추출
  const nickname = window.location.pathname.split('/').pop();
  fetchMyPage(`/api/mypage/info?nickname=${encodeURIComponent(nickname)}`);
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;
