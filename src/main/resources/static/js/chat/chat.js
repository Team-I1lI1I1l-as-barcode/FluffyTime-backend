// DOM 요소 선택 함수
const getElement = (id) => document.getElementById(id);

// div
const roomList = getElement('chat-room-list'); // 메시지방 리스트
const chatMessages = getElement('chatMessages'); // 메시지 출력 구역 div
const emptyMessage = getElement('empty-message'); // 메시지방에 접속 안했을 때 나오는 구역 div
const chatHeader = getElement('chat-header'); // 채팅 구역 div

const sendMessageBtn = getElement('sendMessage'); // 메시지방 만들기 버튼
const recipientProfile = getElement('recipient_profile'); // 수신자 프로필 사진
const recipient = getElement('recipient'); // 수신자 이름
const recipientPetName = getElement('recipient_pet_name'); // 수신자 반려동물 이름

// 추적을 위한 temp
let currentSelectedChatItem = null; // 현재 선택된 chatItem을 추적하는 변수
let roomRecipient = null; // 현재 채널 방 주인(본인)이 누구인지 추적하는 변수

// 웹소켓
let ws;

// 전역 상태로 각 채팅방의 최근 메시지를 저장
const recentMessages = {};

// 초기화
function initialize() {
  // 초기화 채팅 목록 가져오기
  fetchChat("/chat/topics", "GET", chatRoomInfo);
  getElement("message").focus();
}

window.onload = initialize;

// 메시지방 만들기 클릭 시 검색으로 이동
sendMessageBtn.addEventListener("click", () => {
  window.location.href = "/search";
});

// API 요청 함수
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
        window.history.back(); // 이전 페이지로 이동
      });
    }
    return response.json();
  })
  .then(data => func(data))
  .catch(error => {
    console.error("서버 오류 발생: " + error);
  });
}

// 토픽 목록 불러오기 API 응답 처리 함수 - chat 페이지에 관련된 모든 내용 불러오기
function chatRoomInfo(data) {
  let count = 0;

  // 채팅방이 없을 경우
  if (data.chatRoomList === null) {
    console.log("채팅방이 없습니다.");
  } else {
    console.log("채팅방 목록을 불러옵니다.");
    roomList.innerHTML = '';
    // 채팅방 리스트 출력하기
    data.chatRoomList.forEach(roomName => {
      const chatRoomDiv = document.createElement('div'); // 채팅방 정보를 담을 div(프로필사진, 수신자, 최근 채팅 내역)
      const chatRoom = document.createElement('p'); // 수신자 명(ui에서 보이는 채팅방 이름)
      const recentChat = document.createElement('p'); // 최근 채팅 내역
      const profileImage = document.createElement('img'); // 프로필 이미지
      let recipient = data.recipient[count]; // 수신자 이름 설정

      // 태그에 class 추가
      chatRoomDiv.classList.add('chat-item');
      profileImage.classList.add('profile_Image');
      recentChat.classList.add('recent_chat');

      // 각 채팅방별 수신자이름, 메시지방, 프로필 사진, 최근 채팅 내역 가져오기
      count = loadChatRoomList(data, count, chatRoomDiv, chatRoom, recentChat,
          profileImage, roomList);

      // 각 채팅방 영역 클릭 시
      chatRoomDiv.addEventListener('click', () => {
        // WebSocket 실시간 메시징 기능
        setupWebSocket(roomName, recipient, recentChat);
        // 메시지 출력 구역 보이기
        chatMessages.style.display = "flex";
        // 채팅 입력바 보이기
        getElement('chat-input').style.display = "flex";
        // 선택된 채팅방 배경 변경
        RoomChangeColor(chatRoomDiv, recentChat, recipient);

        // 수신자의 정보 불러오기
        fetchChat(`/chat/recipient/${encodeURIComponent(recipient)}`, 'GET',
            recipientInfo);

        // 메시지 리스트 가져오기
        fetchChat(`/chat/log/${encodeURIComponent(roomName)}`, 'GET', chatLog);

        // 채팅 참여하기
        fetchChat(`/chat/topics/${encodeURIComponent(roomName)}`, "GET",
            ServerResponse);
      });

      // 초기 로드 시에도 recentMessages를 업데이트
      recentMessages[roomName] = data.recentChat[count];
    });
  }
}

// 채팅 목록 불러오기 함수
function loadChatRoomList(data, count, chatRoomDiv, chatRoom, recentChat,
    profileImage, roomList) {

  chatRoom.textContent = data.recipient[count]; // 메시지방 이름 설정
  fetchChat(`/chat/recipient/${encodeURIComponent(data.recipient[count])}`,
      'GET', (imageData) => {
        profileImage.src = imageData.fileUrl;
      });
  recentChat.textContent = recentMessages[data.chatRoomList[count]]
      || data.recentChat[count]; // 최근 채팅 내역 설정

  chatRoomDiv.appendChild(profileImage);
  chatRoomDiv.appendChild(chatRoom);
  chatRoomDiv.appendChild(recentChat);
  chatRoomDiv.dataset.roomName = data.chatRoomList[count]; // 채팅방 이름을 데이터 속성으로 저장
  roomList.appendChild(chatRoomDiv);
  count += 1;
  return count;
}

// 채팅방 클릭 시 해당 채팅방 영역 배경색 변경 함수
function RoomChangeColor(chatRoomDiv, recentChat, recipient) {
  // 이전에 선택된 chatRoomDiv 배경색 초기화
  if (currentSelectedChatItem && currentSelectedChatItem !== chatRoomDiv) {
    currentSelectedChatItem.style.background = null;
  }

  // 현재 선택된 chatRoomDiv의 배경색 설정
  chatRoomDiv.style.background = "#fbc02d";
  currentSelectedChatItem = chatRoomDiv; // 현재 선택된 chatRoomDiv을 갱신
  console.log(`${recipient}와 채팅을 시작합니다.`);
}

// 수신자 정보 가져오는 API 응답 처리 함수
function recipientInfo(data) {
  recipient.innerText = data.nickname;
  recipientPetName.innerText = data.petName;
  recipientProfile.src = data.fileUrl;

  emptyMessage.style.display = "none";
  chatHeader.style.display = "flex";
}

// 메시지 내역을 가져오는 API 응답 처리 함수
function chatLog(data) {
  chatMessages.innerHTML = '';
  if (data.chatLog != null) {
    console.log("채팅 내역을 가져옵니다.");
    data.chatLog.forEach(log => {
      const messageElement = document.createElement("p");
      messageElement.innerText = log.split(":").pop().trim();
      if (data.sender === log.split(":")[0].trim()) { // 메시지를 보내는 사람과 실제 채널에서 발신자와 같다면
        messageElement.classList.add('sender');
      } else {
        messageElement.classList.add('receiver');
      }
      chatMessages.appendChild(messageElement);
    });
    chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 자동으로 내리기 (최신 메시지를 보기 위함)
  }
}

// 채팅과 관련해서 요청 보낸 API 응답 처리 함수(채팅방 참여 여부)
function ServerResponse(data) {
  console.log(data.success ? "요청 성공" : "요청 실패");
}

// 웹소켓을 사용하여 실시간으로 메시지를 주고 받는 기능 구현 함수
function setupWebSocket(roomName, recipient, recentChatElement) {
  if (ws) {
    ws.close(); // 기존 WebSocket 연결 종료
  }
  ws = new WebSocket(`wss://fluffytime.kro.kr/ws?room=${roomName}`);

  ws.onmessage = function (event) {
    console.log('Message received: ', event.data);
    const sender = event.data.split(':')[0].trim(); // 메시지 발신자
    const text = event.data.split(":").pop().trim(); // 메시지 내용

    // 최근 메시지 업데이트
    recentMessages[roomName] = text;

    // UI 업데이트: 현재 채팅방이 열려 있을 때만
    if (currentSelectedChatItem && currentSelectedChatItem.dataset.roomName
        === roomName) {
      recentChatElement.innerText = text;

      const messageElement = document.createElement("p");
      messageElement.innerText = text;
      if (sender === recipient) { // 메시지를 보내는 사람과 수신자가 같다면
        messageElement.classList.add('receiver');
      } else {
        messageElement.classList.add('sender');
      }

      chatMessages.appendChild(messageElement);
      chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 자동으로 내리기
    } else {
      // 채팅방이 열려 있지 않을 때, 필요 시 알림 처리
    }
  };

  ws.onopen = function () {
    console.log('WebSocket connection established.');
  };

  ws.onerror = function (error) {
    console.error('WebSocket error: ', error);
  };

  ws.onclose = function (event) {
    console.log('WebSocket connection closed. Reason:', event.reason);
  };
}

// 메시지 전송 함수
function sendMessage() {
  const message = getElement("message").value;
  if (message.trim() !== "") {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(message);
      getElement("message").value = '';
      getElement("message").focus();
    } else {
      console.warn('WebSocket is not connected.');
    }
  }
}

// 엔터를 치면 메시지 전송
getElement("message").addEventListener("keypress", function (event) {
  if (event.key === "Enter") {
    sendMessage();
    event.preventDefault();
  }
});
