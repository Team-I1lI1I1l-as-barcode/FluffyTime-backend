document.addEventListener('DOMContentLoaded', function() {
  let page = 1; // 현재 페이지 번호
  let loading = false; // 데이터를 로딩 중인지 여부
  let isBookmarkProcessing = false; // 북마크 요청이 처리 중인지 여부
  let hasMoreData = true; // 데이터가 더 있는지 여부

  const reelsContainer = document.getElementById('reels-list'); // 릴스 항목을 담을 컨테이너

// 릴스 데이터를 불러오는 함수
  function loadReels(page) {
    if (loading || !hasMoreData) return; // 이미 로딩 중이거나 더 이상 데이터가 없으면 중지

    loading = true; // 로딩 시작
    fetch(`/api/reels?page=${page}`) // 페이지 단위로 데이터를 불러옴
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok ' + response.statusText);
      }
      return response.json(); // 응답 데이터를 JSON으로 파싱
    })
    .then(reelsList => {
      console.log("서버에서 받은 데이터: ", reelsList);

      if (!reelsList || reelsList.length === 0) {
        console.warn("더 이상 불러올 데이터가 없습니다.");
        hasMoreData = false; // 데이터가 없으면 더 이상 로드하지 않음
        loading = false;
        return; // 데이터가 없으면 로딩 종료
      }

      // reelsId를 기준으로 최신 항목이 위로 오도록 내림차순 정렬
      reelsList.sort((a, b) => b.reelsId - a.reelsId);

      // 정렬된 데이터를 순차적으로 렌더링
      (async function renderReels() {
        for (const reel of reelsList) {
          await renderReel(reel); // 각 항목을 순차적으로 렌더링
        }
        loading = false; // 로딩 완료
      })();
    })
    .catch(error => {
      console.error('Error loading reels:', error);
      loading = false; // 에러 발생 시 로딩 종료
    });
  }

// 릴스 항목을 렌더링하는 함수
  async function renderReel(reel) {
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

    profileContainer.appendChild(profileImage);
    profileContainer.appendChild(nickname);
    overlay.appendChild(profileContainer);
    overlay.appendChild(content);
    overlay.appendChild(bookmarkIcon);

    // 좋아요 버튼 생성
    const likeButton = document.createElement('button');
    likeButton.className = 'like-button';

    // 좋아요 상태 초기화
    await initializeLikeStatus(reel.postId, likeButton);

    // 좋아요 버튼 클릭 이벤트 처리
    likeButton.addEventListener('click', () => {
      toggleLikePost(reel.postId, likeButton);
    });

    // 좋아요 수 표시
    const likeCountSpan = document.createElement('span');
    likeCountSpan.className = 'like-count';
    likeCountSpan.textContent = reel.likeCount;

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

    overlay.appendChild(likeButton); // 좋아요 버튼 추가
    overlay.appendChild(likeCountSpan); // 좋아요 수 추가
    overlay.appendChild(toggleIcon); // 토글 아이콘 추가
    overlay.appendChild(toggleMenu); // 토글 메뉴 추가
    reelItem.appendChild(video);
    reelItem.appendChild(overlay);
    reelsContainer.appendChild(reelItem); // 완성된 릴스 항목을 컨테이너에 추가
  }

// 북마크 상태를 업데이트하는 함수
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

// 좋아요 상태
  async function initializeLikeStatus(postId, likeButton) {
    try {
      const response = await fetch(`/api/posts/detail/${postId}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data) {
        likeButton.innerHTML = data.liked
            ? '<span class="material-icons">favorite</span>'
            : '<span class="material-icons">favorite_border</span>'; // 아이콘 모양 업데이트
        likeButton.classList.toggle('liked', data.liked);
      }
    } catch (error) {
      console.error('Error initializing like status:', error);
    }
  }

//좋아요 등록/취소
  async function toggleLikePost(postId, likeButton) {
    try {
      const isLiked = likeButton.classList.contains('liked');
      const method = isLiked ? 'DELETE' : 'POST';
      const response = await fetch(`/api/likes/post/${postId}`, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data) {
        // 아이콘 모양 업데이트
        likeButton.innerHTML = data.liked
            ? '<span class="material-icons">favorite</span>'
            : '<span class="material-icons">favorite_border</span>';
        likeButton.classList.toggle('liked', data.liked);

        // 좋아요 개수 업데이트
        const likeCountSpan = likeButton.nextElementSibling;
        let currentLikeCount = parseInt(likeCountSpan.textContent, 10);

        if (data.liked) {
          likeCountSpan.textContent = currentLikeCount + 1; // 좋아요 증가
        } else {
          likeCountSpan.textContent = currentLikeCount - 1; // 좋아요 감소
        }
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }

// 스크롤 이벤트 처리
  function onScroll() {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
      page++; // 페이지 번호 증가
      loadReels(page); // 다음 페이지 로드
    }
  }

  window.addEventListener('scroll', onScroll); // 스크롤 이벤트 핸들러 추가
  loadReels(page); // 첫 페이지 로드
});

