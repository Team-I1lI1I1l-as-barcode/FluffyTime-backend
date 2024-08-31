// DOM 요소 선택
const chatRooms = document.getElementById('chatRooms');
const chatMessages = document.getElementById('chatMessages');
const emptyMessage = document.getElementById('empty-message');
const chatHeader = document.getElementById('chat-header');
const sendMessageBtn = document.getElementById('sendMessage');
const recipient = document.getElementById('recipient');
const recipientProfile = document.getElementById('recipient_profile');
const recipientPetName = document.getElementById('recipient_pet_name');
let ws; // 웹소켓

function initialize() {
  fetchChat("/chat/topics", "GET", createChatRoomList);
  document.getElementById("message").focus();
}

sendMessageBtn.addEventListener("click", () => {
  window.location.href = "/search"
})

function fetchChat(url, method, func) {
  console.log("fetchChat 실행");
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.error("fetchChat 응답 에러 발생 >> " + errorData.message);
        alert('Error: ' + errorData.message);
        window.location.href = "/";
      });
    }
    return response.json();
  })
  .then(data => func(data))
  .catch(error => {
    console.error("서버 오류 발생: " + error);
  });
}

function createChatRoomList(data) {
  chatRooms.innerHTML = '';
  let count = 0;
  data.chatRoomList.forEach(roomName => {
    const chatRoom = document.createElement('p');
    chatRoom.textContent = data.recipient[count];
    const recipient = data.recipient[count];

    chatRoom.addEventListener('click', () => {
      console.log(`${recipient}와 채팅을 시작합니다.`);
      // 수신자의 정보 불러오기
      fetchChat(`/chat/recipient/${encodeURIComponent(recipient)}`,
          'GET', recipientInfo);
      emptyMessage.style.display = "none";
      chatHeader.style.display = "flex";
      chatMessages.innerHTML = ''; // 기존 메시지 지우기

      // 메시지 리스트 가져오기
      fetchChat(`/chat/log/${encodeURIComponent(roomName)}`,
          'GET', chatLog);

      if (ws) {
        ws.close(); // 기존 WebSocket 연결 종료
      }
      ws = new WebSocket(`ws://${window.location.host}/ws?room=${roomName}`);
      ws.onmessage = function (event) {
        console.log('Message received: ', event.data);
        const messageElement = document.createElement("p");
        messageElement.innerText = event.data;
        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
      };
      ws.onopen = function () {
        console.log('WebSocket connection established.');
      };
      ws.onerror = function (error) {
        console.error('WebSocket error: ', error);
      };
      ws.onclose = function () {
        console.log('WebSocket connection closed.');
      };
      fetchChat(`/chat/topics/${encodeURIComponent(roomName)}`, "GET",
          ServerResponse);
    });
    count += 1;
    chatRooms.appendChild(chatRoom);
  });
}

function recipientInfo(data) {
  recipient.innerText = data.nickname;
  // data.PetName이 null이거나 undefined인 경우 빈 문자열로 처리
  recipientPetName.innerText = data.petName;
  recipientProfile.src = data.fileUrl;

}

function chatLog(data) {
  data.chatLog.forEach(log => {
    const messageElement = document.createElement("p");
    messageElement.innerText = log;
    chatMessages.appendChild(messageElement);
  })
}

function ServerResponse(data) {
  console.log(data.success ? "요청 성공" : "요청 실패");
}

function sendMessage() {
  const message = document.getElementById("message").value;
  if (message.trim() !== "") {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(message);
      document.getElementById("message").value = '';
      document.getElementById("message").focus();
    } else {
      console.warn('WebSocket is not connected.');
    }
  }
}

document.getElementById("message").addEventListener("keypress",
    function (event) {
      if (event.key === "Enter") {
        sendMessage();
        event.preventDefault();
      }
    });

window.onload = initialize;