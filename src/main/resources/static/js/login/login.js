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

    if (!response.ok) {
      alert("로그인 에러")
      return;
    }
    console.log("로그인 성공")

    window.location.href = '/';

  } catch (error) {
    console.error(error);
  }
}