//좋아요 등록/취소
async function toggleLike(commentId, likeButton, likeCountSpan) {
  try {
    const response = await fetch(`/api/likes/comment/${commentId}`, {
      method: 'POST',
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
      likeButton.innerHTML = data.liked ? '♥' : '♡'; // 버튼 모양 업데이트
      likeCountSpan.textContent = `${data.likeCount} Likes`; // 좋아요 수 업데이트
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

//좋아요 유저 목록
async function fetchUsersWhoLiked(commentId) {
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
function showLikeUserModal(users) {
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

    userDiv.appendChild(profileImg);
    userDiv.appendChild(nicknameSpan);
    userList.appendChild(userDiv);
  });

  modal.style.display = 'block';
}

//모달창 닫음
function closeModal() {
  const modal = document.getElementById('like-user-modal');
  modal.style.display = 'none';
}