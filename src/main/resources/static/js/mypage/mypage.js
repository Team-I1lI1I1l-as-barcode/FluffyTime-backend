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
const ProfileImagePreview = getElement('Profile-image-Preview'); // 프로필 이미지 영역
// const follower_count = getElement("follower_count"); // 팔로워 수
// const follow_count = getElement("follow_count");// 팔로우 수
const postCreate = getElement("post_create"); // 게시물 추가 + 기호
let hasProfile; // 프로필 존재 여부

// api  요청  함수
function fetchMyPage(url, method, func) {
  console.log("fetchMyPage 실행");
  fetch(url, {
    method: method, // GET 요청
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

// 사진 등록/수정 api 요청 함수
function fetchProfileImage(method, url) {
  const profileForm = document.getElementById('profileImageForm');
  const formData = new FormData(profileForm);
  fetch(url, {
    method: method,
    body: formData
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.error(errorData.message);
        window.location.href = "/";
        throw new Error(errorData.message);
      });
    }
    return response.json();
  })
  .then(data => {
    if (data.result) {
      alert("사진이 정상적으로 등록되었습니다.");
      img.src = data.fileUrl; // img의 src 속성을 서버에서 반환된 URL로 업데이트
      window.location.reload(); // 새로 고침
    } else {
      alert("사진 등록에 실패하였습니다.");
    }
  })
  .catch(error => console.error("서버 오류 발생: " + error));
}

// 마이페이지 정보 로드 함수
function handleProfileData(data) {
  // 성공적인 응답 시
  console.log("fetchMyPage 응답 Success");
  nickName.innerText = data.nickname;
  hasProfile = data.profile;
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

// 프로필 편집 페이지로 이동하는 함수
function myPageEdit(data) {
  window.location.href = `/mypage/profile/edit/${data.nickname}`;
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

// 프로필 편집 버튼 설정 함수
function setupProfileEditButton() {
  console.log("setupProfileEditButton 실행");
  getElement("profile_edit_button").addEventListener('click', () => {
    fetchMyPage("/api/mypage/info", "GET", myPageEdit); // 프로필 편집 url 설정
  });
}

// 프로필 등록 API 후처리 함수
function handleCreateProfile(data) {
  if (!data.result) {
    console.log("프로필 등록이 실패되었습니다.");
    window.location.href = "/";
  } else {
    console.log("프로필 등록되었습니다.");
  }
}

// 초기화 함수
function initialize() {
  console.log("initialize 실행");

  const profileForm = document.getElementById('profileImageForm');
  const nickname = window.location.pathname.split('/').pop();

  setupProfileEditButton();

  fetchMyPage("/api/mypage/info", "GET", handleProfileData); // 마이페이지 정보 불러오기

  // 초기화 - 프로필 사진 클릭시 파일 선택 버튼이 눌림
  ProfileImagePreview.addEventListener('click', event => {
    event.preventDefault();
    // 프로필 미 생성시 마이페이지에서 프로필 사진 클릭하여 사진 변경하지 못하도록 막기
    if (!hasProfile) {
      fetchMyPage("/api/mypage/profiles/reg", "POST", handleCreateProfile);
    }
    // 프로필이 존재할시 프로필 이미지 등록
    document.getElementById("images").click();

  });

  // 초기화 - 프로필 사진 등록
  document.getElementById("images").addEventListener('change', event => {
    event.preventDefault();

    // 기본 이미지일 경우 이미지 등록 api 요청
    if (img.src === "../../../image/profile/profile.png") {
      console.log("프로필 사진 등록 api 요청")
      fetchProfileImage('POST',
          `/api/mypage/profiles/images/reg?nickname=${encodeURIComponent(
              nickname)}`);
    } else { // 아닐 경우 이미지 업데이트 api 요청
      console.log("프로필 사진 업데이트 api 요청")
      fetchProfileImage('PATCH',
          `/api/mypage/profiles/images/edit?nickname=${encodeURIComponent(
              nickname)}`);
    }
  });

  // // 초기화 - 게시물이 없을때 + 버튼을 누를시 게시글 생성으로 페이지로 이동
  // postCreate.addEventListener('click', (event) => {
  //   event.preventDefault();
  //   window.location.href = "/posts/reg";
  // });

}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;