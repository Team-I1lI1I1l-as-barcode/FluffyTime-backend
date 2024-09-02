// refresh token으로 access token 재발급 요청 보내기
let intervalId;

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

  // // 마지막 갱신 후 50분이 지났다면 즉시 갱신
  // if(timeElapsed >= 50 * 60 * 1000) {
    console.log("즉시갱신")
     await refreshAccessToken();
  // }

  // 주기적으로 AccessToken 갱신
  intervalId = setInterval(() => {
    const lastRefreshTime = localStorage.getItem("lastRefreshTime");
    const currentTime = new Date().getTime();
    const timeElapsed = currentTime - lastRefreshTime;

    // 50분이 지난 경우에만 갱신
    if(timeElapsed >= 50 * 60 * 1000) {
       refreshAccessToken();
    }
  }, 60 * 1000); // 매 1분마다 체크
}

function hiddenAdminPageBtn() {
  // localStorage에서 isAdmin 값 가져오기

  let isAdmin = localStorage.getItem('isAdmin');

  // adminPageBtn 요소 선택
  let adminPageBtnDiv = document.querySelector('.adminPageBtn');

  if(adminPageBtnDiv == null || isAdmin == null) {return;}

  // isAdmin 값에 따라 요소 표시/숨김 처리
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

// 페이지 로드 시 초기화
console.log('Script loaded');
window.addEventListener("load", onPageLoad);