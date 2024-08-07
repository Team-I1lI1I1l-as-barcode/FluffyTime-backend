document.addEventListener("DOMContentLoaded", getCertificationResult);

// 현재 URL의 경로를 가져옵니다.
const pathname = window.location.pathname;

// 정규 표현식을 사용하여 경로의 마지막 부분을 추출합니다.
const match = pathname.match(/([^\/]+)\/?$/);
const email = match ? match[1] : '';

async function getCertificationResult() {
  const certificateResultElement = document.querySelector(
      ".certificate_result");

  try {
    const response = await fetch(
        `/api/users/email-certification?email=${email}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          },
        });

    if (!response.ok) {
      certificateResultElement.innerHTML = "만료된 인증 요청"
      return
    }
    certificateResultElement.innerHTML = "이메일 인증 성공"

  } catch (error) {
    console.error(error);
  }
}