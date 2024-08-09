let currentPostId;
let currentImageIndex = 0;
let editImageIndex = 0;
let imageUrls = [];

// 게시물 데이터 로드
async function loadPostData(postId) {
  console.log(`게시물 데이터 로드 시작: ${postId}`);
  try {
    const response = await fetch(`/api/posts/detail/${postId}`);
    if (!response.ok) {
      console.error('서버 응답 상태:', response.status);  // 응답 상태 출력
      throw new Error('Network response was not ok ' + response.statusText);
    }

    const responseText = await response.text();
    console.log('서버 응답 텍스트:', responseText);

    const postData = JSON.parse(responseText);
    currentPostId = postId;

    // 게시물 내용 설정
    document.getElementById('postContent').innerText = postData.data.content;

    // 이미지 URL 업데이트
    imageUrls = postData.data.imageUrls;

    // 이미지 컨테이너 초기화 및 이미지 추가
    const imageContainer = document.getElementById('imageContainer');
    imageContainer.innerHTML = ''; // 기존 이미지 제거

    imageUrls.forEach((url, index) => {
      const img = document.createElement('img');
      img.src = url;
      img.alt = `image ${index + 1}`;
      img.className = index === 0 ? 'active' : ''; // 첫 번째 이미지를 활성화
      img.onerror = () => {
        console.error('이미지 로드 실패:', url);
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

  } catch (error) {
    console.error('게시물 데이터 로드 중 오류 발생:', error);
  }
}

// 모달 열기
function openModal() {
  console.log('모달 열기');
  document.getElementById('postModal').style.display = 'flex';
}

// 모달 닫기
function closeModal() {
  console.log('모달 닫기');
  document.getElementById('postModal').style.display = 'none';
}

// 드롭다운 메뉴 토글
function toggleDropdownMenu() {
  console.log('드롭다운 메뉴 토글');
  const dropdownMenu = document.getElementById('dropdownMenu');
  dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none'
      : 'block';
}

// 수정 모달 열기
function openEditModal() {
  console.log('수정 모달 열기');
  document.getElementById('editModal').style.display = 'flex';
  document.getElementById('editContent').value = document.querySelector(
      '#postContent p').innerText;

  const editImagePreviewContainer = document.getElementById(
      'editImagePreviewContainer');
  editImagePreviewContainer.innerHTML = '';
  imageUrls.forEach((url, index) => {
    const img = document.createElement('img');
    img.src = url;
    img.alt = 'Preview Image';
    img.className = index === 0 ? 'active' : '';
    img.onerror = () => {
      console.error('이미지 로드 실패:', url);
      img.parentElement.removeChild(img);
    };
    editImagePreviewContainer.appendChild(img);
  });

  if (imageUrls.length > 1) {
    document.getElementById('prevEditButton').style.display = 'block';
    document.getElementById('nextEditButton').style.display = 'block';
  } else {
    document.getElementById('prevEditButton').style.display = 'none';
    document.getElementById('nextEditButton').style.display = 'none';
  }

  toggleDropdownMenu();
}

// 수정 모달 닫기
function closeEditModal() {
  console.log('수정 모달 닫기');
  document.getElementById('editModal').style.display = 'none';
}

// 게시물 수정
async function submitEdit() {
  console.log('게시물 수정 시작');
  const editedContent = document.getElementById('editContent').value;

  const editRequest = new FormData();
  editRequest.append('content', editedContent);

  try {
    const response = await fetch(`/api/posts/edit/${currentPostId}`, {
      method: 'POST',
      body: editRequest,
    });
    if (!response.ok) {
      throw new Error('Failed to update post');
    }
    console.log('게시물 수정 완료');
    alert('게시물이 수정되었습니다.');
    closeEditModal();
    loadPostData(currentPostId);
  } catch (error) {
    console.error('게시물 수정 중 오류 발생:', error);
    alert('게시물 수정에 실패했습니다.');
  }
}

// 게시물 삭제
async function deletePost() {
  console.log('게시물 삭제 시작');
  if (currentPostId) {
    try {
      const response = await fetch(`/api/posts/delete/${currentPostId}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('Failed to delete post');
      }
      console.log('게시물 삭제 완료');
      alert('게시물이 삭제되었습니다.');
      window.location.href = '/';
    } catch (error) {
      console.error('게시물 삭제 중 오류 발생:', error);
      alert('게시물 삭제에 실패했습니다.');
    }
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }
}

// 활성화된 이미지 표시
function showImage(index) {
  console.log(`이미지 표시: ${index}`);
  const images = document.querySelectorAll('#imageContainer img');
  images.forEach((img, idx) => {
    img.className = (idx === index) ? 'active' : '';
  });
}

// 이전 이미지 표시
function prevImage(event) {
  event.preventDefault();
  console.log('이전 이미지 표시');
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1
        : currentImageIndex - 1;
    showImage(currentImageIndex);
  }
}

// 다음 이미지 표시
function nextImage(event) {
  event.preventDefault();
  console.log('다음 이미지 표시');
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0
        : currentImageIndex + 1;
    showImage(currentImageIndex);
  }
}

// 활성화된 수정 이미지 표시
function showEditImage(index) {
  console.log(`수정 이미지 표시: ${index}`);
  const images = document.querySelectorAll('#editImagePreviewContainer img');
  images.forEach((img, idx) => {
    img.className = (idx === index) ? 'active' : '';
  });
}

// 이전 수정 이미지 표시
function prevEditImage(event) {
  event.preventDefault();
  console.log('이전 수정 이미지 표시');
  if (imageUrls.length > 1) {
    editImageIndex = (editImageIndex === 0) ? imageUrls.length - 1
        : editImageIndex - 1;
    showEditImage(editImageIndex);
  }
}

// 다음 수정 이미지 표시
function nextEditImage(event) {
  event.preventDefault();
  console.log('다음 수정 이미지 표시');
  if (imageUrls.length > 1) {
    editImageIndex = (editImageIndex === imageUrls.length - 1) ? 0
        : editImageIndex + 1;
    showEditImage(editImageIndex);
  }
}

// URL에서 postId를 추출하여 로드
document.addEventListener('DOMContentLoaded', () => {
  console.log('페이지 로드 완료');
  const urlParams = new URLSearchParams(window.location.search);
  const postId = urlParams.get('postId');
  if (postId) {
    console.log(`게시물 ID: ${postId}`);
    loadPostData(postId);
    openModal();
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }
});
