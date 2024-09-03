let eventSource = null;
const joinNotice = document.getElementById('joinNotice')
const postNotice = document.getElementById('postNotice')

window.addEventListener("load",async () => {
  console.log("Page loaded, initializing...");
  connect(); // SSE 연결
  await loadNotifications("api/notifications/admin/joinNotification", joinNotice);
  await loadNotifications("api/notifications/admin/postNotification", postNotice);
})

function connect() {

  if (eventSource) {
    console.log(
        "SSE connection already exists, closing previous connection...");
    eventSource.close();
  }

  eventSource = new EventSource("http://localhost:8080/api/notifications/admin/connect")

  eventSource.onopen = () => console.log("SSE connection opened")

  eventSource.addEventListener("connect", (e) => {
    const {data: receivedConnectData } = e;
    console.log("connect event data: ", receivedConnectData); // 'connected'
  })

  eventSource.addEventListener("notification", async (e) => {
    const notification = JSON.parse(e.data);

    console.log("connect event data: ", notification); // 'connected'
    if (notification["type"] === "JOIN_NOTIFICATION") {
      addNotification(notification, joinNotice)

    } else if (notification["type"] === "POST_NOTIFICATION") {
      addNotification(notification, postNotice)
    }
  })

  eventSource.onclose = () => {
    console.log("SSE connection closed");
    eventSource = null; // 연결이 닫힌 후에는 eventSource를 null로 설정하여 추적 초기화
  };
}

async function loadNotifications(url, element) {
  try {
    const response = await fetch(url);

    if(!response.ok) {
      throw new Error("Bad Request")
    }
    const data = await response.json();


    data.forEach(notice => {
      addNotification(notice, element);
    })

  } catch (error) {
    console.log(error);
  }
}

function addNotification(data, element) {
  const listItem = document.createElement("li");
  listItem.className = 'notification-item';

  const notificationContent = document.createElement("div")
  notificationContent.className = 'notification-content';

  const notificationMessage = document.createElement("span")
  notificationMessage.className = 'notification-message';
  notificationMessage.innerText = data.message;


  const notificationCreateAt = document.createElement("span")
  notificationCreateAt.className = 'notification-createAt';
  const [datePart, timePart] = data.createdAt.split('T');
  const [hours, minutes, seconds] = timePart.split(':').map(part => part.split('.')[0]);;
  notificationCreateAt.innerText = `${datePart} ${hours}:${minutes}:${seconds}`

  notificationContent.appendChild(notificationMessage)
  notificationContent.appendChild(notificationCreateAt)
  listItem.appendChild(notificationContent);
  element.prepend(listItem);
}