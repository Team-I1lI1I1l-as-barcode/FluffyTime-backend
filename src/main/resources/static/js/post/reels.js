document.addEventListener('DOMContentLoaded', function() {
  let page = 1;  // 현재 페이지 번호를 저장하는 변수
  let loading = false;  // 데이터 로딩 중인지 여부를 나타내는 플래그

  const reelsContainer = document.getElementById('reels-list');  // 동영상 리스트를 담을 컨테이너

  // Reels 데이터를 로드하여 화면에 표시하는 함수
  function loadReels(page) {
    if (loading) return;

    loading = true;
    fetch(`/api/reels?page=${page}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json();
    })
    .then(reelsList => {
      console.log("API 응답 데이터: ", reelsList);

      if (!reelsList || reelsList.length === 0) {
        console.warn("받은 데이터가 비어 있습니다.");
        loading = false;
        return;
      }

      reelsList.reverse().forEach(reel => {
        const reelItem = document.createElement('div');
        reelItem.className = 'reel-item';

        const video = document.createElement('video');
        video.src = reel.fileUrl;
        video.controls = true;
        video.autoplay = true;
        video.muted = true;
        video.loop = true;

        const overlay = document.createElement('div');
        overlay.className = 'overlay';

        const profileContainer = document.createElement('div');
        profileContainer.className = 'profile-container';

        const profileImage = document.createElement('img');
        profileImage.src = reel.profileImageUrl;
        profileImage.alt = '프로필 이미지';

        const nickname = document.createElement('div');
        nickname.className = 'nickname';
        nickname.innerText = reel.authorNickname;

        const content = document.createElement('div');
        content.className = 'content';
        content.innerText = reel.postContent;

        profileContainer.appendChild(profileImage);
        profileContainer.appendChild(nickname);
        overlay.appendChild(profileContainer);
        overlay.appendChild(content);

        reelItem.appendChild(video);
        reelItem.appendChild(overlay);
        reelsContainer.appendChild(reelItem);
      });

      loading = false;

      if (reelsList.length === 0) {
        window.removeEventListener('scroll', onScroll);
      }
    })
    .catch(error => {
      console.error('Error loading reels:', error);
      loading = false;
    });
  }

  // 스크롤 이벤트 처리 함수
  function onScroll() {
    // 사용자가 페이지 끝에 도달했을 때 추가 데이터를 로드
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
      page++;  // 페이지 번호 증가
      loadReels(page);  // 다음 페이지의 Reels 데이터 로드
    }
  }

  window.addEventListener('scroll', onScroll);  // 스크롤 이벤트 리스너 추가

  // 초기 로드: 첫 번째 페이지의 Reels 데이터를 로드
  loadReels(page);
});
