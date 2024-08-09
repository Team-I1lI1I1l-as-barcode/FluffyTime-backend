document.addEventListener('DOMContentLoaded', function () {
  const postsContainer = document.getElementById('posts-container');
  const loading = document.getElementById('loading');
  let page = 1;
  let isLoading = false;

  function fetchPosts(page) {
    // 더미 데이터를 생성합니다. 실제로는 API 호출을 사용합니다.
    return new Promise((resolve) => {
      setTimeout(() => {
        const posts = Array.from({length: 5}, (_, i) => ({
          id: (page - 1) * 5 + i + 1,
          userName: `User ${((page - 1) * 5 + i + 1)}`,
          userImage: 'https://via.placeholder.com/40',
          postImage: 'https://via.placeholder.com/600x400',
          postContent: `This is a post content #${((page - 1) * 5 + i + 1)}`,
          comments: [
            `Comment 1 for post #${((page - 1) * 5 + i + 1)}`,
            `Comment 2 for post #${((page - 1) * 5 + i + 1)}`,
          ]
        }));
        resolve(posts);
      }, 1000); // 1초 지연
    });
  }

  function renderPosts(posts) {
    posts.forEach(post => {
      const postElement = document.createElement('div');
      postElement.classList.add('post');

      postElement.innerHTML = `
                <div class="post-header">
                    <img src="${post.userImage}" alt="${post.userName}">
                    <strong>${post.userName}</strong>
                </div>
                <div class="post-content">
                    <img src="${post.postImage}" alt="Post Image">
                    <p>${post.postContent}</p>
                </div>
                <div class="post-footer">
                    ${post.comments.map(comment => `<p>${comment}</p>`).join(
          '')}
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

    fetchPosts(page).then(posts => {
      renderPosts(posts);
      page += 1;
      isLoading = false;
      loading.style.display = 'none';
    });
  }

  // 초기 게시글 로드
  loadMorePosts();

  // 스크롤 이벤트 처리
  window.addEventListener('scroll', () => {
    if (window.innerHeight + window.scrollY
        >= document.documentElement.scrollHeight - 100) {
      loadMorePosts();
    }
  });
});
