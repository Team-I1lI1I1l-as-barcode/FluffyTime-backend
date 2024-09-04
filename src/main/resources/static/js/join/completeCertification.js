const succeedCertificationBtn = document.getElementById(
    'succeedBtn');

const pathname = window.location.pathname;

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