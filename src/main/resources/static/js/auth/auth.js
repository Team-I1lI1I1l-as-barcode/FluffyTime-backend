const tokenManager = {
  accessToken: getCookie('accessToken'),
  refreshToken: getCookie('refreshToken'),

  async validateAndRefreshToken() {
    if (!this.accessToken && this.refreshToken) {
      await handleTokenRefresh();
    }
  }
};


// API 요청을 보낼 때 사용하는 함수
async function fetchWithAuth(url, options = {}) {
  if(checkTokens()) {
    return await fetch(url, options);
  }
  // accessToken이 만료된 경우, 재발급 요청을 보내는 함수 호출
  await handleTokenRefresh();
    // 이후 다시 원래 요청을 보냄
    if(getCookie("accessToken") !== null) {
    return fetch(url, options);
  }
}

// accessToken 재발급 함수
async function handleTokenRefresh() {
  try {
    const response = await fetch('/api/users/reissue', {
      method: 'POST',
      credentials: 'include', // 쿠키를 포함하여 요청
    });

    if(!response.ok) {
      throw new Error('Failed RefreshToken Validation')
    }
  } catch (error) {
    console.error('Failed to refresh token:', error);
    // 실패한 경우, 사용자에게 알리고 로그인 페이지로 리다이렉트
    alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
    window.location.href = '/login';
  }
}

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

function checkTokens() {
  const accessToken = getCookie('accessToken');

  if (accessToken) {
    console.log('Access Token exists:', accessToken);
    return true;
  } else {
    console.log('Access Token does not exist.');
    return false;
  }
}