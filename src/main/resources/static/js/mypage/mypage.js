// DOM 요소 선택 함수
const getElement = (id) => document.getElementById(id);

// DOM 요소 변수 선언
const posts_count = getElement("posts_count"); // 게시물 개수
const pet_name = getElement("pet_name"); // 반려동물 이름
const pet_sex = getElement("pet_sex"); // 반려동물 성별
const pet_age = getElement("pet_age"); // 반려동물 나이
const intro = getElement("intro"); // 소개글
const img = getElement('img'); // 이미지 미리보기
const profileImagePreview = getElement('Profile-image-Preview'); // 프로필 이미지 영역
const bookmark = getElement('bookmark');
const mention = getElement('mention');

// 기본 api  요청  함수
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

// 프로필 사진 등록/수정 api 요청 함수
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
      console.log("프로필 사진 등록 성공");
      img.src = data.fileUrl;
      window.location.reload(); // 새로 고침
    } else {
      alert("프로필 사진 등록에 실패");
    }
  })
  .catch(error => console.error("서버 오류 발생: " + error));
}

// 마이페이지 정보 로드 함수
function handleProfileData(data) {
  // 성공적인 응답 시
  console.log("fetchMyPage 응답 Success");

  getElement("nickname").innerText = data.nickname;
  posts_count.innerText = data.postsList === null ? 0 : data.postsList.length;
  pet_name.innerText = data.petName;
  pet_sex.innerText = data.petSex === "none" ? " " : data.petSex;
  pet_age.innerText = (data.petAge === null || data.petAge === 0) ? " "
      : data.petAge + "살";
  intro.innerText = data.intro;

  if (data.fileUrl !== null) {
    console.log("등록된 프로필 사진 로드");
    img.src = data.fileUrl;
  }

  // 해당 유저의 게시글이 있을시 렌더링 처리
  if (data.postsList !== null) {
    renderPosts(data.postsList);
  } else {
    // 게시글이 없을시 문구 출력
    getElement('no_post').style.display = 'flex';
  }
  // 북마크 클릭시 북마크 게시글 렌더링 처리
  bookmark.addEventListener('click', (event) => {
    const postListElement = document.querySelector('#post_list');
    postListElement.innerHTML = ''; // 기존 리스트 비우기
    renderPosts(data.bookmarkList);
  });

  // 저장됨 클릭시 멘션된 게시글 렌더링 처리
  mention.addEventListener('click', (event) => {
    const postListElement = document.querySelector('#post_list');
    postListElement.innerHTML = ''; // 기존 리스트 비우기
    renderPosts(data.tagePostList);
  });
}

// 게시물 목록을 렌더링하는 함수
function renderPosts(posts) {
  console.log("renderPosts 실행");
  const postListElement = document.querySelector('#post_list');

  posts.forEach(post => {
    if (post.fileUrl != null) {
      if (post.mineType === "video/mp4") {
        const video = document.createElement('video'); // <img> 요소 생성
        video.classList.add('mypage_video');
        video.src = post.fileUrl;
        // 비디오 자동 재생 끄기
        video.addEventListener('play', function (event) {
          event.preventDefault();
          this.pause();
        });
        // 비디오 클릭시 해당 게시물 상세보기 모달창 열기
        video.addEventListener('click', event => {

          window.location.href = `/posts/detail/${post.postId}`;
        });
        postListElement.appendChild(video); // <video>를 섹션에 추가

      } else {
        const img = document.createElement('img'); // <img> 요소 생성
        img.src = post.fileUrl; // 이미지 URL 설정
        img.alt = post.postId;

        // 이미지 클릭시 해당 게시물 상세보기 모달창 열기
        img.addEventListener('click', event => {
          console.log(img.alt + "게시물 클릭 ");
          window.location.href = `/posts/detail/${img.alt}`;
        });
        postListElement.appendChild(img); // <img>를 섹션에 추가
      }
    }
  });
}

// 초기화 함수
function initialize() {
  const nickname = window.location.pathname.split('/').pop();

  // 초기화 - 프로필 편집 버튼 클릭시 프로필 페이지로 이동
  getElement("profile_edit_button").addEventListener('click', () => {
    window.location.href = `/mypage/profile/edit/${nickname}`;
  });

  // 초기화 - 마이페이지 정보 불러오기
  fetchMyPage("/api/mypage/info", "GET", handleProfileData);

  // 초기화 - 프로필 사진 클릭시 파일 선택 버튼이 눌림
  profileImagePreview.addEventListener('click', event => {
    event.preventDefault();
    document.getElementById("mypage-images").click();  // 프로필 이미지 등록

  });

  // 초기화 - 프로필 사진 업데이트
  document.getElementById("mypage-images").addEventListener('change', event => {
    event.preventDefault();
    console.log("프로필 사진 업데이트 api 요청")
    fetchProfileImage('PATCH',
        `/api/mypage/profiles/images/edit?nickname=${encodeURIComponent(
            nickname)}`);

  });
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;