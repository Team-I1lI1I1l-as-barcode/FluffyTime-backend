const loginBtnElement = document.getElementById('loginBtn');
const emailErrorElement = document.querySelector('.email-error');
const passwordErrorElement = document.querySelector('.password-error');

loginBtnElement.addEventListener('click', loginProcess);

const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;

async function loginProcess(event) {
  event.preventDefault();
  const formElement = document.getElementById('loginForm');

  const email = formElement.email.value;
  const password = formElement.password.value;

  let errorCount = 0;

  // 이메일 형식 검사
  if (!emailPattern.test(email)) {
    emailErrorElement.innerText = "올바른 이메일 형식이 아닙니다.";
    emailErrorElement.classList.remove('hidden');
    errorCount++;
  } else {
    emailErrorElement.innerText = "";
    emailErrorElement.classList.add('hidden');
  }

  // passsword 빈값 검사
  if (!password) {
    passwordErrorElement.innerText = "비밀번호를 입력해주세요.";
    passwordErrorElement.classList.remove('hidden');
    errorCount++;
  } else {
    passwordErrorElement.innerText = "";
    passwordErrorElement.classList.add('hidden');
  }

  if(errorCount>0) {
    return;
  }

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
      emailErrorElement.innerText = "아이디, 비밀번호를 확인해주세요."
      emailErrorElement.classList.remove('hidden')
      return;
    }
    console.log("로그인 성공")
    localStorage.setItem('lastRefreshTime', new Date().getTime());
    window.location.href = '/';

  } catch (error) {
    console.error(error);
  }
}