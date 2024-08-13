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
        return []; // 배열이 아닌 경우 빈 배열 반환
      }
    } catch (error) {
      console.error('Fetch error:', error);
      return []; // 에러 발생 시 빈 배열 반환
    }
  }

  function renderPosts(posts) {
    if (!Array.isArray(posts)) {
      console.error('Expected an array for posts but got:', posts);
      return;
    }

    posts.forEach(post => {
      const postElement = document.createElement('div');
      postElement.classList.add('home-post');

      postElement.innerHTML = `
        <div class="home-post-header">
          <img src="${post.userImage
      || 'https://via.placeholder.com/40'}" alt="${post.userName || 'User'}">
          <strong>${post.userName || 'Anonymous'}</strong>
        </div>
        <div class="home-post-content">
          <img src="${post.imageUrl || 'https://via.placeholder.com/600x400'}" alt="Post Image">
          <p>${post.content || ''}</p>
        </div>
        <div class="home-post-footer">
          ${post.comments && Array.isArray(post.comments)
          ? post.comments.map(comment => `<p>${comment}</p>`).join('')
          : '<p>No comments available.</p>'}
        </div>
      `;

      postsContainer.appendChild(postElement);
    });
  }

  function loadMorePosts() {
    if (isLoading) {
      return;
    }
    isLoading = true;
    loading.style.display = 'block';

    fetchPosts(currentPage).then(posts => {
      if (posts.length > 0) {
        renderPosts(posts);
        currentPage++;
        console.log(posts.length);
        console.log(currentPage);
      } else {
        // 사용자에게 더 이상 게시물이 없음을 알리는 메시지 표시
        if (!document.querySelector('.no-more-posts-message')) {
          const noMorePostsMessage = document.createElement('div');
          noMorePostsMessage.className = 'no-more-posts-message';
          noMorePostsMessage.textContent = 'No more posts available.';
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
  }, 200); // 200ms 디바운스 대기 시간

  // 스크롤 이벤트 처리
  window.addEventListener('scroll', handleScroll);
});
