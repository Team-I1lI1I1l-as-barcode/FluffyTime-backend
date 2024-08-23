// 유저 차단 관련된 api
function handleApiRequest(url, method, successMessage) {
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.log(`${successMessage} 진행 중 에러 발생 >> ${errorData.message}`);
        alert('Error: ' + errorData.message);
        window.location.reload();
      });
    }
    return response.json();
  })
  .then(data => {
    if (data.userBlockResult) {
      console.log(successMessage + " 완료되었습니다.");
    }
    window.location.reload();
  })
  .catch(error => {
    console.log("서버 오류 발생: " + error);
  });
}

// 유저 차단
blockFollow.addEventListener('click', (event) => {
  event.preventDefault();
  const nickname = window.location.pathname.split('/').pop();
  handleApiRequest(`/api/users/block?nickname=${encodeURIComponent(nickname)}`,
      'POST', '유저 차단');
});

// 유저 차단 해제
blockFollowCancel.addEventListener('click', (event) => {
  event.preventDefault();
  const nickname = window.location.pathname.split('/').pop();
  handleApiRequest(
      `/api/users/unblock?nickname=${encodeURIComponent(nickname)}`, 'DELETE',
      '유저 차단 해제');
});
