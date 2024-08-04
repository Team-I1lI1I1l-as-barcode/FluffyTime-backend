function postComment(postId) {
  const content = document.getElementById('comment-content').value;
  const userId = document.getElementById('user-id').value;
  const statusMessage = document.getElementById('status-message');

  fetch('/api/comments/reg', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({content, userId, postId}),
  })
  .then(response => {
    return response.json().then(data => {
      if (response.ok) {
        statusMessage.textContent = '댓글 등록 성공!';
        statusMessage.className = 'status-message';
        document.getElementById('comment-content').value = '';
        console.log('댓글 등록 성공!')
        console.log('서버 응답: ', data);
      } else {
        statusMessage.textContent = '댓글 등록 실패!';
        statusMessage.className = 'error-message';
        console.error('댓글 등록 실패! 상태 코드: ', response.status, '서버 응답: ', data);
      }
    })
  })
  .catch(error => {
    statusMessage.textContent = '댓글 등록 실패!';
    statusMessage.className = 'error-message';
    console.error('댓글 등록 중 예외 발생!', error);
  })
}