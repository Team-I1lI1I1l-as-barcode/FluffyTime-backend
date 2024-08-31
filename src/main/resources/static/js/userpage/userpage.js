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

const unblockComment = getElement('unblock_comment'); // 차단됨 문구
// const follower_count = getElement("follower_count"); // 팔로워 수
// const follow_count = getElement("follow_count");// 팔로우 수

// div 영역
const noPost = getElement('no_post'); // 게시물이 없을시 나올 div
const privateAccount = getElement('private_account'); // 비공개 계정일시 나올 div
const blockAccount = getElement('block_account'); // 차단된 게정일시 나올 div

// 모달 관련 요소
const userPageModal = document.getElementById('userPage-modal');
const userPageOverlay = document.getElementById('userPage-modal-overlay');
const closeModalButtons = document.querySelectorAll('#block_cancel');
const blockFollow = getElement('block_follow'); // 유저 차단
const blockFollowCancel = getElement('block_follow_cancel'); // 유저 차단
const chat = getElement('chat') // 메시지 보내기

// api  요청  함수
function fetchUserPage(url, method, func) {
  console.log("fetchMyPage 실행");
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'

    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // 에러 메시지 포함하여 alert 호출
        console.log("fetchUserPage 응답 에러 발생 >> " + errorData.message);
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
  // 해당 유저를 차단 했을때
  if (data.isUserBlocked) {
    blockAccount.style.display = "flex"; // 차단한 계정  div 활성화
    blockFollow.style.display = "none"; // (모달) 유저 차단 비활성화
    blockFollowCancel.style.display = "block"; // (모달) 유저 차단 해제 활성화
    followBtn.style.display = "none"; // 팔로우 버튼 비활성화
    unblockComment.style.display = "inline-flex"; // 유저 차단된 문구 활성화
  } else {
    // 해당 유저의 계정이 공개 계정일때
    if (data.publicStatus === "1") {
      (data.postsList !== null) ? renderPosts(data.postsList)
          : noPost.style.display = 'flex';

    } else { // 해당 유저의 계정이 비공개 계정일때
      // 추후 팔로우 관계일시에는 게시물이 보이게 변경할 예정
      // if (data.postsList !== null) {
      //   renderPosts(data.postsList);
      // } else {
      // 게시글이 없을시 문구 출력
      privateAccount.style.display = 'flex';
      // }
    }
  }
}

// 게시물 목록을 렌더링하는 함수
function renderPosts(posts) {
  console.log("renderPosts 실행");
  const postListElement = document.querySelector('#post_list');

  posts.forEach(post => {
    if (post.fileUrl != null) {
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
  });
}

async function checkFollowStatus() {
  console.log("checkFollowStatus() 실행")
  const response = await fetch(
      '/api/follow/status', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });
}

// 채팅관련 서버 결과를 처리하는 함수
function serverResponse(data) {
  if (data.success) {
    console.log("요청 성공")
    window.location.href = "/chat";
  } else {
    console.log("요청 실패")
  }
}

// 초기화 함수
function initialize() {
  const nickname = window.location.pathname.split('/').pop();

  // 초기화 - 유저페이지 정보 불러오기
  fetchUserPage(`/api/users/pages?nickname=${encodeURIComponent(nickname)}`,
      "GET",
      handleUserData);

  // 초기화 - ... 버튼시 파일 선택 버튼이 눌림
  more.addEventListener('click', event => {
    event.preventDefault();
    // 모달 창 열기
    userPageOverlay.style.display = 'block';
    userPageModal.classList.add('show');
  });
//초기화 - 모달 닫기
  closeModalButtons.forEach(button => {
    button.addEventListener('click', (event) => {
      event.preventDefault();
      userPageModal.classList.remove('show');
      userPageOverlay.style.display = 'none';
    });
  });

  // 초기화 - 메시지 보내기
  chat.addEventListener('click', (event) => {
    event.preventDefault();
    // 메시지 방 생성
    fetchUserPage(`/chat/topics/${encodeURIComponent(nickname)}`, "PUT",
        serverResponse);
  });
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;