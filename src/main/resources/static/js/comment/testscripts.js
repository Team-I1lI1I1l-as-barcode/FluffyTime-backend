//댓글 조회
async function fetchComments(postId) {
  const response = await fetch(`/api/comments/post/${postId}`);
  if (!response.ok) {
    console.error('댓글 목록 가져오기 실패!', response.status);
    return;
  }
  const comments = await response.json();
  const commentList = document.getElementById('comment-list');
  commentList.innerHTML = ''; // 기존 댓글 목록 초기화
  comments.forEach(comment => {
    const commentDiv = document.createElement('div');
    commentDiv.className = 'comment';
    commentDiv.dataset.id = comment.id;

    const contentDiv = document.createElement('div');
    contentDiv.className = 'comment-content';
    commentDiv.textContent = `${comment.nickname}: ${comment.content}`;

    const editButton = document.createElement('button');
    editButton.textContent = '수정';
    editButton.onclick = () => showEdit(comment.id, comment.content);

    const deleteButton = document.createElement('button');
    deleteButton.textContent = '삭제';
    deleteButton.onclick = () => deleteComment(comment.id, postId);

    commentDiv.appendChild(contentDiv);
    commentDiv.appendChild(editButton);
    commentDiv.appendChild(deleteButton);

    commentList.appendChild(commentDiv);
  });
}

//댓글 등록
async function postComment(postId) {
  const content = document.getElementById('comment-content').value;
  const userId = document.getElementById('user-id').value;
  const statusMessage = document.getElementById('status-message');

  try {
    const response = await fetch('/api/comments/reg', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content, userId, postId}),
    });

    const data = await response.json();

    if (response.ok) {
      statusMessage.textContent = '댓글 등록 성공!';
      statusMessage.className = 'status-message';
      document.getElementById('comment-content').value = '';
      console.log('댓글 등록 성공!');
      console.log('서버 응답: ', data);
    } else {
      statusMessage.textContent = '댓글 등록 실패!';
      statusMessage.className = 'error-message';
      console.error('댓글 등록 실패! 상태 코드: ', response.status, '서버 응답: ', data);
    }
  } catch (error) {
    statusMessage.textContent = '댓글 등록 실패!';
    statusMessage.className = 'error-message';
    console.error('댓글 등록 중 예외 발생!', error);
  }
}

//어느 게시글의 댓글인지
document.addEventListener('DOMContentLoaded', async () => {
  await fetchComments(2); // 페이지 로드 시 postId 2번의 댓글 목록을 가져옴
});

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
async function deleteComment(commentId, postId) {
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