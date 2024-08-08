let currentPostId;
let currentImageIndex = 0;
let editImageIndex = 0;
let imageUrls = [];

// JWT 토큰을 가져오는 함수
function getJwtToken() {
  return localStorage.getItem('jwtToken'); // 로컬 스토리지에서 토큰을 가져옴
}

// 게시물 데이터 로드
async function loadPostData(postId) {
  try {
    const token = getJwtToken();
    const response = await fetch(`/api/posts/detail/${postId}`, {
      headers: {
        'Authorization': 'Bearer ' + token
      }
    });
    if (!response.ok) {
      throw new Error('Network response was not ok ' + response.statusText);
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
      const postData = await response.json();
      currentPostId = postId;

      const postContent = document.getElementById('postContent');
      postContent.innerHTML = `<p>${postData.content}</p>`; // 게시물 내용 설정

      const imageContainer = document.getElementById('imageContainer');
      imageContainer.innerHTML = ''; // 기존 이미지 초기화
      if (Array.isArray(postData.imageUrls)) {
        imageUrls = postData.imageUrls.filter(
            url => url.startsWith("http://") || url.startsWith("https://"));
        imageUrls.forEach((url, index) => {
          const img = document.createElement('img');
          img.src = url;
          img.alt = `image ${index + 1}`;
          img.className = index === 0 ? 'active' : ''; // 첫 번째 이미지 활성화
          img.onerror = () => {
            console.error('Failed to load image:', url);
            img.parentElement.removeChild(img); // 이미지 로드 실패 시 요소 제거
          };
          imageContainer.appendChild(img);
        });

        // 이미지가 한 장일 경우 슬라이드 버튼 숨기기
        if (imageUrls.length > 1) {
          document.getElementById('prevButton').style.display = 'block';
          document.getElementById('nextButton').style.display = 'block';
        } else {
          document.getElementById('prevButton').style.display = 'none';
          document.getElementById('nextButton').style.display = 'none';
        }
      } else {
        console.error('imageUrls is not an array', postData.imageUrls);
      }
    } else {
      throw new Error("Received content is not JSON");
    }
  } catch (error) {
    console.error('Error fetching post data:', error);
  }
}

// 모달 열기
function openModal() {
  document.getElementById('postModal').style.display = 'flex';
}

// 모달 닫기
function closeModal() {
  document.getElementById('postModal').style.display = 'none';
}

// 드롭다운 메뉴 토글
function toggleDropdownMenu() {
  const dropdownMenu = document.getElementById('dropdownMenu');
  dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none'
      : 'block';
}

// 수정 모달 열기
function openEditModal() {
  document.getElementById('editModal').style.display = 'flex';
  document.getElementById('editContent').value = document.querySelector(
      '#postContent p').innerText; // 수정 내용 설정

  const editImagePreviewContainer = document.getElementById(
      'editImagePreviewContainer');
  editImagePreviewContainer.innerHTML = ''; // 기존 이미지 초기화
  imageUrls.forEach((url, index) => {
    const img = document.createElement('img');
    img.src = url;
    img.alt = 'Preview Image';
    img.className = index === 0 ? 'active' : ''; // 첫 번째 이미지 활성화
    img.onerror = () => {
      console.error('Failed to load image:', url);
      img.parentElement.removeChild(img); // 이미지 로드 실패 시 요소 제거
    };
    editImagePreviewContainer.appendChild(img);
  });

  // 이미지가 한 장일 경우 슬라이드 버튼 숨기기
  if (imageUrls.length > 1) {
    document.getElementById('prevEditButton').style.display = 'block';
    document.getElementById('nextEditButton').style.display = 'block';
  } else {
    document.getElementById('prevEditButton').style.display = 'none';
    document.getElementById('nextEditButton').style.display = 'none';
  }

  toggleDropdownMenu(); // 드롭다운 메뉴 닫기
}

// 수정 모달 닫기
function closeEditModal() {
  document.getElementById('editModal').style.display = 'none';
}

// 게시물 수정
async function submitEdit() {
  const editedContent = document.getElementById('editContent').value;

  const editRequest = new FormData();
  editRequest.append('content', editedContent); // 수정 내용 추가

  try {
    const token = getJwtToken();
    const response = await fetch(`/api/posts/edit/${currentPostId}`, {
      method: 'POST', // PATCH에서 POST로 변경
      body: editRequest,
      headers: {
        'Authorization': 'Bearer ' + token
      }
    });
    if (!response.ok) {
      throw new Error('Failed to update post');
    }
    alert('게시물이 수정되었습니다.');
    closeEditModal(); // 수정 모달 닫기
    loadPostData(currentPostId); // 수정된 게시물 데이터 로드
  } catch (error) {
    console.error('Error updating post:', error);
    alert('게시물 수정에 실패했습니다.');
  }
}

// 게시물 삭제
async function deletePost() {
  if (currentPostId) {
    try {
      const token = getJwtToken();
      const response = await fetch(`/api/posts/delete/${currentPostId}`, {
        method: 'POST', // DELETE에서 POST로 변경
        headers: {
          'Authorization': 'Bearer ' + token
        }
      });
      if (!response.ok) {
        throw new Error('Failed to delete post');
      }
      alert('게시물이 삭제되었습니다.');
      window.location.href = '/'; // 게시물 삭제 후 메인 페이지로 리다이렉트
    } catch (error) {
      console.error('Error deleting post:', error);
      alert('게시물 삭제에 실패했습니다.');
    }
  } else {
    console.error('No postId found in URL');
  }
}

// 활성화된 이미지 표시
function showImage(index) {
  const images = document.querySelectorAll('#imageContainer img');
  images.forEach((img, idx) => {
    img.className = (idx === index) ? 'active' : '';
  });
}

// 이전 이미지 표시
function prevImage(event) {
  event.preventDefault();
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1
        : currentImageIndex - 1;
    showImage(currentImageIndex);
  }
}

// 다음 이미지 표시
function nextImage(event) {
  event.preventDefault();
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0
        : currentImageIndex + 1;
    showImage(currentImageIndex);
  }
}

// 활성화된 수정 이미지 표시
function showEditImage(index) {
  const images = document.querySelectorAll('#editImagePreviewContainer img');
  images.forEach((img, idx) => {
    img.className = (idx === index) ? 'active' : '';
  });
}

// 이전 수정 이미지 표시
function prevEditImage(event) {
  event.preventDefault();
  if (imageUrls.length > 1) {
    editImageIndex = (editImageIndex === 0) ? imageUrls.length - 1
        : editImageIndex - 1;
    showEditImage(editImageIndex);
  }
}

// 다음 수정 이미지 표시
function nextEditImage(event) {
  event.preventDefault();
  if (imageUrls.length > 1) {
    editImageIndex = (editImageIndex === imageUrls.length - 1) ? 0
        : editImageIndex + 1;
    showEditImage(editImageIndex);
  }
}

// URL에서 postId를 추출하여 로드
document.addEventListener('DOMContentLoaded', () => {
  const urlParams = new URLSearchParams(window.location.search);
  const postId = urlParams.get('postId');
  if (postId) {
    loadPostData(postId); // 게시물 데이터 로드
    openModal(); // 모달 열기
  } else {
    console.error('No postId found in URL');
  }
});
