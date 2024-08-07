let isEmailChecked = false;
let checkedEmail = "";
let isNicknameChecked = false;
let checkedNickname = "";

const getCertificationNumberBtn = document.getElementById(
    "certificationNumberBtn");
getCertificationNumberBtn.addEventListener("click", checkEmail);

const checkNicknameBtn = document.getElementById("checkNicknameBtn");
checkNicknameBtn.addEventListener("click", checkNickName);

const joinBtn = document.getElementById("joinBtn");
joinBtn.addEventListener("click", tempJoin);

async function tempJoin(event) {
  event.preventDefault()

  // 폼 데이터 수집
  const formElement = document.getElementById("joinForm");

  const email = formElement.email.value;
  const password = formElement.password.value;
  const checkPassword = formElement.checkPassword.value;
  const nickname = formElement.nickname.value;

  if (!formElement.checkValidity()) {
    formElement.reportValidity(); // 브라우저의 기본 검증 메시지 표시
    return;
  }

  if (email !== checkedEmail) {
    alert("이메일 중복확인을 완료해주세요.")
    return
  }

  if (nickname !== checkedNickname) {
    alert("유저명 중복확인을 완료해주세요.")
    return
  }

  if (password !== checkPassword) {
    alert("비밀번호를 확인해주세요.")
    return
  }

  // 폼 데이터를 JSON으로 변환
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
      if (res.code === "GE-010") {
        alert("옳바른 회원가입 데이터 형식이 아닙니다.")
        return
      } else {
        alert("[ERROR]" + res.code + " : " + res.message)
        return
      }
    }

    await getCertificationEmail();

    window.location.href = '/join/email-certificate/' + email; // 원하는 URL로 변경

  } catch (error) {
    console.error(error);
  }
}

async function checkEmail() {
  const emailElement = document.getElementById('email');
  checkedEmail = emailElement.value;

  try {
    const response = await fetch(
        `/api/users/check-email?email=${emailElement.value}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const res = await response.json();

    if (!response.ok) {
      isEmailChecked = false;
      if (res.code === "JE-001") {
        alert("이미 사용중인 이메일입니다. 다른 이메일을 입력해주세요.")
        return;
      }
      alert("[ERROR]" + res.code + " : " + res.message)
      return;
    }

    isEmailChecked = true;
    alert("사용가능한 이메일입니다.")

  } catch (error) {
    console.error(error);
    isEmailChecked = false;
  }
}

async function checkNickName() {
  const nicknameElement = document.getElementById('nickname');
  checkedNickname = nicknameElement.value;

  try {
    const response = await fetch(
        `/api/users/check-nickname?nickname=${nicknameElement.value}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        });

    const res = await response.json();

    if (!response.ok) {
      isNicknameChecked = false;
      if (res.code === "JE-002") {
        alert("이미 사용중인 유저명입니다. 다른 유저명을 입력해주세요.")
        return;
      }
      alert("[ERROR]" + res.code + " : " + res.message)
      return;
    }

    isNicknameChecked = true;
    alert("사용가능한 유저명입니다.")

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
      alert("[ERROR]" + res.code + " : " + res.message)
      location.reload();
    }

  } catch (error) {
    console.error(error);
  }
}