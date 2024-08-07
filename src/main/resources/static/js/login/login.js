const loginBtnElement = document.getElementById('loginBtn');

loginBtnElement.addEventListener('click', loginProcess);

async function loginProcess(event) {
  event.preventDefault();
  const formElement = document.getElementById('loginForm');

  const email = formElement.email.value;
  const password = formElement.password.value;

  if (!formElement.checkValidity()) {
    formElement.reportValidity(); // 브라우저의 기본 검증 메시지 표시
    return;
  }

  // 폼 데이터를 JSON으로 변환
  const jsonData = {
    email: email,
    password: password
  };

  try {
    const response = await fetch(
        '/api/users/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(jsonData)
        });
    const data = await response.json();

    if (!response.ok) {
      alert("로그인 에러")
      throw new Error(data.message || "error");
    }
    window.location.href = '/'; // 원하는 URL로 변경
  } catch (error) {
    console.error(error);
  }
}