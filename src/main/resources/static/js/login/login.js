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

  if (!emailPattern.test(email)) {
    emailErrorElement.innerText = "올바른 이메일 형식이 아닙니다.";
    emailErrorElement.classList.remove('hidden');
    errorCount++;
  } else {
    emailErrorElement.innerText = "";
    emailErrorElement.classList.add('hidden');
  }

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
    formElement.reportValidity();
    return;
  }

  const jsonData = {
    email: email,
    password: password
  };

  const currentUrl = window.location.href;

  const url = new URL(currentUrl);

  const queryParams = new URLSearchParams(url.search);

  const redirectURL = queryParams.get('redirectURL');

  let loginApiUri;

  if (redirectURL) {
    loginApiUri = `/api/users/login?redirectURL=${redirectURL}`;
  } else {
    loginApiUri = '/api/users/login';
  }

  try {
    const response = await fetch(
        loginApiUri, {
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
    window.location.href = response.headers.get("Location"); // 원하는 URL로 변경;

  } catch (error) {
    console.error(error);
  }
}