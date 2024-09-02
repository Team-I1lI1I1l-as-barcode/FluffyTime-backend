// DOM 요소 선택
const roomList = document.getElementById('chat-room-list');
const chatMessages = document.getElementById('chatMessages');
const emptyMessage = document.getElementById('empty-message');
const chatHeader = document.getElementById('chat-header');
const sendMessageBtn = document.getElementById('sendMessage');
const recipient = document.getElementById('recipient');
const recipientProfile = document.getElementById('recipient_profile');
const recipientPetName = document.getElementById('recipient_pet_name');
let currentSelectedChatItem = null; // 현재 선택된 chatItem을 추적하는 변수
let currentSelectedChat = null; // 현재 선택된 chat을 추적하는 변수
let ws; // 웹소켓
let roomRecipient = null; // 채널 방 주인(본인)

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
  roomList.innerHTML = '';
  let count = 0;
  data.chatRoomList.forEach(roomName => {
    const chatRoom = document.createElement('p');
    const recentChat = document.createElement('p');
    const profileImage = document.createElement('img');
    const chatItem = document.createElement('div');
    chatItem.classList.add('chat-item'); // class 이름 추가
    profileImage.classList.add('profile_Image'); // class 이름 추가
    recentChat.classList.add('recent_chat'); // class 이름 추가
    let recipient;
    if (data.recipient !== null) {
      chatRoom.textContent = data.recipient[count];
      recipient = data.recipient[count];
    }

    if (data.profileImages !== null) {
      profileImage.src = data.profileImages[count];
    }

    if (data.recentChat !== null) {
      recentChat.textContent = data.recentChat[count];
    }
    chatItem.appendChild(profileImage);
    chatItem.appendChild(chatRoom);
    chatItem.appendChild(recentChat);
    roomList.appendChild(chatItem);
    count += 1;
    chatItem.addEventListener('click', () => {
      // 이전에 선택된 chatItem의 배경색 초기화
      if (currentSelectedChatItem && currentSelectedChatItem !== chatItem) {
        currentSelectedChatItem.style.background = null;
      }

      // 현재 선택된 chatItem의 배경색 설정
      chatItem.style.background = "#fbc02d";
      currentSelectedChatItem = chatItem; // 현재 선택된 chatItem을 갱신
      currentSelectedChat = recentChat;
      console.log(`${recipient}와 채팅을 시작합니다.`);
      // 수신자의 정보 불러오기
      fetchChat(`/chat/recipient/${encodeURIComponent(recipient)}`, 'GET',
          recipientInfo);
      emptyMessage.style.display = "none";
      chatHeader.style.display = "flex";
      chatMessages.innerHTML = ''; // 기존 메시지 지우기

      // 메시지 리스트 가져오기
      fetchChat(`/chat/log/${encodeURIComponent(roomName)}`, 'GET', chatLog);

      if (ws) {
        ws.close(); // 기존 WebSocket 연결 종료
      }
      ws = new WebSocket(`ws://${window.location.host}/ws?room=${roomName}`);
      ws.onmessage = function (event) {
        console.log('Message received: ', event.data);
        const sender = event.data.split(':')[0].trim(); // 메시지 발신자

        const messageElement = document.createElement("p");
        messageElement.innerText = event.data.split(":").pop().trim();

        if (roomRecipient && roomRecipient !== recipient) {
          roomRecipient.className = '';
        }
        roomRecipient = recipient;

        if (sender === roomRecipient) {// 메시지를 보내는 사람과 실제 채널에서 수신자와 같다면
          messageElement.classList.add('receiver');
        } else {
          messageElement.classList.add('sender');
        }
        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 자동으로 내리기 (최신 메시지를 보기 위함)
        currentSelectedChat.textContent = event.data.split(':').pop().trim();
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
  });
}

function recipientInfo(data) {
  if (data != null) {
    recipient.innerText = data.nickname;
    // data.PetName이 null이거나 undefined인 경우 빈 문자열로 처리
    recipientPetName.innerText = data.petName;
    recipientProfile.src = data.fileUrl;
  }

}

function chatLog(data) {
  if (data.chatLog != null) {
    data.chatLog.forEach(log => {
      const messageElement = document.createElement("p");
      messageElement.innerText = log.split(":").pop().trim();
      if (data.sender === log.split(":")[0].trim()) {// 메시지를 보내는 사람과 실제 채널에서 발신자와 같다면
        messageElement.classList.add('sender');
      } else {
        messageElement.classList.add('receiver');
      }
      chatMessages.appendChild(messageElement);
    });
    chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 자동으로 내리기 (최신 메시지를 보기 위함)
  }
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