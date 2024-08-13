let currentPostId; // 현재 게시물의 ID를 저장하는 변수
let currentImageIndex = 0;
let imageUrls = [];

// 게시물 데이터 로드
async function loadPostData(postId) {
  console.log(`게시물 데이터 로드 시작: ${postId}`);
  try {
    // 서버에서 게시물 데이터를 불러옴
    const response = await fetch(`/api/posts/detail/${postId}`);
    if (!response.ok) {
      console.error('서버 응답 상태:', response.status);
      throw new Error('서버 응답이 올바르지 않습니다: ' + response.statusText);
    }
    console.log('응답 ok!')

    const postData = await response.json();
    console.log('콘솔 불러와주세요~');
    currentPostId = postId;

    // 게시물 내용 설정
    document.getElementById('postContent').innerText = postData.data.content;

    // 이미지 URL 업데이트

    imageUrls = postData.data.imageUrls; // 이미지 URL 배열을 업데이트
    console.log(imageUrls);

    // 이미지 컨테이너 초기화 및 이미지 추가
    updateImageContainer('imageContainer', imageUrls);

    // 이미지가 한 장일 경우 슬라이드 버튼 숨기기
    toggleSlideButtons(imageUrls.length, 'prevButton', 'nextButton');

  } catch (error) {
    console.error('게시물 데이터 로드 중 오류 발생:', error);
  }
}

// 이미지 컨테이너 업데이트 함수
function updateImageContainer(containerId, urls) {
  const container = document.getElementById(containerId);
  container.innerHTML = ''; // 컨테이너를 초기화

  urls.forEach((imageObj, index) => {
    const img = document.createElement('img');
    img.src = imageObj.filepath; // 이미지 경로를 설정
    img.alt = `image ${index + 1}`;
    img.style.display = index === 0 ? 'block' : 'none'; // 첫 번째 이미지만 처음에 표시
    img.className = index === 0 ? 'active' : ''; // 첫 번째 이미지만 처음에 표시
    img.style.width = '100%'; // 이미지가 꽉 차도록 설정
    img.style.height = '100%'; // 이미지가 꽉 차도록 설정
    img.style.objectFit = 'cover'; // 이미지를 컨테이너에 맞게 자르도록 설정
    img.onerror = () => {
      console.error('이미지 로드 실패:', imageObj.filepath);
      img.parentElement.removeChild(img); // 이미지 로드 실패 시 요소를 제거합니다.
    };
    container.appendChild(img); // 이미지 요소를 컨테이너에 추가합니다.
  });
}

// 현재 인덱스에 해당하는 이미지를 표시하는 함수
function showImage(index, containerId) {
  console.log(`이미지 표시: ${index}`);
  const images = document.querySelectorAll(`#${containerId} img`);
  images.forEach((img, idx) => {
    img.style.display = idx === index ? 'block' : 'none'; // 현재 인덱스의 이미지만 표시
    img.className = idx === index ? 'active' : ''; // 활성화된 이미지에 클래스 추가
  });
}

// 이전 이미지를 표시하는 함수
function prevImage(event) {
  event.preventDefault();
  console.log('이전 이미지 표시');
  if (imageUrls.length > 1) {
    // 현재 인덱스가 0이면 마지막 이미지로, 아니면 이전 이미지로 이동
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1
        : currentImageIndex - 1;
    showImage(currentImageIndex, 'imageContainer');
  }
}

// 다음 이미지 표시
function nextImage(event) {
  event.preventDefault();
  console.log('다음 이미지 표시');
  if (imageUrls.length > 1) {
    // 현재 인덱스가 마지막이면 첫 번째 이미지로, 아니면 다음 이미지로 이동
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0
        : currentImageIndex + 1;
    showImage(currentImageIndex, 'imageContainer');
  }
}

// 슬라이드 버튼 표시/숨기기 함수
function toggleSlideButtons(length, prevBtnId, nextBtnId) {
  const displayValue = length > 1 ? 'block' : 'none';
  document.getElementById(prevBtnId).style.display = displayValue;
  document.getElementById(nextBtnId).style.display = displayValue;
}

// 수정 모달 열기
function openEditModal() {
  console.log('수정 모달 열기');
  document.getElementById('editModal').style.display = 'flex';
  document.getElementById('editContent').value = document.getElementById(
      'postContent').innerText;

  // 이미지 인덱스 초기화
  currentImageIndex = 0;

  // 이미지 컨테이너 업데이트
  updateImageContainer('editImagePreviewContainer', imageUrls);

  // 슬라이드 버튼 토글
  toggleSlideButtons(imageUrls.length, 'prevEditButton', 'nextEditButton');

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

  const editRequest = {
    content: editedContent
  };

  try {
    const response = await fetch(`/api/posts/edit/${currentPostId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(editRequest)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || '게시물 수정 실패');
    }

    console.log('게시물 수정 완료');

    // 화면 업데이트
    document.getElementById('postContent').innerText = editedContent;

    alert('게시물이 수정되었습니다.');
    closeEditModal();
  } catch (error) {
    console.error('게시물 수정 중 오류 발생:', error);
    alert(error.message || '게시물 수정에 실패했습니다.');
  }
}

// 게시물 삭제
async function deletePost() {
  console.log('게시물 삭제 시작');
  if (currentPostId) {
    try {
      const response = await fetch(`/api/posts/delete/${currentPostId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || '게시물 삭제 실패');
      }

      console.log('게시물 삭제 완료');
      alert('게시물이 삭제되었습니다.');
      window.location.href = '/';
    } catch (error) {
      console.error('게시물 삭제 중 오류 발생:', error);
      alert(error.message || '게시물 삭제에 실패했습니다.');
    }
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }
}

// 이전 수정 이미지 표시
function prevEditImage(event) {
  event.preventDefault();
  console.log('이전 수정 이미지 표시');
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1
        : currentImageIndex - 1;
    showImage(currentImageIndex, 'editImagePreviewContainer');
  }
}

// 다음 수정 이미지 표시
function nextEditImage(event) {
  event.preventDefault();
  console.log('다음 수정 이미지 표시');
  if (imageUrls.length > 1) {
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0
        : currentImageIndex + 1;
    showImage(currentImageIndex, 'editImagePreviewContainer');
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

// URL에서 postId를 추출하여 로드
document.addEventListener('DOMContentLoaded', () => {
  console.log('페이지 로드 완료');

  // 현재 URL의 경로를 가져옴
  const path = window.location.pathname;

  // 경로를 '/'로 분리하여 배열로 만듬
  const pathSegments = path.split('/');

  // 배열의 마지막 요소가 postId
  const postId = pathSegments[pathSegments.length - 1];

  if (postId) {
    loadPostData(postId);
    openModal();

    //댓글/답글
    const commentButton = document.getElementById('commentButton');
    commentButton.onclick = function () {
      postComment(postId);
    }
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }
});

