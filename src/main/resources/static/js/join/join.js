const joinBtn = document.getElementById("joinBtn");
joinBtn.addEventListener("click", join);

async function join(event) {
  event.preventDefault();

  // 폼 데이터 수집
  const formElement = document.getElementById("joinForm");

  // 폼 데이터를 JSON으로 변환
  const jsonData = {
    email: formElement.email.value,
    password: formElement.password.value,
    nickname: formElement.nickname.value
  };

  console.log(jsonData)

  // 서버로 데이터 전송
  await fetch('/api/users/join', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(jsonData)
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(data => {
        throw new Error(data.message || "error")
      })
    }
    return response.json();
  })
  .then(data => {
    console.log(data)
  })
}