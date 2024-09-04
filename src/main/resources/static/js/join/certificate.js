document.addEventListener("DOMContentLoaded", getCertificationResult);

const pathname = window.location.pathname;

const match = pathname.match(/([^\/]+)\/?$/);
const email = match ? match[1] : '';

async function getCertificationResult() {
  const successMessageElement = document.querySelector(".success-message")
  const failMessageElement = document.querySelector(".fail-message")

  try {
    const response = await fetch(
        `/api/users/email-certification?email=${email}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          },
        });

    if (!response.ok) {
      successMessageElement.classList.add('hidden');
      failMessageElement.classList.remove('hidden');
      return
    }
      successMessageElement.classList.remove('hidden');
      failMessageElement.classList.add('hidden');

  } catch (error) {
    console.error(error);
  }
}