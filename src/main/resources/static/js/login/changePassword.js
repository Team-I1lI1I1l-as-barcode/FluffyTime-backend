const changeBtn = document.getElementById("changeBtn");

changeBtn.addEventListener("click", changePassword);

const passwordPattern = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,20}$/;

async function changePassword(event) {
  event.preventDefault();

  // 현재 화면 URL을 가져옵니다.
  const currentUrl = window.location.href;

  // URL 객체를 생성합니다.
  const url = new URL(currentUrl);

  // URLSearchParams 객체를 사용하여 쿼리 파라미터를 추출합니다.
  const queryParams = new URLSearchParams(url.search);

  // 'redirectURL' 쿼리 파라미터 추출
  const email = queryParams.get('email');

  const formElement = document.getElementById('changePasswordForm');
  const passwordErrorElement = document.querySelector('.password-error');
  const resultMsgElement = document.querySelector('.resultMsg');

  const password = formElement.password.value;
  const checkPassword = formElement.checkPassword.value;

  // 비밀번호 유효성 검사
  if (!passwordPattern.test(password)) {
    passwordErrorElement.innerText = "비밀번호는 8자 이상 20자 이하, 숫자, 문자, 특수문자를 포함해야 합니다.";
    passwordErrorElement.classList.remove('hidden');
    return
  } else {
    if (password !== checkPassword) {
      passwordErrorElement.innerText = "비밀번호가 일치하지 않습니다.";
      passwordErrorElement.classList.remove('hidden');
    return
    } else {
      passwordErrorElement.innerText = "";
      passwordErrorElement.classList.add('hidden');
    }
  }

  const jsonData = {
    email:email,
    password:password
  }

  const response = await fetch('/api/users/change/password', {
    method:'POST',
    headers: {
      'Content-Type' : 'application/json'
    },
    body: JSON.stringify(jsonData)
  });

  if (!response.ok) {
    resultMsgElement.innerText = "비밀번호 번경에 실패하였습니다."
        + "\n 변경 메일을 다시 전송받은 후 비밀번호 변경을 진행해주세요."
    return;
  }

  resultMsgElement.innerText = "비밀번호 변경에 성공하였습니다."
      + "\n재로그인 후 서비스를 이용해주세요."
}