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

  // 현재 화면 URL을 가져옵니다.
  const currentUrl = window.location.href;

  // URL 객체를 생성합니다.
  const url = new URL(currentUrl);

  // URLSearchParams 객체를 사용하여 쿼리 파라미터를 추출합니다.
  const queryParams = new URLSearchParams(url.search);

  // 'redirectURL' 쿼리 파라미터 추출
  const redirectURL = queryParams.get('redirectURL');

  let loginApiUri;

  // target API URI
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