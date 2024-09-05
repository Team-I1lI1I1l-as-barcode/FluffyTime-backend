// 팔로우버튼 클릭 api 호출 함수
async function toggleFollow(button, action, targetUserNickname) {
  console.log("팔로우/언팔로우 api 호출");
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
    await updateFollowCounts(targetUserNickname); // 팔로우 수 갱신
  } catch (error) {
    console.error('toggleFollow() Error:', error);
  }
}

// 팔로우 상태 확인 함수
async function checkFollowStatus(button, targetUserNickname) {
  console.log("팔로우 상태 확인 api 호출");
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
  console.log("버튼 상태 갱신 api 호출");
  if (isFollowing) {
    button.textContent = "팔로잉";
    button.dataset.action = "remove";
    button.style.backgroundColor = "#CCCCCC";
  } else {
    button.textContent = "팔로우";
    button.dataset.action = "add";
    button.style.backgroundColor = "#FFCC00";
  }
}

//팔로워/팔로우 수 갱신 함수
async function updateFollowCounts(nickname) {
  console.log("팔로워/팔로우 카운트 갱신 api 호출");
  try {
    const response = await fetch(`/api/follow/count/${nickname}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      throw new Error('팔로워/팔로잉 수 조회 중 오류 발생');
    }

    const data = await response.json();
    if (document.getElementById("follower_count")) {
      document.getElementById(
          "follower_count").textContent = data.followerCount;
      document.getElementById("follow_count").textContent = data.followingCount;
    }
  } catch (error) {
    console.error('updateFollowCounts() Error:', error);
  }
}

// 팔로우 버튼 클릭 이벤트 핸들러
async function handleFollowButtonClick(event, targetUserNickname) {
  const button = event.target.closest(".follow_button");
  if (!button) {
    return;
  } // 클릭된 요소가 팔로우 버튼이 아니면 무시

  const action = button.dataset.action || "add"; // 초기 액션은 "add"

  await checkFollowStatus(button, targetUserNickname);
  await toggleFollow(button, action, targetUserNickname);
}

//모달

// 모달 열기 함수
function openModal(modal) {
  modal.style.display = "block";
}

// 모달 닫기 함수
function closeModal(modal) {
  modal.style.display = "none";
}

// 모달 외부 클릭 시 닫기 함수
window.onclick = function (event) {
  const followerModal = document.getElementById('followerList-modal');
  const followingModal = document.getElementById('followingList-modal');

  // 팔로워 모달이 열려 있고, 클릭했던 대상이 팔로워 모달인 경우 모달 닫기
  if (followerModal && event.target === followerModal) {
    closeModal(followerModal);
  }

  // 팔로잉 모달이 열려 있고, 클릭했던 대상이 팔로잉 모달인 경우 모달 닫기
  if (followingModal && event.target === followingModal) {
    closeModal(followingModal);
  }
}

// 모달 닫기 버튼 클릭 시 닫기 함수
document.querySelectorAll('.follow-close').forEach(closeButton => {
  closeButton.onclick = function () {
    const followerModal = document.getElementById('followerList-modal');
    const followingModal = document.getElementById('followingList-modal');

    if (followerModal && followerModal.style.display === 'block') {
      closeModal(followerModal);
    } else if (followingModal && followingModal.style.display === 'block') {
      closeModal(followingModal);
    }
  };
});

//팔로우/팔로잉 유저 목록 가져오기
async function fetchList(type, nickname) {
  console.log(`${type} 리스트 API 호출`);
  try {
    const response = await fetch(`/api/follow/search/${type}s/${nickname}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      throw new Error(`${type} 목록을 가져오는 중 오류 발생`);
    }

    const items = await response.json();
    const listElement = document.getElementById(`${type}List`);
    listElement.innerHTML = ''; // 기존 목록 초기화

    for (const item of items) {
      const li = document.createElement('li');
      li.className = `${type}-item`; // 클래스 이름 추가

      const linkBox = document.createElement('div');
      linkBox.className = 'linkBox';

      const img = document.createElement('img');
      img.src = item.profileImageUrl; // 프로필 이미지 URL 설정
      img.alt = `${item.nickname}의 프로필 이미지`; // 이미지 대체 텍스트 설정
      img.className = `${type}-profile-image`; // 프로필 이미지에 대한 클래스 이름 설정

      const details = document.createElement('div');
      details.className = 'details';

      const nicknameDiv = document.createElement('div');
      nicknameDiv.className = 'nickname';
      nicknameDiv.textContent = item.nickname; // 팔로워/팔로잉 닉네임 표시

      const intro = document.createElement('div');
      intro.className = 'intro';
      intro.textContent = item.intro; // 팔로워/팔로잉의 한줄 소개 표시

      // "자기 소개 없음"이면 글씨 회색으로
      if (intro.textContent === "자기 소개 없음") {
        intro.style.color = '#A9A9A9';
      }

      details.appendChild(nicknameDiv);
      details.appendChild(intro);

      linkBox.appendChild(img);
      linkBox.appendChild(details);

      // 클릭 시 유저 페이지로 이동
      linkBox.addEventListener('click', () => {
        window.location.href = `/userpages/${item.nickname}`;
      });

      li.appendChild(linkBox);

      if (item.targetUserId !== item.myUserId) {

        //각 유저에 대해 팔로우버튼 넣어주기
        const followButton = document.createElement('button');
        followButton.className = 'follow_button';
        followButton.textContent = '팔로우'; // 기본 텍스트
        li.appendChild(followButton);

        // 팔로우 상태를 확인하고 버튼 상태를 업데이트
        await checkFollowStatus(followButton, item.nickname);

        // 팔로우 버튼에 이벤트 핸들러 추가
        followButton.addEventListener('click', async (event) => {
          event.stopPropagation(); // 부모의 클릭 이벤트가 발생하지 않도록 막음
          await handleFollowButtonClick(event, item.nickname);
          await updateFollowCounts(nickname); // 팔로우/언팔로우 후 숫자 업데이트
        });
      }

      // 리스트 항목들을 리스트에 추가
      listElement.appendChild(li);
    }

    const modal = document.getElementById(`${type}List-modal`);
    openModal(modal);
  } catch (error) {
    console.error(
        `fetch ${type}List() Error:`, error);
  }
}

// 팔로워 리스트 API 호출 함수
async function fetchFollowerList(nickname) {
  await fetchList('follower', nickname);
}

// 팔로잉 리스트 API 호출 함수
async function fetchFollowingList(nickname) {
  await fetchList('following', nickname);
}

// 페이지 로드 시 이벤트 핸들러 설정
document.addEventListener("DOMContentLoaded", async function () {
  //비동기 팔로우 버튼이 있는 페이지들 정의
  const asynchronousPages = ['userpages', 'mypage'];

  // 경로를 '/'로 분리하여 배열로 만들고, userpage, mypage 인지 검증하고 맞는 경우 마지막 요소인 유저 닉네임을 얻는다.
  const path = window.location.pathname;
  const pathSegments = path.split('/');

  //mypage혹은 userpage인 경우
  if (asynchronousPages.includes(pathSegments[pathSegments.length - 2])) {

    const targetUserNickname = pathSegments[pathSegments.length - 1];

    // 팔로우 상태를 확인하고 상태를 화면에 적용
    const followButton = document.querySelector(".follow_button");
    if (followButton) {//팔로우 버튼이 있는 경우에만 팔로우 버튼 상태확인(마이페이지는 없음)
      await checkFollowStatus(followButton, targetUserNickname);
    }
    //팔로우,팔로잉 유저 수를 업데이트
    await updateFollowCounts(targetUserNickname);

    // 정적으로 생성된(화면에 무조건 있는) 팔로우 버튼에 대한 이벤트 핸들러
    document.body.addEventListener("click", function (event) {
      handleFollowButtonClick(event, targetUserNickname);
    });

    // 팔로워 목록 불러오기 클릭 이벤트
    const followerText = document.querySelector("#follower_list_modal");
    if (followerText) {
      followerText.addEventListener("click", async function () {
        await fetchFollowerList(targetUserNickname);
      });
    }

    // 팔로잉 목록 불러오기 클릭 이벤트
    const followingText = document.querySelector("#following_list_modal");
    if (followingText) {
      followingText.addEventListener("click", async function () {
        await fetchFollowingList(targetUserNickname);
      });
    }

    //게시물 상세보기(detail) 페이지인 경우
  } else if (pathSegments[pathSegments.length - 2] === "detail") {

    document.addEventListener('postDataLoaded', async (event) => {

      const postData = event.detail;  // postData를 postDetail.js의 이벤트로부터 가져옴
      console.log('postData를 사용하여 follow.js에서 작업 수행:', postData.nickname);

      const targetUserNickname = postData.nickname;
      console.log("포스트 작성 유저 닉네임: " + targetUserNickname);

      // 팔로우 상태를 확인하고 상태를 화면에 적용
      const followButton = document.querySelector(".follow_button");
      if (followButton) {//팔로우 버튼이 있는 경우에만 팔로우 버튼 상태확인(마이페이지는 없음)
        console.log("팔로우 버튼 존재!");
        await checkFollowStatus(followButton, targetUserNickname);
      }

      // 정적으로 생성된(화면에 무조건 있는) 팔로우 버튼에 대한 이벤트 핸들러
      followButton.addEventListener("click", function (event) {
        event.stopPropagation();
        handleFollowButtonClick(event, targetUserNickname);
      });
    });

  } else {
    console.log("이 페이지에는 팔로우 버튼이 없습니다.")
  }

});