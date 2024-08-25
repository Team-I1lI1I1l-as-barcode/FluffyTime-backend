//일단 정적 페이지에 대한 팔로우만.. ex)userpage

// 버튼 클릭 후 api 호출 함수
async function toggleFollow(button, action, targetUserNickname) {
  const url = action === "add" ? "/api/follow/add" : "/api/follow/remove";

  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        followedUserNickname: targetUserNickname
      })
    });

    if (!response.ok) {
      throw new Error('팔로우/언팔로우 api 호출 결과에서 오류 발생!');
    }

    await checkFollowStatus(button, targetUserNickname); // 상태 갱신
  } catch (error) {
    console.error('toggleFollow() Error:', error);
  }
}

// 팔로우 상태 확인 함수
async function checkFollowStatus(button, targetUserNickname) {
  try {
    const response = await fetch(
        `/api/follow/status?nickname=${encodeURIComponent(targetUserNickname)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          }
        });

    if (!response.ok) {
      throw new Error('팔로우 상태 확인 중 오류 발생!');
    }

    const isFollowing = await response.json();
    updateButtonStatus(button, isFollowing);
  } catch (error) {
    console.error('checkFollowStatus() Error:', error);
  }
}

//버튼 상태 갱신 함수
function updateButtonStatus(button, isFollowing) {
  if (isFollowing) {
    button.textContent = "팔로잉";
    button.dataset.action = "remove";
  } else {
    button.textContent = "팔로우";
    button.dataset.action = "add";
  }
}

// 팔로우 버튼 클릭 이벤트 핸들러
async function handleFollowButtonClick(event) {
  const button = event.target.closest(".follow_button");
  if (!button) {
    return;
  } // 클릭된 요소가 팔로우 버튼이 아니면 무시

  const path = window.location.pathname;
  // 경로를 '/'로 분리하여 배열로 만든다.
  const pathSegments = path.split('/');
  // 배열의 마지막 요소가 유저 닉네임이다.
  const targetUserNickname = pathSegments[pathSegments.length - 1];

  const action = button.dataset.action || "add"; // 초기 액션은 "add"

  await checkFollowStatus(button, targetUserNickname);
  await toggleFollow(button, action, targetUserNickname);
}

// 페이지 로드 시 이벤트 핸들러 설정
document.addEventListener("DOMContentLoaded", function () {

  // 페이지 내 모든 팔로우 버튼에 대해 팔로우 상태를 확인하고 상태를 화면에 적용
  const followButtons = document.querySelectorAll(".follow_button");
  followButtons.forEach(async (button) => {
    const path = window.location.pathname;
    const pathSegments = path.split('/');// 동적 생성 팔로우는 더 고려해봐야 함..
    const targetUserNickname = pathSegments[pathSegments.length - 1];
    await checkFollowStatus(button, targetUserNickname);
  });

  // 정적으로 생성된 팔로우 버튼에 대한 이벤트 핸들러
  document.body.addEventListener("click", handleFollowButtonClick);
});