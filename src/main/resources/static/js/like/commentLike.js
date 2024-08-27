//좋아요 등록/취소
async function toggleLikeComment(commentId, likeButton, likeCountSpan) {
  try {
    const isLiked = likeButton.classList.contains('liked');
    const method = isLiked ? 'DELETE' : 'POST';
    const response = await fetch(`/api/likes/comment/${commentId}`, {
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
async function fetchUsersWhoLikedComment(commentId) {
  try {
    const response = await fetch(`/api/likes/comment/${commentId}/list`);
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
function showLikeUserModalComment(users) {
  const modal = document.getElementById('like-user-modal');
  const userList = modal.querySelector('.user-list');
  userList.innerHTML = ''; //기존 목록 초기화

  users.forEach(user => {
    const userDiv = document.createElement('div');
    userDiv.className = 'user';

    const profileImg = document.createElement('img');
    profileImg.src = user.profileImageurl || '/image/profile/profile.png';
    profileImg.className = 'profile-img';

    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = user.nickname;

    const introSpan = document.createElement('span');
    introSpan.className = 'intro-text';
    introSpan.textContent = user.intro;

    // nicknameSpan과 introSpan을 묶어줄 컨테이너 div 생성
    const nicknameIntroDiv = document.createElement('div');
    nicknameIntroDiv.className = 'nickname-intro';

    nicknameIntroDiv.appendChild(nicknameSpan);
    nicknameIntroDiv.appendChild(introSpan);

    const followButton = document.createElement('button');
    followButton.className = 'follow-button';
    followButton.textContent = '팔로우';

    userDiv.appendChild(profileImg);
    userDiv.appendChild(nicknameIntroDiv);
    userDiv.appendChild(followButton);

    userList.appendChild(userDiv);
  });

  modal.style.display = 'block';
}

//모달창 닫음
function closeModal() {
  const modal = document.getElementById('like-user-modal');
  modal.style.display = 'none';
}