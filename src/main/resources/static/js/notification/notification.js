let eventSource = null;

document.addEventListener('DOMContentLoaded', async () => {
  console.log("Page loaded, initializing...");

  try {
    const response = await fetch('/api/notifications/current-user');
    if (!response.ok) {
      throw new Error('유저 찾기 실패!');
    }

    const user = await response.json();
    const userId = user.userId;

    if (!userId) {
      console.error('User ID is required');
      return;
    }

    loadExistingNotifications(userId); // 알림 조회
    connectSSE(userId); // SSE 연결
  } catch (error) {
    console.error('Error fetching user infomation: ', error);
  }
});

async function loadExistingNotifications(userId) {
  if (!userId) {
    console.error('User ID is required');
    return;
  }

  try {
    console.log("Loading existing notifications...");
    const response = await fetch(`/api/notifications/all?userId=${userId}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const notifications = await response.json();
    const allList = document.getElementById('allNotificationList');
    allList.innerHTML = '';
    notifications.forEach(notification => {
      allList.appendChild(createNotificationElement(notification));
    });
  } catch (error) {
    console.error('Error loading notifications:', error);
  }
}

function connectSSE(userId) {
  if (!userId) {
    console.error('User ID is required');
    return;
  }

  if (eventSource) {
    console.log(
        "SSE connection already exists, closing previous connection...");
    eventSource.close();
  }

  eventSource = new EventSource(`/api/notifications/stream?userId=${userId}`);

  eventSource.onopen = () => console.log("SSE connection opened");

  eventSource.onerror = event => {
    console.error("SSE connection error:", event);
    eventSource.close();
    setTimeout(connectSSE, 5000); // 연결 오류 시 5초 후 재연결 시도
  };

  eventSource.addEventListener('notification', event => {
    console.log("Received notification:", event.data);
    const notification = JSON.parse(event.data);
    addNotificationToList(notification);
  });

  eventSource.addEventListener('connect', event => {
    console.log("SSE connected:", event.data);
  });

  eventSource.onclose = () => {
    console.log("SSE connection closed");
    eventSource = null; // 연결이 닫힌 후에는 eventSource를 null로 설정하여 추적 초기화
  };
}

function createNotificationElement(notification) {
  const listItem = document.createElement('li');
  listItem.className = 'notification';
  listItem.classList.toggle('read', notification.read);

  const notificationBox = document.createElement('div');
  notificationBox.className = 'notification-box';

  // 프로필 이미지를 위한 요소 생성
  const profileImage = document.createElement('img');
  profileImage.src = notification.profileImageurl;
  profileImage.alt = 'Profile Image';
  profileImage.className = 'profile-image';

  // 알림 메시지를 위한 요소 생성
  const notificationContent = document.createElement('div');
  notificationContent.className = 'notification-content';
  notificationContent.textContent = notification.message;

  // 프로필 이미지와 알림 메시지를 묶는 컨테이너 생성
  const contentContainer = document.createElement('div');
  contentContainer.className = 'notificaiton-content-container';
  contentContainer.appendChild(profileImage);
  contentContainer.appendChild(notificationContent);

  const deleteButton = document.createElement('button');
  deleteButton.textContent = '삭제';
  deleteButton.className = 'delete-button';
  deleteButton.onclick = async function (event) {
    event.stopPropagation();
    await deleteNotification(notification.notificationId, listItem);
  };

  notificationBox.appendChild(contentContainer);
  notificationBox.appendChild(deleteButton);
  listItem.appendChild(notificationBox);

  listItem.dataset.id = notification.notificationId;
  listItem.dataset.url = notification.postId
      ? `/posts/detail/${notification.postId}`
      : '#';

  listItem.onclick = function () {
    markAsRead(this);
    if (this.dataset.url !== '#') {
      window.location.href = this.dataset.url;
    }
  };

  return listItem;
}

async function deleteNotification(notificationId, listItem) {
  if (confirm('알림을 삭제하시겠습니까?')) {
    try {
      const response = await fetch(
          `/api/notifications/${notificationId}/delete`, {method: 'DELETE'});
      if (!response.ok) {
        throw new Error('Failed to delete notification');
      }

      listItem.remove();
      console.log("Notification deleted successfully");
    } catch (error) {
      console.error('Error deleting notification:', error);
      alert('알림 삭제 중 문제가 발생했습니다: ' + error.message);
    }
  }
}

function addNotificationToList(notification) {
  const list = document.getElementById('allNotificationList');
  if (list) {
    list.insertBefore(createNotificationElement(notification), list.firstChild);
  } else {
    console.error(
        `Notification list element not found for type: ${notification.type}`);
  }
}

async function markAsRead(element) {
  const notificationId = element.dataset.id;
  if (!element.classList.contains('read')) {
    try {
      const response = await fetch(`/api/notifications/${notificationId}/read`,
          {method: 'POST'});
      if (response.ok) {
        element.classList.add('read');
      }
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  }
}
