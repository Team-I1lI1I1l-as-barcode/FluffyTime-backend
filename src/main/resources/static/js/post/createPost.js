let currentImageIndex = 0;
let imagesArray = [];
let currentDraftPostId = null;

// 새 게시물 만들기 모달 열기
function openModal() {
  document.getElementById('postModal').style.display = 'flex';
}

// 새 게시물 만들기 모달 닫기
function closeModal() {
  document.getElementById('postModal').style.display = 'none';
}

function openDraftModal() {
  document.getElementById('draftModal').style.display = 'flex'; // 임시 저장 모달 열기
  loadDraft(); // 임시 저장 글 불러오기
}

function closeDraftModal() {
  document.getElementById('draftModal').style.display = 'none'; // 임시 저장 모달 닫기
}

function showComplete() {
  document.querySelector('.content').style.display = 'none'; // 게시물 등록 완료 메시지 표시
  document.getElementById('complete-container').style.display = 'block'; // 게시물 등록 완료 메시지 표시
}

function previewImages(event) {
  const files = event.target.files;
  const previewContainer = document.getElementById('imagePreviewContainer');
  const dragDropText = document.getElementById('dragDropText');
  const shareButton = document.getElementById('shareButton');
  const leftContent = document.getElementById('leftContent');

  previewContainer.innerHTML = '';
  imagesArray = [];

  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    event.target.value = ""; // 파일 선택 초기화
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader();
    reader.onload = function (e) {
      imagesArray.push(e.target.result);
      if (i === 0) { // 첫 번째 이미지를 미리 보기로 설정
        const img = document.createElement('img');
        img.src = e.target.result;
        img.classList.add('photo');
        img.style.objectFit = 'cover'; // 이미지 꽉 차게
        previewContainer.appendChild(img);
      }
    };
    reader.readAsDataURL(file);
  }

  if (files.length > 0) {
    dragDropText.style.display = 'none'; // 드래그 앤 드롭 텍스트 숨기기
    shareButton.style.display = 'none'; // 공유하기 버튼 숨기기
    leftContent.classList.add('fullscreen'); // 이미지를 전체 영역에 표시

    // 이미지가 한 장이면 슬라이드 버튼 숨기기
    if (files.length > 1) {
      document.getElementById('prevButton').style.display = 'block';
      document.getElementById('nextButton').style.display = 'block';
    } else {
      document.getElementById('prevButton').style.display = 'none';
      document.getElementById('nextButton').style.display = 'none';
    }
  }
}

function updateCharCount() {
  const content = document.getElementById('content').value;
  const charCount = document.getElementById('charCount');
  charCount.textContent = `${content.length} / 2200`; // 글자 수 업데이트
}

async function saveAsTemp(event) {
  event.preventDefault();

  const content = document.getElementById('content').value;
  const images = document.getElementById('images').files;

  if (!content) {
    alert('내용을 입력하세요.');
    return;
  }

  if (images.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    return;
  }

  const postRequest = {
    content: content,
    tempStatus: 'TEMP',
    imageUrls: []
  };

  const formData = new FormData();
  formData.append('post',
      new Blob([JSON.stringify(postRequest)], {type: 'application/json'}));
  for (let i = 0; i < images.length; i++) {
    formData.append('images', images[i]);
  }

  fetch('/api/posts/temp-reg', {
    method: 'POST',
    body: formData
  }).then(response => {
    if (response.ok) {
      alert('임시 저장되었습니다.');
      closeModal();
    } else {
      alert('임시 저장에 실패했습니다.');
    }
  }).catch(error => {
    alert('임시 저장 중 오류가 발생했습니다.');
    console.error('Error:', error);
  });
}

async function submitPost(event) {
  event.preventDefault();

  const content = document.getElementById('content').value;
  const images = document.getElementById('images').files;

  if (!content) {
    alert('내용을 입력하세요.');
    return;
  }

  if (images.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    return;
  }

  const postRequest = {
    tempId: currentDraftPostId, // 현재 임시 저장된 글 ID를 추가
    content: content,
    tempStatus: 'SAVE',
    imageUrls: []
  };

  const formData = new FormData();
  formData.append('post',
      new Blob([JSON.stringify(postRequest)], {type: 'application/json'}));
  for (let i = 0; i < images.length; i++) {
    formData.append('images', images[i]);
  }

  fetch('/api/posts/reg', {
    method: 'POST',
    body: formData
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      throw new Error('게시물 등록에 실패했습니다.');
    }
  }).then(postId => {
    // 게시물 등록 완료 후 3초 동안 완료 메시지 표시
    document.querySelector('.content').style.display = 'none';
    document.getElementById('complete-container').style.display = 'block';
    setTimeout(() => {
      document.getElementById('complete-container').style.display = 'none';
      window.location.href = `/html/post/postDetail.html?postId=${postId}`; // 게시물 상세 페이지로 이동
    }, 3000); // 3초 후에 완료 메시지를 숨기고 상세 페이지로 이동
  }).catch(error => {
    alert(error.message);
  });
}

async function loadDraft() {
  const userId = 1; // 나중에 수정
  const tempPostsContainer = document.getElementById('tempPostsContainer');
  tempPostsContainer.innerHTML = '';

  try {
    const response = await fetch(`/api/posts/temp-posts/list?userId=${userId}`);
    if (response.ok) {
      const tempPosts = await response.json();
      tempPosts.forEach(post => {
        const postElement = document.createElement('div');
        postElement.classList.add('temp-post');

        const postContent = post.content.length > 40 ? post.content.substring(0,
            40) + '...' : post.content;
        const postDate = new Date(post.createdAt).toLocaleDateString();

        postElement.innerHTML = `
              <div class="post-details">
                <p class="post-content">${postContent}</p>
                <span class="post-date">${postDate}</span>
                <span class="delete-link" onclick="deleteTempPost(${post.postId}, event)">삭제</span>
              </div>
            `;
        postElement.onclick = () => continueDraft(post); // 임시 저장 글 불러오기
        tempPostsContainer.appendChild(postElement);
      });
      tempPostsContainer.style.display = 'flex';
    } else {
      alert('임시 저장 글 불러오기에 실패했습니다.');
    }
  } catch (error) {
    alert('임시 저장 글 불러오기 중 오류가 발생했습니다.');
    console.error('Error:', error);
  }
}

function continueDraft(post) {
  document.getElementById('content').value = post.content; // 임시 저장 글 내용 불러오기
  updateCharCount();
  currentDraftPostId = post.postId;

  const previewContainer = document.getElementById('imagePreviewContainer');
  previewContainer.innerHTML = '';
  imagesArray = post.imageUrls;
  currentImageIndex = 0;
  displayImages();

  closeDraftModal();
  openModal();
}

function displayImages() {
  const previewContainer = document.getElementById('imagePreviewContainer');
  previewContainer.innerHTML = '';

  if (imagesArray.length > 0) {
    const img = document.createElement('img');
    img.src = imagesArray[currentImageIndex];
    img.classList.add('photo');
    img.style.objectFit = 'cover'; // 이미지 꽉 차게
    previewContainer.appendChild(img);

    if (imagesArray.length > 1) {
      document.getElementById('prevButton').style.display = 'block';
      document.getElementById('nextButton').style.display = 'block';
    } else {
      document.getElementById('prevButton').style.display = 'none';
      document.getElementById('nextButton').style.display = 'none';
    }
  }
}

function deleteImage(event, index) {
  event.stopPropagation();
  imagesArray.splice(index, 1); // 이미지 삭제
  displayImages();
}

async function deleteTempPost(postId, event) {
  event.stopPropagation();
  try {
    const response = await fetch(`/api/posts/temp-delete/${postId}`, {
      method: 'POST' // DELETE에서 POST로 변경
    });
    if (response.ok) {
      loadDraft(); // 임시 저장 글 목록 다시 불러오기
    } else {
      alert('임시 저장 글 삭제에 실패했습니다.');
    }
  } catch (error) {
    alert('임시 저장 글 삭제 중 오류가 발생했습니다.');
    console.error('Error:', error);
  }
}

function prevImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imagesArray.length - 1
        : currentImageIndex - 1;
    displayImages(); // 이전 이미지 표시
  }
}

function nextImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === imagesArray.length - 1) ? 0
        : currentImageIndex + 1;
    displayImages(); // 다음 이미지 표시
  }
}
