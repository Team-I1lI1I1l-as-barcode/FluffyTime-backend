async function postComment(postId) {
  const content = document.getElementById('comment-content').value;
  const username = document.getElementById('comment-username').value;
  const response = await fetch('/api/comments/reg', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({content, userId, postId}),
  });
  const statusMessage = document.getElementById('status-message');
  if (response.ok) {
    statusMessage.textContent = '댓글 등록 성공!';
    statusMessage.className = 'status-message';
    document.getElementById('comment-content').value = '';
  } else {
    statusMessage.textContent = '댓글 등록 실패!';
    statusMessage.className = 'error-message';
  }
}