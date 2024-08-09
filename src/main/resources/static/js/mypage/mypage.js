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

// 마이페이지 정보 로드 함수
function handleProfileData(data) {
  // 성공적인 응답 시
  console.log("fetchMyPage 응답 Success");
  nickName.innerText = data.nickname;
  posts_count.innerText = data.postsList.length;
  pet_name.innerText = data.petName;
  if (data.petSex === "none") {
    pet_sex.innerText = " ";
  } else {
    pet_sex.innerText = data.petSex;
  }
  if (data.petAge === 0) {
    pet_age.innerText = " ";
  } else {
    pet_age.innerText = data.petAge;
  }
  if (data.fileUrl !== null) {
    console.log("등록된 프로필 사진을 불러옵니다.");
    img.src = data.fileUrl;
  }
  intro.innerText = data.intro;
  renderPosts(data.postsList);
}

// 프로필 편집 페이지로 이동하는 함수
function myPageEdit(data) {
  window.location.href = `/mypage/profile/edit/${data.nickname}`;
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
    fetchMyPage("/api/mypage/info", myPageEdit); // 프로필 편집 url 설정
  });
}

// 초기화 함수
function initialize() {
  console.log("initialize 실행");
  fetchMyPage("/api/mypage/info", handleProfileData); // 마이페이지 정보 불러오기
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;
