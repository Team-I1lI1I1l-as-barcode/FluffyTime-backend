// refresh token으로 access token 재발급 요청 보내기
async function refreshAccessToken() {
  console.log("Refreshing Access Token");
  const response = await fetch('/api/auth/refreshToken', {
    method:'POST'
  })

  if(!response.ok) {
    console.log("Failed to refresh Access Token")
    // window.location.href="/login"
    return;
  }

  console.log("Access Token refreshed successfully")
  localStorage.setItem('lastRefreshTime', new Date().getTime());
}

// access token 만료 시간 이전 주기적 갱신 시도
async function initTokenRefresh() {
  console.log("initTokenRefresh");
  const lastRefreshTime = localStorage.getItem("lastRefreshTime");
  const currentTime = new Date().getTime();
  const timeElapsed = currentTime - lastRefreshTime;

  // 마지막 갱신 후 50분이 지났다면 즉시 갱신
  if(timeElapsed >= 50 * 60 * 1000) {
    console.log("즉시갱신")
     await refreshAccessToken();
  }

  // 주기적으로 AccessToken 갱신
  setInterval(() => {
    const lastRefreshTime = localStorage.getItem("lastRefreshTime");
    const currentTime = new Date().getTime();
    const timeElapsed = currentTime - lastRefreshTime;

    // 50분이 지난 경우에만 갱신
    if(timeElapsed >= 50 * 60 * 1000) {
       refreshAccessToken();
    }
  }, 60 * 1000); // 매 1분마다 체크
}

// 페이지 로드 시 초기화
console.log('Script loaded');
window.addEventListener("load", initTokenRefresh);