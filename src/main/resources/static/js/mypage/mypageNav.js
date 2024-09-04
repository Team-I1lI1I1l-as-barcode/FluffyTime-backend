// 마이페이지
document.getElementById("mypageBtn").addEventListener('click', event => {
  //window.location.href = "/mypage/test";
  fetch("/api/mypage/info", {
    method: "GET", // GET 요청
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // 에러 메시지 포함하여 alert 호출
        console.log("fetchMyPage 응답 에러 발생 >> " + errorData.message);
        alert('Error: ' + errorData.message);
        window.location.href = "/";
      });
    }
    return response.json();
  })  // 서버에서 보낸 응답을 JSON 형식으로 변환
  .then(data => {
    window.location.href = `/mypage/${data.nickname}`;
  })
  .catch(error => {
    console.log("서버 오류 발생:" + error);
  });
});