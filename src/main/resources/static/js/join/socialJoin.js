document.addEventListener('DOMContentLoaded', (event) => {
  const currentUrl = window.location.href;

  const url = new URL(currentUrl);

  const queryParams = new URLSearchParams(url.search);

  const email = queryParams.get('email');

  const emailField = document.getElementById('email');

  emailField.value = email;

  emailField.disabled = true;
});

let isNicknameChecked = false;
let checkedNickname = "";

const checkNicknameBtn = document.getElementById("checkNicknameBtn");
checkNicknameBtn.addEventListener("click", checkNickName);

const joinBtn = document.getElementById("joinBtn");
joinBtn.addEventListener("click", socialJoin);

const passwordNoticeElement = document.querySelector('.password-notice');
const nicknameNoticeElement = document.querySelector('.nickname-notice');

const passwordPattern = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,20}$/;
const usernamePattern = /^[a-zA-Z0-9_-]+$/;

async function socialJoin(event) {
  event.preventDefault()

  const formElement = document.getElementById("joinForm");

  const email = formElement.email.value;
  const password = formElement.password.value;
  const checkPassword = formElement.checkPassword.value;
  const nickname = formElement.nickname.value;

  if (!formElement.checkValidity()) {
    formElement.reportValidity();
    return;
  }

  let errorCount = 0;

  if (nickname !== checkedNickname || !isNicknameChecked) {
    nicknameNoticeElement.innerText="유저명 중복확인을 완료해주세요."
    nicknameNoticeElement.classList.add('error')
    nicknameNoticeElement.classList.remove('hidden')
    errorCount++;
  } else {
    nicknameNoticeElement.innerText=""
    nicknameNoticeElement.classList.remove('error')
    nicknameNoticeElement.classList.add('hidden')
  }

  if (!passwordPattern.test(password)) {
    passwordNoticeElement.innerText = "비밀번호는 8자 이상 20자 이하, 숫자, 문자, 특수문자를 포함해야 합니다.";
    passwordNoticeElement.classList.add('error');
    passwordNoticeElement.classList.remove('hidden');
    errorCount++;
  } else {
    if (password !== checkPassword) {
      passwordNoticeElement.innerText="비밀번호가 일치하지 않습니다."
      passwordNoticeElement.classList.add('error')
      passwordNoticeElement.classList.remove('hidden')
      errorCount++;
    } else {
      passwordNoticeElement.innerText=""
      passwordNoticeElement.classList.remove('error')
      passwordNoticeElement.classList.add('hidden')
    }
  }

  console.log(errorCount)
  if(errorCount > 0) {
    return
  }

  const jsonData = {
    email: email,
    password: password,
    nickname: nickname
  };

  try {
    const response = await fetch(
        '/api/users/social-join', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(jsonData)
        });

    const res = await response.json();

    if (!response.ok) {
      if (res.code === "GE-010") {
        alert("올바른 회원가입 데이터 형식이 아닙니다.")
        return
      } else if (res.code === "JE-003") {
        window.location.href = '/join/fail';
      } else {
        alert("[ERROR]" + res.code + " : " + res.message)
        return
      }
    }

    window.location.href = '/join/success';

  } catch (error) {
    console.error(error);
  }
}

async function checkNickName() {
  const nicknameElement = document.getElementById('nickname');
  const nickname = nicknameElement.value;

  if (!usernamePattern.test(nickname)) {
    nicknameNoticeElement.innerText = "올바른 유저명 형식이 아닙니다.";
    nicknameNoticeElement.classList.add('error')
    nicknameNoticeElement.classList.remove('hidden');
    return;
  } else {
    nicknameNoticeElement.innerText = "";
    nicknameNoticeElement.classList.add('hidden');
    nicknameNoticeElement.classList.remove('error');
  }

  if (nickname.length > 20) {
    nicknameNoticeElement.innerText = "유저명은 20자 이하만 가능합니다.";
    nicknameNoticeElement.classList.add('error')
    nicknameNoticeElement.classList.remove('hidden');
    return;
  } else {
    nicknameNoticeElement.innerText = "";
    nicknameNoticeElement.classList.add('hidden');
    nicknameNoticeElement.classList.remove('error');
  }

  try {
    const response = await fetch(
        `/api/users/check-nickname?nickname=${nickname}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const res = await response.json();

    if (!response.ok) {
      isNicknameChecked = false;
      if (res.code === "JE-002") {
        nicknameNoticeElement.innerText = "이미 사용중인 유저명입니다. 다른 유저명을 입력해주세요."
        nicknameNoticeElement.classList.add('error')
        nicknameNoticeElement.classList.remove('hidden')
        return;
      }
      alert("[ERROR]" + res.code + " : " + res.message)
      return;
    }
    checkedNickname = nickname;

    nicknameNoticeElement.innerText = "사용가능한 유저명입니다."
    nicknameNoticeElement.classList.remove('error')
    nicknameNoticeElement.classList.remove('hidden')
    isNicknameChecked = true;

  } catch (error) {
    console.error(error);
    isNicknameChecked = false;
  }
}