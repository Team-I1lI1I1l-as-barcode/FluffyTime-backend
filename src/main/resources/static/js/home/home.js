document.addEventListener('DOMContentLoaded', function () {
  const postsContainer = document.getElementById('posts-container');
  const loading = document.getElementById('loading');
  let currentPage = 1;
  const itemsPerPage = 5;
  let isLoading = false;

  async function fetchPosts(page = 1) {
    console.log('fetchPosts 시작 ' + currentPage);
    try {
      const response = await fetch(
          `/api/explore?page=${page}&perPage=${itemsPerPage}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json'
            }
          });

      const data = await response.json();

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      // 데이터가 배열인지 확인
      if (data && Array.isArray(data.list)) {
        return data.list || [];
      } else {
        console.error('Expected an array but got:', data);
        return [];
      }
    } catch (error) {
      console.error('Fetch error:', error);
      return [];
    }
  }

  async function renderPosts(posts) {
    if (!Array.isArray(posts)) {
      console.error('Expected an array for posts but got:', posts);
      return;
    }

    for (const post of posts) {
      const postElement = document.createElement('div');
      postElement.classList.add('home-post');

      const fileExtension = post.imageUrl.split('.').pop().toLowerCase();

      // 파일 확장자에 따라 img 또는 video 요소를 생성
      if (fileExtension === 'mp4' || fileExtension === 'mov' || fileExtension
          === 'webm') {
        postElement.innerHTML = `
      <div class="home-post-header">
        <img src="${post.profileImageUrl
        || '/image/profile/profile.png'}" alt="${post.nickname}">
        <strong>${post.nickname || 'Anonymous'}</strong>
      </div>
      <div class="home-post-content">
       <video controls autoplay muted playsinline class="active" src="${post.imageUrl
        || 'https://via.placeholder.com/600x400'}" alt="Post Image">

      </div>
      <div class="home-post-footer">
        <div id="comment-list-${post.postId}">Loading comments...</div>
      </div>
    `;
      } else {
        postElement.innerHTML = `
      <div class="home-post-header">
        <img src="${post.profileImageUrl
        || '/image/profile/profile.png'}" alt="${post.nickname}">
        <strong>${post.nickname || 'Anonymous'}</strong>
      </div>
      <div class="home-post-content">
        <img src="${post.imageUrl || '/image/profile/profile.png'}" alt="Post Image">
        <p>${highlightMentions(post.content || '')}</p>
      </div>
      <div class="home-post-footer">
        <div id="comment-list-${post.postId}">Loading comments...</div>
      </div>
    `;
      }

      //프로필 클릭 시 유저 페이지로 이동
      const headerElement = postElement.querySelector('.home-post-header');
      headerElement.addEventListener('click', () => {
        const nickname = post.nickname;
        window.location.href = `/userpages/${nickname}`;
      })

      //게시글 클릭 시 상세 페이지로 이동
      const contentElement = postElement.querySelector('.home-post-content')
      contentElement.addEventListener('click', () => {
        window.location.href = `/posts/detail/${post.postId}`;
      })

      postsContainer.appendChild(postElement);

      // 댓글을 가져와서 해당 게시글 아래에 표시
      try {
        const commentList = postElement.querySelector(
            `#comment-list-${post.postId}`);
        const comments = await fetchComments(post.postId);
        commentList.innerHTML = '';

        //최대 2개의 댓글만 표시
        const maxComments = comments.slice(0, 2);

        if (maxComments.length > 0) {
          maxComments.forEach(comment => {
            const commentElement = document.createElement('div');
            commentElement.className = 'comment-content';

            const nicknameElement = document.createElement('span');
            nicknameElement.className = 'comment-nickname';
            nicknameElement.textContent = `${comment.nickname} `;

            const contentElement = document.createElement('span');
            contentElement.className = 'comment-text';
            contentElement.innerHTML = highlightMentions(comment.content);

            commentElement.appendChild(nicknameElement);
            commentElement.appendChild(contentElement);
            commentList.appendChild(commentElement);
          });
        } else {
          commentList.innerHTML = '<p>-</p>';
        }
      } catch (error) {
        console.error('Error fetching comments:', error);
        const commentList = postElement.querySelector(
            `#comment-list-${post.postId}`);
        if (commentList) {
          commentList.innerHTML = '<p>Failed to load comments.</p>';
        }
      }
    }
  }

  //댓글 조회
  async function fetchComments(postId) {
    try {
      const response = await fetch(`/api/comments/post/${postId}`);
      if (!response.ok) {
        console.error('댓글 목록 가져오기 실패!', response.status);
        return [];
      }
      const comments = await response.json();
      if (!Array.isArray(comments)) {
        console.error('댓글 데이터가 배열이 아닙니다.', comments);
        return [];
      }
      return comments;
    } catch (error) {
      console.error('댓글 목록 가져오기 중 예외 발생!', error);
      return [];
    }
  }

  function loadMorePosts() {
    if (isLoading) {
      return;
    }
    isLoading = true;
    loading.style.display = 'block';

    fetchPosts(currentPage).then(posts => {
      if (posts.length > 0) {
        console.log(posts)
        renderPosts(posts);
        currentPage++;
        console.log(posts.length);
        console.log(currentPage);
      } else {
        // 사용자에게 더 이상 게시물이 없음을 알리는 메시지 표시
        if (!document.querySelector('.no-more-posts-message')) {
          const noMorePostsMessage = document.createElement('div');
          noMorePostsMessage.className = 'no-more-posts-message';
          noMorePostsMessage.textContent = '게시글을 모두 확인했습니다';
          postsContainer.appendChild(noMorePostsMessage);
        }
      }
      isLoading = false;
      loading.style.display = 'none';
    }).catch(() => {
      isLoading = false;
      loading.style.display = 'none';
    });
  }

  // 초기 게시글 로드
  loadMorePosts();

  // 디바운스 함수 정의
  function debounce(func, wait) {
    let timeout;
    return function (...args) {
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(this, args), wait);
    };
  }

  // 디바운스를 적용한 스크롤 이벤트 핸들러
  const handleScroll = debounce(() => {
    if (window.innerHeight + window.scrollY
        >= document.documentElement.scrollHeight - 100) {
      loadMorePosts();
    }
  }, 200);

  // 스크롤 이벤트 처리
  window.addEventListener('scroll', handleScroll);
});

// 멘션 하이라이트 함수
function highlightMentions(content) {
  // '@nickname' 패턴을 찾아서 <span> 태그로 감싸기
  return content.replace(/(@\w+)/g,
      '<span style="color: #0078e8; font-weight: 500;">$1</span>');
}