const findBtn = document.getElementById('findBtn');

findBtn.addEventListener("click", findEmail);

const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;

async function findEmail(event) {
  event.preventDefault();

  const formElement = document.getElementById('findEmailForm');
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

  const response= await fetch('/api/users/find-email', {
    method: 'POST',
    headers: {
      'Content-Type':'application/json'
    },
    body: JSON.stringify(jsonData)
  });

  const data  = await response.json();

  if (!response.ok) {
    emailErrorElement.innerText = "올바르지 않은 이메일 입력입니다."
    resultMsgElement.innerText = ""
    emailErrorElement.classList.remove('hidden')
    return;
  }

  if(data.isExists) {
    resultMsgElement.innerText = "해당 이메일로 가입한 유저가 존재합니다."
  } else {
    resultMsgElement.innerText = "해당 이메일로 가입된 유저 정보를 찾을 수 없습니다."
  }
}