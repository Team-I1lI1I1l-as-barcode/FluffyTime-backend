document.addEventListener('DOMContentLoaded', function() {
  let page = 1; // 현재 페이지 번호
  let loading = false; // 데이터를 로딩 중인지 여부
  let isBookmarkProcessing = false; // 북마크 요청이 처리 중인지 여부

  const reelsContainer = document.getElementById('reels-list'); // 릴스 항목을 담을 컨테이너

  // 리일 데이터를 불러오는 함수
  function loadReels(page) {
    if (loading) return; // 이미 로딩 중이면 중복 로딩 방지

    loading = true; // 로딩 시작
    fetch(`/api/reels?page=${page}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json(); // 응답 데이터를 JSON으로 파싱
    })
    .then(reelsList => {
      console.log("API 응답 데이터: ", reelsList);

      if (!reelsList || reelsList.length === 0) {
        console.warn("받은 데이터가 비어 있습니다.");
        loading = false;
        return; // 데이터가 없으면 로딩 종료
      }

      reelsList.reverse().forEach(async reel => {
        const reelItem = document.createElement('div'); // 릴스 항목을 담을 div 생성
        reelItem.className = 'reels-reel-item';

        const video = document.createElement('video'); // 비디오 요소 생성
        video.src = reel.filepath;
        video.controls = true;
        video.autoplay = true;
        video.muted = true;
        video.loop = true;

        const overlay = document.createElement('div'); // 비디오 위에 올려질 오버레이 생성
        overlay.className = 'reels-overlay';

        const profileContainer = document.createElement('div'); // 프로필 정보를 담을 컨테이너 생성
        profileContainer.className = 'reels-profile-container';

        const profileImage = document.createElement('img'); // 프로필 이미지 생성
        profileImage.src = reel.profileImageUrl || '../../../image/profile/profile.png';
        profileImage.alt = '프로필 이미지';
        console.log("프로필 이미지 경로: ", profileImage.src);

        const nickname = document.createElement('div'); // 닉네임 표시 영역 생성
        nickname.className = 'reels-nickname';
        nickname.innerText = reel.nickname;

        const content = document.createElement('div'); // 릴스 내용 표시 영역 생성
        content.className = 'reels-content';
        content.innerText = reel.content;

        // 북마크 아이콘 생성 및 초기화
        const bookmarkIcon = document.createElement('img');
        bookmarkIcon.className = 'bookmark-icon';

        // 북마크 상태를 확인하고 아이콘 설정
        await initializeBookmarkState(reel.postId, bookmarkIcon);

        // 북마크 클릭 이벤트 처리
        bookmarkIcon.addEventListener('click', () => {
          toggleBookmark(reel.postId, bookmarkIcon); // 북마크 상태 토글
        });

        // 좋아요 아이콘 생성 및 초기화
        const likeIcon = document.createElement('span');
        likeIcon.className = 'like-button material-icons';
        likeIcon.textContent = reel.isLiked ? 'favorite' : 'favorite_border'; // 초기 상태 설정
        likeIcon.addEventListener('click', () => {
          toggleLikePost(reel.postId, likeIcon); // 좋아요 상태 토글
        });

        const likeCountSpan = document.createElement('span');
        likeCountSpan.className = 'like-count';
        likeCountSpan.textContent = reel.likeCount; // 좋아요 수 표시

        // 토글 아이콘 추가
        const toggleIcon = document.createElement('img');
        toggleIcon.className = 'reels-toggle-icon';
        toggleIcon.src = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/steppers/default/24px.svg";

        // 토글 메뉴 추가
        const toggleMenu = document.createElement('div');
        toggleMenu.className = 'reels-toggle-menu reels-hidden';

        const reportButton = document.createElement('button');
        reportButton.className = 'reels-toggle-button';
        reportButton.innerText = '신고';

        const followButton = document.createElement('button');
        followButton.className = 'reels-toggle-button';
        followButton.innerText = '팔로우';

        toggleMenu.appendChild(reportButton);
        toggleMenu.appendChild(followButton);

        // 토글 아이콘 클릭 이벤트
        toggleIcon.addEventListener('click', () => {
          toggleMenu.classList.toggle('reels-hidden'); // 메뉴의 표시/숨기기 토글
        });

        // 각 요소를 오버레이와 릴스 아이템에 추가
        profileContainer.appendChild(profileImage);
        profileContainer.appendChild(nickname);
        overlay.appendChild(profileContainer);
        overlay.appendChild(content);
        overlay.appendChild(bookmarkIcon); // 북마크 버튼 추가
        // overlay.appendChild(likeIcon); // 좋아요 아이콘 추가
        // overlay.appendChild(likeCountSpan); // 좋아요 수 추가
        overlay.appendChild(toggleIcon); // 토글 아이콘 추가
        overlay.appendChild(toggleMenu); // 토글 메뉴 추가

        reelItem.appendChild(video);
        reelItem.appendChild(overlay);
        reelsContainer.appendChild(reelItem); // 완성된 릴스 항목을 컨테이너에 추가
      });

      loading = false; // 로딩 종료

      if (reelsList.length === 0) {
        window.removeEventListener('scroll', onScroll); // 더 이상 로드할 데이터가 없으면 스크롤 이벤트 제거
      }
    })
    .catch(error => {
      console.error('Error loading reels:', error);
      loading = false; // 에러 발생 시 로딩 종료
    });
  }

  // 북마크 상태를 초기화하는 함수
  async function initializeBookmarkState(postId, bookmarkIcon) {
    try {
      const response = await fetch(`/api/bookmarks/check?postId=${postId}`, {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('북마크 상태를 확인할 수 없습니다.');
      }

      const isBookmarked = await response.json(); // 북마크 상태를 받아옴

      // 북마크 상태에 따라 아이콘 설정
      bookmarkIcon.src = isBookmarked
          ? "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark_check/default/24px.svg"
          : "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark/default/24px.svg";

      // 북마크 상태 저장
      bookmarkIcon.dataset.bookmarked = isBookmarked ? "true" : "false";

      // 북마크가 존재할 경우 서버에서 북마크 ID 가져와 설정
      if (isBookmarked) {
        const bookmarkListResponse = await fetch(`/api/bookmarks/post/${postId}`, {
          method: 'GET',
          credentials: 'include'
        });

        if (bookmarkListResponse.ok) {
          const bookmarkList = await bookmarkListResponse.json();
          if (bookmarkList && bookmarkList.length > 0) {
            bookmarkIcon.dataset.bookmarkId = bookmarkList[0].bookmarkId; // 북마크 ID 설정
          }
        }
      }

    } catch (error) {
      console.error('북마크 상태 초기화 중 오류 발생:', error.message);
    }
  }

  // 북마크 상태를 토글하는 함수
  async function toggleBookmark(postId, bookmarkIcon) {
    if (isBookmarkProcessing) return; // 북마크 처리 중이면 중복 방지
    isBookmarkProcessing = true; // 북마크 처리 시작

    try {
      const isBookmarked = bookmarkIcon.dataset.bookmarked === "true";
      const bookmarkId = bookmarkIcon.dataset.bookmarkId;

      if (isBookmarked) {
        // 북마크 해제 요청
        const response = await fetch(`/api/bookmarks/delete/${bookmarkId}`, {
          method: 'POST',
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error('북마크 삭제 실패: ' + response.statusText);
        }

        // 북마크 해제 후 아이콘 및 데이터 갱신
        bookmarkIcon.src = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark/default/24px.svg";
        bookmarkIcon.dataset.bookmarked = "false";
        bookmarkIcon.dataset.bookmarkId = null; // 북마크 ID 초기화

      } else {
        // 북마크 추가 요청
        const response = await fetch(`/api/bookmarks/reg`, {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ postId: postId })
        });

        if (!response.ok) {
          throw new Error('북마크 추가 실패: ' + response.statusText);
        }

        // 북마크 추가 후 아이콘 및 데이터 갱신
        const newBookmarkData = await response.json();
        bookmarkIcon.src = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark_check/default/24px.svg";
        bookmarkIcon.dataset.bookmarked = "true";
        bookmarkIcon.dataset.bookmarkId = newBookmarkData.bookmarkId; // 새로운 북마크 ID 설정
      }
    } catch (error) {
      console.error('북마크 토글 중 오류 발생:', error.message);
    } finally {
      isBookmarkProcessing = false; // 북마크 처리 종료
    }
  }

  // 페이지 스크롤 시 추가 데이터를 로드하는 함수
  function onScroll() {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
      page++;
      loadReels(page); // 다음 페이지 로드
    }
  }

  window.addEventListener('scroll', onScroll); // 스크롤 이벤트 핸들러 추가
  loadReels(page); // 초기 페이지 로드
});
