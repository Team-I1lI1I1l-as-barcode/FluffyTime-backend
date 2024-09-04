// refresh token으로 access token 재발급 요청 보내기
let intervalId;

async function refreshAccessToken() {
  console.log("Refreshing Access Token");
  const response = await fetch('/api/auth/refreshToken', {
    method:'POST'
  })

  if(!response.ok) {
    console.log("Failed to refresh Access Token")
    return;
  }

  const isAdminValue = response.headers.get('is-admin');

  if (isAdminValue !== null) {
    localStorage.setItem('isAdmin', isAdminValue);
    console.log('is-true value saved to localStorage:', isAdminValue);
  } else {
    console.log('is-true header not found in the response');
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


  console.log("즉시갱신")
  await refreshAccessToken();

  intervalId = setInterval(() => {
    const lastRefreshTime = localStorage.getItem("lastRefreshTime");
    const currentTime = new Date().getTime();
    const timeElapsed = currentTime - lastRefreshTime;

    if(timeElapsed >= 50 * 60 * 1000) {
       refreshAccessToken();
    }
  }, 60 * 1000);
}

function hiddenAdminPageBtn() {

  let isAdmin = localStorage.getItem('isAdmin');

  let adminPageBtnDiv = document.querySelector('.adminPageBtn');

  if(adminPageBtnDiv == null || isAdmin == null) {return;}

  if (isAdmin === 'true') {
    adminPageBtnDiv.classList.remove('hidden');  // 보여줌
    console.log("admin 숨기기 제거")
  } else {
    if(adminPageBtnDiv.classList.contains('hidden')) {
      console.log("admin 숨기기 이미있음")
      return
    }
    console.log("admin 숨기기 실행")
    adminPageBtnDiv.classList.add('hidden');  // 보여줌
  }
}

async function onPageLoad() {
  await initTokenRefresh();
  hiddenAdminPageBtn();
}

window.addEventListener("beforeunload", () => {
  clearInterval(intervalId);
});

console.log('Script loaded');
window.addEventListener("load", onPageLoad);