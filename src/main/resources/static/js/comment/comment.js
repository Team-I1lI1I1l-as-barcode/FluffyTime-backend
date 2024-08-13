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

// 프로필 이미지를 가져오는 함수
async function fetchProfileImage(userId) {
  try {
    const response = await fetch('/api/mypage/profiles/info');
    if (!response.ok) {
      console.error('프로필 이미지 가져오기 실패!', response.status);
      return '/image/profile/profile.png'; // 기본 이미지 URL
    }
    const profileData = await response.json();
    console.log(profileData);
    return profileData.fileUrl || '/image/profile/profile.png'; // 프로필 이미지가 없으면 기본 이미지 사용
  } catch (error) {
    console.error('프로필 이미지 가져오는 중 예외 발생!', error);
    return '/image/profile/profile.png'; // 예외 발생 시 기본 이미지 사용
  }
}

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

    const contentDiv = document.createElement('div');
    contentDiv.className = 'comment-content';

    // 프로필 이미지
    const profileImgUrl = await fetchProfileImage(comment.userId); // 프로필 이미지 가져오기

    const profileImg = document.createElement('img');
    profileImg.src = profileImgUrl;
    profileImg.className = 'profile-img';

    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = comment.nickname;

    const contentSpan = document.createElement('span');
    contentSpan.className = 'text';
    contentSpan.textContent = comment.content;

    contentDiv.appendChild(nicknameSpan);
    contentDiv.appendChild(contentSpan);

    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'button-container';

    const buttonsDiv = document.createElement('div');
    buttonsDiv.className = 'buttons';

    // 댓글 수정 및 삭제 버튼
    if (comment.author) {
      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEdit(comment.commentId, comment.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteComment(comment.commentId);

      const editDeleteDiv = document.createElement('div');
      editDeleteDiv.className = 'edit-delete-buttons';
      editDeleteDiv.appendChild(editButton);
      editDeleteDiv.appendChild(deleteButton);

      buttonContainer.appendChild(editDeleteDiv);
      commentDiv.appendChild(editDeleteDiv);
    }

    // 답글 버튼
    const replyButton = document.createElement('button');
    replyButton.textContent = '답글';
    replyButton.onclick = () => toggleReplyInput(comment.commentId);
    buttonsDiv.appendChild(replyButton);
    commentDiv.appendChild(replyButton);

    commentDiv.appendChild(profileImg);
    commentDiv.appendChild(contentDiv);
    commentDiv.appendChild(buttonsDiv);

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
    replyElement.textContent = `${reply.nickname} ${reply.content}`;

    // 프로필 이미지
    const profileImgUrl = await fetchProfileImage(reply.userId); // 프로필 이미지 가져오기

    const profileImg = document.createElement('img');
    profileImg.src = profileImgUrl;
    profileImg.className = 'profile-img';

    if (reply.author) {
      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEditReply(reply.replyId, reply.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteReply(reply.replyId, commentId);

      replyElement.appendChild(editButton);
      replyElement.appendChild(deleteButton);
    }

    replyDiv.appendChild(profileImg);
    replyDiv.appendChild(replyElement);
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
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateReply(replyId, editTextarea.value);

  replyDiv.appendChild(editTextarea);
  replyDiv.appendChild(saveButton);
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
      await fetchComments(2); // 댓글 목록 갱신 (postId를 적절히 대체)
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
      await fetchComments(2); // 댓글 목록 갱신 (postId를 적절히 대체)
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
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateComment(commentId, editTextarea.value);

  commentDiv.appendChild(editTextarea);
  commentDiv.appendChild(saveButton);
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
      await fetchComments(2); // 댓글 목록 갱신 (postId를 적절히 대체)
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