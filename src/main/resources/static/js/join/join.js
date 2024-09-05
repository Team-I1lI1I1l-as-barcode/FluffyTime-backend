let isEmailChecked = false;
let checkedEmail = "";
let isNicknameChecked = false;
let checkedNickname = "";

const getCertificationNumberBtn = document.getElementById(
    "checkEmailBtn");
getCertificationNumberBtn.addEventListener("click", checkEmail);

const checkNicknameBtn = document.getElementById("checkNicknameBtn");
checkNicknameBtn.addEventListener("click", checkNickName);

const joinBtn = document.getElementById("joinBtn");
joinBtn.addEventListener("click", tempJoin);

const emailNoticeElement = document.querySelector('.email-notice');
const passwordNoticeElement = document.querySelector('.password-notice');
const nicknameNoticeElement = document.querySelector('.nickname-notice');

const passwordPattern = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,20}$/;
const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
const usernamePattern = /^[a-zA-Z0-9_-]+$/;

async function tempJoin(event) {
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
  console.log(checkedEmail)
  console.log(isEmailChecked)
  if (email !== checkedEmail || !isEmailChecked) {
    emailNoticeElement.innerText="이메일 중복확인을 완료해주세요."
    emailNoticeElement.classList.add('error')
    emailNoticeElement.classList.remove('hidden')
    errorCount++;
  } else {
    emailNoticeElement.innerText=""
    emailNoticeElement.classList.remove('error')
    emailNoticeElement.classList.add('hidden')
  }

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

  // 비밀번호 유효성 검사
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
        '/api/users/temp-join', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(jsonData)
        });

    const res = await response.json();

    if (!response.ok) {
      joinBtn.innerText="회원가입"
      if (res.code === "GE-010") {
        alert("올바른 회원가입 데이터 형식이 아닙니다.")
        return
      } else {
        alert("[ERROR]" + res.code + " : " + res.message)
        return
      }
    }

    joinBtn.innerText="인증메일 전송 중..."

    const sendResult = await getCertificationEmail();
    if (sendResult) {
      window.location.href = response.headers.get("Location"); // 원하는 URL로 변경
    }
  } catch (error) {
    console.error(error);
  }
}

async function checkEmail() {
  const emailElement = document.getElementById('email');
  const email = emailElement.value;

  if (!emailPattern.test(email)) {
    emailNoticeElement.innerText = "올바른 이메일 형식이 아닙니다.";
    emailNoticeElement.classList.add('error')
    emailNoticeElement.classList.remove('hidden');
    return;
  } else {
    emailNoticeElement.innerText = "";
    emailNoticeElement.classList.add('hidden');
    emailNoticeElement.classList.remove('error');
  }

  try {
    const response = await fetch(
        `/api/users/check-email?email=${email}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const res = await response.json();

    if (!response.ok) {
      isEmailChecked = false;
      if (res.code === "JE-001") {
        emailNoticeElement.innerText="이미 사용중인 이메일입니다. 다른 이메일을 입력해주세요."
        emailNoticeElement.classList.add('error')
        emailNoticeElement.classList.remove('hidden')
        return;
      }
      alert("[ERROR]" + res.code + " : " + res.message)
      return;
    }
    checkedEmail = email;

    emailNoticeElement.innerText="사용가능한 이메일입니다."
    emailNoticeElement.classList.remove('error')
    emailNoticeElement.classList.remove('hidden')
    isEmailChecked = true;
  } catch (error) {
    console.error(error);
    isEmailChecked = false;
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

async function getCertificationEmail() {
  const emailElement = document.getElementById('email');

  try {
    const response = await fetch(
        `/api/users/email-certification/send?email=${emailElement.value}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const res = await response.json();

    if (!response.ok) {
      joinBtn.innerText = "회원가입"
      alert("[ERROR]" + res.code + " : " + res.message)

      return false;
    }

    return true;
  } catch (error) {
    console.error(error);
    return false;
  }
}