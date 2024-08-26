const sendBtnElement = document.getElementById("sendBtn");

sendBtnElement.addEventListener("click", sendChangePasswordLink);

const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;

async function sendChangePasswordLink(event) {
  event.preventDefault();

  const formElement = document.getElementById('findPasswordForm');
  const emailErrorElement = document.querySelector('.email-error');
  const resultMsgElement = document.querySelector('.resultMsg');

  const email = formElement.email.value;

  const jsonData = {
    email:email
  }

  console.log(jsonData)

  if (!emailPattern.test(email)) {
    emailErrorElement.innerText = "올바른 이메일 형식이 아닙니다.";
    emailErrorElement.classList.remove('hidden');
    return
  } else {
    emailErrorElement.innerText = "";
    emailErrorElement.classList.add('hidden');
  }

  const response = await fetch('/api/users/email-changePassword/send', {
    method:'POST',
    headers: {
      'Content-Type' : 'application/json'
    },
    body: JSON.stringify(jsonData)
  });

  const data = await response.json();

  if (!response.ok) {
    if(data.code === "GE-001") {
      resultMsgElement.innerText = "해당 이메일로 가입된 유저를 찾을 수 없습니다."
      return
    }
    emailErrorElement.innerText = "올바르지 않은 이메일 입력입니다."
    resultMsgElement.innerText = ""
    emailErrorElement.classList.remove('hidden')
      return
  }

    resultMsgElement.innerText =
        "해당 이메일 주소로 비밀번호 변경 링크 메일이 전송되었습니다. "
        + "\n 5분 이내로 전송된 링크를 통해 비밀번호를 변경해주세요."
}