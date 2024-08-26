let postId;

//어느 게시글의 댓글인지
document.addEventListener('DOMContentLoaded', async () => {
  // 현재 URL의 경로를 가져옵니다.
  const path = window.location.pathname;
  // 경로를 '/'로 분리하여 배열로 만듭니다.
  const pathSegments = path.split('/');
  // 배열의 마지막 요소가 postId입니다.
  postId = pathSegments[pathSegments.length - 1];
  await fetchComments(postId); // 페이지 로드 시 postId 2번의 댓글 목록을 가져옴
});

//댓글 조회
async function fetchComments() {
  const response = await fetch(`/api/comments/post/${postId}`);
  if (!response.ok) {
    console.error('댓글 목록 가져오기 실패!', response.status);
    return [];
  }
  const comments = await response.json();
  const commentList = document.getElementById('comment-list');
  commentList.innerHTML = ''; // 기존 댓글 목록 초기화
  for (const comment of comments) {

    // 댓글 내용
    const commentDiv = document.createElement('div');
    commentDiv.className = 'comment';
    commentDiv.dataset.id = comment.commentId;

    // 프로필 이미지
    const profileImg = document.createElement('img');
    profileImg.src = comment.profileImageurl || '/image/profile/profile.png'; // 프로필 이미지 가져오기
    profileImg.className = 'profile-img';

    //닉네임
    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = comment.nickname;

    //댓글 내용
    const contentSpan = document.createElement('span');
    contentSpan.className = 'text';
    contentSpan.textContent = comment.content;

    // 좋아요 버튼 추가
    const likeButton = document.createElement('span');
    likeButton.className = 'like-button material-icons';
    likeButton.innerHTML = comment.liked ? 'favorite' : 'favorite_border'; // 좋아요 상태에 따라 버튼 모양 설정

    // 좋아요 상태에 따른 클래스 적용
    if (comment.liked) {
      likeButton.classList.add('liked');
    } else {
      likeButton.classList.remove('liked');
    }

    // 좋아요 개수
    const likeCountSpan = document.createElement('span');
    likeCountSpan.className = 'like-count';
    likeCountSpan.textContent = `${comment.likeCount}`;

    // 좋아요 버튼 클릭 이벤트
    likeButton.onclick = () => {
      toggleLikeComment(comment.commentId, likeButton, likeCountSpan);
    };

    //좋아요 목록 모달 보여줌 (개수 클릭 시)
    likeCountSpan.onclick = async = async () => {
      const users = await fetchUsersWhoLikedComment(comment.commentId);
      showLikeUserModalComment(users);
    }

    // 좋아요 버튼/개수 묶음
    const likedDiv = document.createElement('div');
    likedDiv.className = 'liked-box';
    likedDiv.appendChild(likeButton);
    likedDiv.appendChild(likeCountSpan);

    // nicknameSpan과 contentSpan을 한 번 더 묶음
    const nicknameContentDiv = document.createElement('div');
    nicknameContentDiv.className = 'nickname-content';
    nicknameContentDiv.appendChild(nicknameSpan);
    nicknameContentDiv.appendChild(contentSpan);

    // 프로필 이미지와 닉네임/댓글 내용을 하나의 div로 묶음
    const profileContentDiv = document.createElement('div');
    profileContentDiv.className = 'profile-content';
    profileContentDiv.appendChild(profileImg);
    profileContentDiv.appendChild(nicknameContentDiv);
    profileContentDiv.appendChild(likedDiv);

    // 버튼들 묶음
    const editDeleteButtonsDiv = document.createElement('div');
    editDeleteButtonsDiv.className = 'edit-delete-buttons';

    // 댓글 수정 및 삭제 버튼
    if (comment.author) {
      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEdit(comment.commentId, comment.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteComment(comment.commentId);

      editDeleteButtonsDiv.appendChild(editButton);
      editDeleteButtonsDiv.appendChild(deleteButton);
    }

    // 답글 버튼
    const replyButton = document.createElement('button');
    replyButton.textContent = '답글';
    replyButton.onclick = () => toggleReplyInput(comment.commentId);

    editDeleteButtonsDiv.appendChild(replyButton);

    // 댓글 내용을 commentDiv에 추가
    commentDiv.appendChild(profileContentDiv);
    commentDiv.appendChild(editDeleteButtonsDiv);

    // 답글 목록 추가
    const repliesDiv = document.createElement('div');
    repliesDiv.className = 'replies';
    fetchReplies(comment.commentId, repliesDiv);

    commentDiv.appendChild(repliesDiv);
    commentList.appendChild(commentDiv);
  }
}

// 답글 입력 칸 토글
function toggleReplyInput(commentId) {
  let replyDiv = document.querySelector(
      `.reply-section[data-id='${commentId}']`);
  if (replyDiv) {
    replyDiv.remove();
  } else {
    replyDiv = document.createElement('div');
    replyDiv.className = 'reply-section';
    replyDiv.dataset.id = commentId;

    const replyTextarea = document.createElement('textarea');
    replyTextarea.id = `reply-textarea-${commentId}`;
    replyTextarea.placeholder = '답글 내용을 입력하세요...';

    const replyButton = document.createElement('button');
    replyButton.textContent = '답글 달기';
    replyButton.onclick = () => postReply(commentId, replyTextarea.value);

    replyDiv.appendChild(replyTextarea);
    replyDiv.appendChild(replyButton);

    const commentDiv = document.querySelector(
        `.comment[data-id='${commentId}']`);
    commentDiv.appendChild(replyDiv);

    // 스크롤을 하단으로 이동
    commentDiv.scrollIntoView({behavior: 'smooth', block: 'end'});
  }
}

//답글 조회
async function fetchReplies(commentId, replyDiv) {
  const response = await fetch(`/api/replies/comment/${commentId}`);
  if (!response.ok) {
    console.error('답글 목록 가져오기 실패!', response.status);
    return;
  }
  const replies = await response.json();
  for (const reply of replies) {
    const replyElement = document.createElement('div');
    replyElement.className = 'reply';
    replyElement.dataset.id = reply.replyId;

    // 프로필 이미지
    const profileImg = document.createElement('img');
    profileImg.src = reply.profileImageurl || '/image/profile/profile.png'; // 프로필 이미지 가져오기
    profileImg.className = 'profile-img';

    // 닉네임 및 답글 내용
    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = reply.nickname;

    const contentSpan = document.createElement('span');
    contentSpan.className = 'text';
    contentSpan.textContent = reply.content;

    // 좋아요 버튼 추가
    const likeButton = document.createElement('span');
    likeButton.className = 'like-button material-icons';
    likeButton.innerHTML = reply.liked ? 'favorite' : 'favorite_border'; // 좋아요 상태에 따라 버튼 모양 설정

    // 좋아요 상태에 따른 클래스 적용
    if (reply.liked) {
      likeButton.classList.add('liked');
    } else {
      likeButton.classList.remove('liked');
    }

    // 좋아요 개수
    const likeCountSpan = document.createElement('span');
    likeCountSpan.className = 'like-count';
    likeCountSpan.textContent = `${reply.likeCount}`;

    // 좋아요 버튼 클릭 이벤트
    likeButton.onclick = () => {
      toggleLikeReply(reply.replyId, likeButton, likeCountSpan);
    };

    //좋아요 목록 모달 보여줌 (개수 클릭 시)
    likeCountSpan.onclick = async = async () => {
      const users = await fetchUsersWhoLikedReply(reply.replyId);
      showLikeUserModalReply(users);
    }

    // 좋아요 버튼/개수 묶음
    const likedDiv = document.createElement('div');
    likedDiv.className = 'liked-box';
    likedDiv.appendChild(likeButton);
    likedDiv.appendChild(likeCountSpan);

    // nicknameSpan과 contentSpan을 한 번 더 묶음
    const nicknameContentDiv = document.createElement('div');
    nicknameContentDiv.className = 'nickname-content';
    nicknameContentDiv.appendChild(nicknameSpan);
    nicknameContentDiv.appendChild(contentSpan);

    // 프로필 이미지와 닉네임/답글 내용을 하나의 div로 묶음
    const profileContentDiv = document.createElement('div');
    profileContentDiv.className = 'profile-content';
    profileContentDiv.appendChild(profileImg);
    profileContentDiv.appendChild(nicknameContentDiv);
    profileContentDiv.appendChild(likedDiv);

    const editDeleteButtonsDiv = document.createElement('div');
    editDeleteButtonsDiv.className = 'edit-delete-buttons-reply';

    if (reply.author) {

      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEditReply(reply.replyId, reply.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteReply(reply.replyId, commentId);

      editDeleteButtonsDiv.appendChild(editButton);
      editDeleteButtonsDiv.appendChild(deleteButton);
    }

    replyElement.appendChild(profileContentDiv);
    replyDiv.appendChild(replyElement);
    replyElement.appendChild(editDeleteButtonsDiv);
  }
}

// 답글 등록
async function postReply(commentId, content) {
  try {
    const response = await fetch('/api/replies/reg', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content, commentId}),
    });

    if (response.ok) {
      console.log('답글 등록 성공!');
      await fetchComments(postId); // 댓글 목록 갱신
    } else {
      console.error('답글 등록 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 등록 중 예외 발생!', error);
  }
}

// 답글 수정칸 보여주기
function showEditReply(replyId, currentContent) {
  const replyDiv = document.querySelector(`.reply[data-id='${replyId}']`);
  replyDiv.innerHTML = ''; // 기존 내용 지우기

  const editTextarea = document.createElement('textarea');
  editTextarea.value = currentContent;

  const saveButton = document.createElement('button');
  saveButton.className = 'comment-edit-button';
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateReply(replyId, editTextarea.value);

  const cancelButton = document.createElement('button');
  cancelButton.className = 'comment-cancel-button';
  cancelButton.textContent = '취소';
  cancelButton.onclick = () => fetchComments(postId); // 페이지 새로고침으로 수정 취소

  const saveCancleDiv = document.createElement('div');
  saveCancleDiv.className = 'save-cancle-button-box';
  saveCancleDiv.appendChild(saveButton);
  saveCancleDiv.appendChild(cancelButton);

  replyDiv.appendChild(editTextarea);
  replyDiv.appendChild(saveCancleDiv);
}

// 답글 수정
async function updateReply(replyId, newContent) {
  try {
    const response = await fetch(`/api/replies/update/${replyId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content: newContent}),
    });

    if (response.ok) {
      console.log('답글 수정 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('답글 수정 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 수정 중 예외 발생!', error);
  }
}

// 답글 삭제
async function deleteReply(replyId, commentId) {
  try {
    const response = await fetch(`/api/replies/delete/${replyId}`, {
      method: 'DELETE',
    });

    if (response.ok) {
      console.log('답글 삭제 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('답글 삭제 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 삭제 중 예외 발생!', error);
  }
}

//댓글 등록
async function postComment() {
  const content = document.getElementById('comment-content').value;
  const statusMessage = document.getElementById('status-message');

  try {
    const response = await fetch('/api/comments/reg', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content, postId}),
    });

    const data = await response.json();

    if (response.ok) {
      document.getElementById('comment-content').value = '';
      console.log('댓글 등록 성공!');
      console.log('서버 응답: ', data);
      await fetchComments(postId);
    } else {

      console.error('댓글 등록 실패! 상태 코드: ', response.status, '서버 응답: ', data);
    }
  } catch (error) {
    console.error('댓글 등록 중 예외 발생!', error);
  }
}

//수정칸 보여주기
function showEdit(commentId, currentContent) {
  const commentDiv = document.querySelector(`.comment[data-id='${commentId}']`);
  commentDiv.innerHTML = ''; // 기존 내용 지우기

  const editTextarea = document.createElement('textarea');
  editTextarea.value = currentContent;

  const saveButton = document.createElement('button');
  saveButton.className = 'comment-edit-button';
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateComment(commentId, editTextarea.value);

  const cancelButton = document.createElement('button');
  cancelButton.className = 'comment-cancel-button';
  cancelButton.textContent = '취소';
  cancelButton.onclick = () => fetchComments(postId); // 페이지 새로고침으로 수정 취소

  const saveCancleDiv = document.createElement('div');
  saveCancleDiv.className = 'save-cancle-button-box';
  saveCancleDiv.appendChild(saveButton);
  saveCancleDiv.appendChild(cancelButton);

  commentDiv.appendChild(editTextarea);
  commentDiv.appendChild(saveCancleDiv);
}

//댓글 수정
async function updateComment(commentId, newContent) {
  try {
    const response = await fetch(`/api/comments/update/${commentId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content: newContent}),
    });

    if (response.ok) {
      console.log('댓글 수정 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('댓글 수정 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('댓글 수정 중 예외 발생!', error);
  }
}

//댓글 삭제
async function deleteComment(commentId) {
  console.log('Deleting comment with Id: ', commentId);
  try {
    const response = await fetch(`/api/comments/delete/${commentId}`, {
      method: 'DELETE',
    });

    if (response.ok) {
      console.log('댓글 삭제 성공!');
      await fetchComments(postId); // 댓글 목록 갱신
    } else {
      console.error('댓글 삭제 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('댓글 삭제 중 예외 발생!', error);
  }
}