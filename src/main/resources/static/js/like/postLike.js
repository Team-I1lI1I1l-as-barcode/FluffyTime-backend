document.addEventListener('DOMContentLoaded', () => {
  initializeLikeStatus(postId);
});

async function initializeLikeStatus(postId) {
  try {
    const response = await fetch(`/api/posts/detail/${postId}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    if (data) {
      likeButton.innerHTML = data.liked ? 'favorite' : 'favorite_border'; // 버튼 모양 업데이트
      likeButton.classList.toggle('liked', data.liked);
      likeCountSpan.textContent = `${data.likeCount}`; // 좋아요 수 업데이트
    }
  } catch (error) {
    console.error('Error initializing like status:', error);
  }
}

//좋아요 등록/취소
async function toggleLikePost(postId, likeButton, likeCountSpan) {
  try {
    const isLiked = likeButton.classList.contains('liked');
    const method = isLiked ? 'DELETE' : 'POST';
    const response = await fetch(`/api/likes/post/${postId}`, {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({})  // 비워진 객체를 body로 전송
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    if (data) {
      likeButton.innerHTML = data.liked ? 'favorite' : 'favorite_border'; // 버튼 모양 업데이트
      likeButton.classList.toggle('liked', data.liked);
      likeCountSpan.textContent = `${data.likeCount}`; // 좋아요 수 업데이트
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

//좋아요 유저 목록
async function fetchUsersWhoLikedPost(postId) {
  try {
    const response = await fetch(`/api/likes/post/${postId}/list`);
    if (!response.ok) {
      console.error('좋아요 유저 목록 가져오기 실패!', response.status);
      return [];
    }
    return await response.json();
  } catch (error) {
    console.error('좋아요 유저 목록 가져오는 중 에러 발생', error);
    return [];
  }
}

//유저 목록 모달창 띄우기
function showLikeUserModalPost(users) {
  const modal = document.getElementById('like-user-modal');
  const userList = modal.querySelector('.user-list');
  userList.innerHTML = ''; //기존 목록 초기화

  users.forEach(user => {
    const userDiv = document.createElement('div');
    userDiv.className = 'user';

    const linkBox = document.createElement('div');
    linkBox.className = 'linkBox';

    const profileImg = document.createElement('img');
    profileImg.src = user.profileImageurl || '/image/profile/profile.png';
    profileImg.className = 'profile-img';

    // nicknameSpan과 introSpan을 묶어줄 컨테이너 div 생성
    const details = document.createElement('div');
    details.className = 'details';

    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = user.nickname;

    const introSpan = document.createElement('span');
    introSpan.className = 'intro-text';
    introSpan.textContent = user.intro;

    // 자기소개 없으면 "자기 소개 없음" 글씨 회색으로 변경
    if (introSpan.textContent === null || introSpan.textContent.trim() === "") {
      introSpan.textContent = "자기 소개 없음";
      introSpan.style.color = '#A9A9A9';
    }

    details.appendChild(nicknameSpan);
    details.appendChild(introSpan);

    linkBox.appendChild(profileImg);
    linkBox.appendChild(details);

    // 클릭 시 유저 페이지로 이동
    linkBox.addEventListener('click', () => {
      window.location.href = `/userpages/${user.nickname}`;
    });

    userDiv.appendChild(linkBox);

    if (user.myUserId !== user.userId) {

      console.log("내 아이디가 아님");
      const followButton = document.createElement('button');
      followButton.className = 'follow-button';
      followButton.textContent = '팔로우';

      userDiv.appendChild(followButton);
    }

    userList.appendChild(userDiv);
  });

  modal.style.display = 'block';
}

//모달창 닫음
function closeModalLike() {
  const modal = document.getElementById('like-user-modal');
  modal.style.display = 'none';
}