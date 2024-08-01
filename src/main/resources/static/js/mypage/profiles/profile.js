// 현재 URL 경로에서 파라미터 추출하기
const pathSegments = window.location.pathname.split('/');
// URL 파라미터의 맨 마지막에 존재하는 유저아이디 추출하기
const userId = pathSegments[pathSegments.length - 1];

document.getElementById("testBtn").addEventListener('click', event => {
  fetch(`/api/mypage/profiles/info?userId=${encodeURIComponent(userId)}`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.text())
  .then(data => {
    if (data.error) {
      alert("에러메시지 : " + data.error)
    } else {
      alert(data);
    }
  })
  .catch(error => {
    alert("네트워크 에러 또는 기타 오류 발생:" + error);
  });
});