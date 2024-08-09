let currentImageIndex = 0;
let imagesArray = [];
let currentDraftPostId = null;

const postModalElement = document.getElementById('postModal');
const draftModalElement = document.getElementById('draftModal');
const imagePreviewContainer = document.getElementById('imagePreviewContainer');
const dragDropText = document.getElementById('dragDropText');
const shareButton = document.getElementById('shareButton');
const leftContent = document.getElementById('leftContent');
const prevButton = document.getElementById('prevButton');
const nextButton = document.getElementById('nextButton');
const charCountElement = document.getElementById('charCount');
const contentElement = document.getElementById('content');
const completeContainer = document.getElementById('complete-container');

function openModal() {
  console.log('모달 열기');
  postModalElement.style.display = 'flex';
}

function closeModal() {
  console.log('모달 닫기');
  postModalElement.style.display = 'none';
}

function openDraftModal() {
  console.log('임시 저장 모달 열기');
  draftModalElement.style.display = 'flex';
  loadDraft();
}

function closeDraftModal() {
  console.log('임시 저장 모달 닫기');
  draftModalElement.style.display = 'none';
}

function showComplete() {
  console.log('게시물 등록 완료 화면 표시');
  document.querySelector('.content').style.display = 'none';
  completeContainer.style.display = 'block';
}

function previewImages(event) {
  console.log('이미지 미리보기 시작');
  const files = event.target.files;
  imagePreviewContainer.innerHTML = '';
  imagesArray = [];

  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    event.target.value = "";
    console.warn('최대 이미지 개수 초과');
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader();
    reader.onload = function (e) {
      imagesArray.push(e.target.result);
      if (i === 0) {
        const img = document.createElement('img');
        img.src = e.target.result;
        img.classList.add('photo');
        img.style.objectFit = 'cover';
        imagePreviewContainer.appendChild(img);
      }
    };
    reader.readAsDataURL(file);
  }

  if (files.length > 0) {
    dragDropText.style.display = 'none';
    shareButton.style.display = 'none';
    leftContent.classList.add('fullscreen');

    prevButton.style.display = files.length > 1 ? 'block' : 'none';
    nextButton.style.display = files.length > 1 ? 'block' : 'none';
  }

  console.log(`이미지 ${files.length}개 미리보기 완료`);
}

function updateCharCount() {
  const content = contentElement.value;
  charCountElement.textContent = `${content.length} / 2200`;
  console.log(`내용 길이 업데이트: ${content.length}자`);
}

async function saveAsTemp(event) {
  event.preventDefault();
  console.log('임시 저장 시작');

  const content = contentElement.value;
  const images = document.getElementById('images').files;

  if (!content) {
    alert('내용을 입력하세요.');
    console.warn('내용이 입력되지 않음');
    return;
  }

  if (images.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    console.warn('최대 이미지 개수 초과');
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

  try {
    const response = await fetch('/api/posts/temp-reg', {
      method: 'POST',
      body: formData,
    });

    if (response.ok) {
      console.log('임시 저장 성공');
      alert('임시 저장되었습니다.');
      closeModal();
    } else {
      const data = await response.json();
      console.error('임시 저장 실패:', data.message);
      alert(data.message || '임시 저장에 실패했습니다.');
    }
  } catch (error) {
    console.error('임시 저장 중 오류 발생:', error);
    alert('임시 저장 중 오류가 발생했습니다.');
  }
}

async function submitPost(event) {
  event.preventDefault();
  console.log('게시물 등록 시작');

  const content = contentElement.value;
  const images = document.getElementById('images').files;

  if (!content) {
    alert('내용을 입력하세요.');
    console.warn('내용이 입력되지 않음');
    return;
  }

  if (images.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    console.warn('최대 이미지 개수 초과');
    return;
  }

  const postRequest = {
    tempId: currentDraftPostId,
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

  try {
    const response = await fetch('/api/posts/reg', {
      method: 'POST',
      body: formData,
    });

    if (response.ok) {
      const data = await response.json();
      console.log('게시물 등록 성공:', data);
      document.querySelector('.content').style.display = 'none';
      completeContainer.style.display = 'block';
      setTimeout(() => {
        completeContainer.style.display = 'none';
        window.location.href = `/html/post/postDetail.html?postId=${data.data}`;
      }, 3000);
    } else {
      const data = await response.json();
      console.error('게시물 등록 실패:', data.message);
      throw new Error(data.message || '게시물 등록에 실패했습니다.');
    }
  } catch (error) {
    console.error('게시물 등록 중 오류 발생:', error);
    alert(error.message);
  }
}

async function loadDraft() {
  console.log('임시 저장 목록 불러오기');
  const tempPostsContainer = document.getElementById('tempPostsContainer');
  tempPostsContainer.innerHTML = '';

  try {
    const response = await fetch(`/api/posts/temp-posts/list`);

    console.log('서버 응답 상태:', response.status); // 응답 상태 출력
    const responseText = await response.text(); // 응답을 텍스트로 받음
    console.log('서버 응답 텍스트:', responseText); // 응답 텍스트 로그 출력

    try {
      const tempPosts = JSON.parse(responseText); // JSON 파싱 시도
      console.log('임시 저장 목록 불러오기 성공:', tempPosts);

      tempPosts.data.forEach(post => {
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
        postElement.onclick = () => continueDraft(post);
        tempPostsContainer.appendChild(postElement);
      });
      tempPostsContainer.style.display = 'flex';

    } catch (jsonError) {
      console.error('JSON 파싱 중 오류 발생:', jsonError);
    }

  } catch (error) {
    console.error('임시 저장 글 불러오기 중 오류 발생:', error);
    alert('임시 저장 글 불러오기 중 오류가 발생했습니다.');
  }
}

function continueDraft(post) {
  console.log('임시 저장 글 이어쓰기:', post);
  contentElement.value = post.content;
  updateCharCount();
  currentDraftPostId = post.postId;

  imagePreviewContainer.innerHTML = '';
  imagesArray = post.imageUrls;
  currentImageIndex = 0;
  displayImages();

  closeDraftModal();
  openModal();
}

function displayImages() {
  console.log('이미지 표시:', imagesArray);
  imagePreviewContainer.innerHTML = '';

  if (imagesArray.length > 0) {
    const img = document.createElement('img');
    img.src = imagesArray[currentImageIndex];
    img.classList.add('photo');
    img.style.objectFit = 'cover';
    imagePreviewContainer.appendChild(img);

    prevButton.style.display = imagesArray.length > 1 ? 'block' : 'none';
    nextButton.style.display = imagesArray.length > 1 ? 'block' : 'none';
  }
}

async function deleteTempPost(postId, event) {
  event.stopPropagation();
  console.log('임시 저장 글 삭제:', postId);
  try {
    const response = await fetch(`/api/posts/temp-delete/${postId}`, {
      method: 'POST',
    });

    if (response.ok) {
      console.log('임시 저장 글 삭제 성공:', postId);
      loadDraft();
    } else {
      console.error('임시 저장 글 삭제 실패:', postId);
      alert('임시 저장 글 삭제에 실패했습니다.');
    }
  } catch (error) {
    console.error('임시 저장 글 삭제 중 오류 발생:', error);
    alert('임시 저장 글 삭제 중 오류가 발생했습니다.');
  }
}

function prevImage(event) {
  event.preventDefault();
  console.log('이전 이미지 보기');
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imagesArray.length - 1
        : currentImageIndex - 1;
    displayImages();
  }
}

function nextImage(event) {
  event.preventDefault();
  console.log('다음 이미지 보기');
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0
        : currentImageIndex + 1;
    displayImages();
  }
}
