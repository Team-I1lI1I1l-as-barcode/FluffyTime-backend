// let follower_count =  document.getElementById("follower_count"); // 팔로워 수
// let follow_count =document.getElementById("follow_count");// 팔로우 수

// 프로필 편집 버튼 클릭시 프로필 화면으로 이동하기
document.getElementById("profile_edit_button").addEventListener('click',
    event => {
      // 로그인 연동 후 /mypage/profile/edit/{id}로 변경할 예정
      window.location.href = "/mypage/profile/edit/1";
    });

// 마이페이지에 접속하자마자 API 요청
window.onload = function () {
  // 현재 URL 경로에서 파라미터 추출하기
  const pathSegments = window.location.pathname.split('/');
// URL 파라미터의 맨 마지막에 존재하는 유저아이디 추출하기
  const userId = pathSegments[pathSegments.length - 1];

  let nickName = document.getElementById("nickename"); // 닉네임
  let posts_count = document.getElementById("posts_count"); // 게시물 개수
  let pet_name = document.getElementById("pet_name"); // 반려동물 이름
  let pet_sex = document.getElementById("pet_sex"); // 반려동물 성별
  let pet_age = document.getElementById("pet_age"); // 반려동물 나이
  let users_intro = document.getElementById("users_intro"); // 소개글

  // 마이페이지에 필요한 정보 요청(API)
  fetch(`/api/mypage/info?userId=${encodeURIComponent(userId)}`, {
    method: "GET", // GET 요청
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.json()) // 서버에서 보낸 응답을 JOSN 형식으로 변환
  // 변환된 데이터를 처리
  .then(data => {
    if (data.error) { // 서버에서 에러 메시지가 반환된 경우
      alert("에러메시지: " + data.error);
    } else { // 성공적인 응답을 받은 경우
      nickName.innerText = data.nickname;
      posts_count.innerText = data.postsList.length;
      pet_name.innerText = data.petName;
      pet_sex.innerText = data.petSex;
      pet_age.innerText = data.petAge;
      users_intro.innerText = data.intro;
      renderPosts(data.postsList);
    }
  })
  // 네트워크 오류나 기타 문제가 발생한 경우 처리
  .catch(error => {
    alert("네트워크 에러 또는 기타 오류 발생:" + error);
  });
}

// 마이페이지 나의 게시글 목록 추가
function renderPosts(posts) {
  const postListElement = document.getElementById('post_list');

  // 기존 게시물 리스트 비우기
  postListElement.innerHTML = '';

  posts.forEach(post => {
    // <li> 요소 생성
    const li = document.createElement('li');
    li.textContent = post.title; // 게시물 제목 추가

    // <li> 요소를 <ul> 요소에 추가
    postListElement.appendChild(li);
  });
}
