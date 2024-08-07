const succeedCertificationBtn = document.getElementById(
    'succeedCertificationBtn');

// 현재 URL의 경로를 가져옵니다.
const pathname = window.location.pathname;

// 정규 표현식을 사용하여 경로의 마지막 부분을 추출합니다.
const match = pathname.match(/([^\/]+)\/?$/);
const email = match ? match[1] : '';

succeedCertificationBtn.addEventListener("click", join);

async function join() {
  try {
    const response = await fetch(
        `/api/users/join?email=${email}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          },
        });

    const res = await response.json();

    if (!response.ok) {
      if (res.code === "JE-004") {
        alert("이메일 링크 클릭 후 완료버튼을 눌러주세요.")
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