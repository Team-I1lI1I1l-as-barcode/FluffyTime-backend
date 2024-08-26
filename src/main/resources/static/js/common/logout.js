//로그아웃
const btn = document.getElementById('logout');
btn.addEventListener('click', async () => {
  try {
    await fetch(
        '/api/users/logout', {
          method: 'POST'
        });
    window.location.href = "/login"
  } catch (error) {
    console.error(error);
  }
})