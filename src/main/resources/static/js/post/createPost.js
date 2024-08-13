let currentImageIndex = 0;
let imagesArray = [];
let currentDraftPostId = null;
let tagsArray = [];

const postModalElement = document.getElementById('postModal');
const draftModalElement = document.getElementById('draftModal');
const imagePreviewContainer = document.getElementById('imagePreviewContainer');
const dragDropText = document.getElementById('dragDropText');
const shareButton = document.getElementById('shareButton'); // 공유 버튼
const leftContent = document.getElementById('leftContent');
const prevButton = document.getElementById('prevButton');
const nextButton = document.getElementById('nextButton');
const charCountElement = document.getElementById('charCount'); // 글자 수 카운터
const contentElement = document.getElementById('content');
const completeContainer = document.getElementById('complete-container');
const imgElement = document.getElementById('profileImage');
const nicknameElement = document.getElementById('nicknameDisplay');

//프로필 fileUrl & nickname 가져
fetch('/api/mypage/profiles/info', {
  method: 'GET',
  credentials: 'include', // 쿠키를 포함해서 요청 보냄
})
.then(response => response.json())
.then(data => {
  const profileImageUrl = data.fileUrl;
  const nickname = data.nickname;

  if (profileImageUrl) {
    // 프로필 이미지가 있는 경우
    imgElement.src = profileImageUrl;
  } else {
    // 프로필 이미지가 없는 경우 기본 이미지로 설정
    imgElement.src = '/image/profile/profile.png';
  }

  if (nickname) {
    nicknameElement.textContent = nickname;
  } else {
    nicknameElement.textContent = '닉네임 없음';
  }
})
.catch(error => {
  console.error('Error:', error);
  // 에러 발생 시 기본 이미지 설정 및 닉네임 오류 표시
  document.getElementById('profileImage').src = '/image/profile/profile.png';
  document.getElementById('nicknameDisplay').textContent = '오류 발생';
});

// 게시물 작성 모달을 여는 함수
function openModal() {
  postModalElement.style.display = 'flex'; // 모달을 화면에 표시
}

// 게시물 작성 모달을 닫는 함수
function closeModal() {
  postModalElement.style.display = 'none'; // 모달을 화면에서 숨김
}

// 임시 저장 모달 열기 함수
function openDraftModal() {
  draftModalElement.style.display = 'flex';
  loadDraft();
}

// 임시 저장 모달 닫기 함수
function closeDraftModal() {
  draftModalElement.style.display = 'none';
}

// 이미지 미리보기 및 이미지 파일 처리 함수
function previewImages(event) {
  const files = event.target.files; // 사용자가 선택한 파일들
  imagePreviewContainer.innerHTML = ''; // 기존 미리보기 초기화
  imagesArray = []; // 이미지 배열 초기화

  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    event.target.value = '';
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader(); // 파일을 읽기 위한 FileReader 객체 생성
    reader.onload = function (e) { // 파일 읽기가 완료되면 실행
      imagesArray.push({
        file: file, // 원본 파일 저장
        url: e.target.result, // 파일의 Data URL 저장 (미리보기에 사용)
      });
      if (i === 0) { // 첫 번째 파일인 경우
        displayImage(e.target.result); // 미리보기 화면에 표시
      }
    };
    reader.readAsDataURL(file); // 파일을 Data URL 형식으로 읽음
  }

  if (files.length > 0) {
    dragDropText.style.display = 'none';
    shareButton.style.display = 'none';
    leftContent.classList.add('fullscreen');
    prevButton.style.display = files.length > 1 ? 'block' : 'none';
    nextButton.style.display = files.length > 1 ? 'block' : 'none';
  }
}

// 선택된 이미지를 화면에 표시하는 함수
function displayImage(url) {
  const img = document.createElement('img'); // 이미지 요소 생성
  img.src = url; // 이미지 소스 설정
  img.classList.add('photo'); // 이미지에 클래스 추가 (스타일 적용을 위해)
  img.style.objectFit = 'cover'; // 이미지가 컨테이너에 맞게 크기 조정
  imagePreviewContainer.appendChild(img); // 이미지 요소를 미리보기 컨테이너에 추가
}

// 글자 수 업데이트 함수
function updateCharCount() {
  const content = contentElement.value;
  charCountElement.textContent = `${content.length} / 2200`;
}

// 게시물 데이터 준비 함수
function preparePostData(tempId, content, images, status) {
  return {
    tempId: tempId,
    content: content,
    tempStatus: status,
    imageUrls: images.map(image => image.url),
    tags: tagsArray // 태그 데이터 추가
  };
}

// 게시물 데이터를 서버로 전송하는 함수
async function submitPostData(url, postRequest, images) {
  const formData = new FormData(); // 서버로 전송할 데이터를 담을 FormData 객체 생성
  formData.append('post',
      // 게시물 데이터를 JSON으로 변환하여 FormData에 추가
      new Blob([JSON.stringify(postRequest)], {type: 'application/json'}));

  if (images && images.length > 0) {
    for (let i = 0; i < images.length; i++) {
      if (images[i].file) {
        formData.append('images', images[i].file);
      }
    }
  } else {
    formData.append('images', new Blob([]), '');
  }

  try {
    const response = await fetch(url, {
      method: 'POST',
      body: formData,
      credentials: 'include'
    });

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
      const data = await response.json(); // 응답을 JSON으로 파싱
      return data;
    } else {
      const text = await response.text();
      console.error('서버 응답이 JSON이 아닙니다:', text);
      throw new Error('서버 응답이 JSON이 아닙니다. 응답 내용: ' + text);
    }
  } catch (error) {
    console.error('서버 요청 중 오류 발생:', error.message);
    alert(error.message || '서버 요청 중 오류 발생');
    throw error;
  }
}

// 게시물 등록 완료 화면 표시 함수
function showComplete() {
  completeContainer.style.display = 'block';

  const completeImage = completeContainer.querySelector('img');
  completeImage.onload = function () {
  };
  completeImage.onerror = function () {
  };

  // 2초 후에 페이지 새로고침
  setTimeout(() => {
    window.location.reload();
  }, 1000);
}

// 게시물을 임시 저장하는 함수
async function saveAsTemp(event) {
  event.preventDefault(); // 기본 폼 제출 동작 방지

  const content = contentElement.value;
  if (!content) {
    alert('내용을 입력하세요.');
    return;
  }

  const postRequest = preparePostData(null, content, imagesArray, 'TEMP');

  try {
    await submitPostData('/api/posts/temp-reg', postRequest, imagesArray);
    alert('임시 저장되었습니다.');
    closeModal();
    resetForm(); // 폼 초기화

    // 임시 저장 완료 후 페이지 새로고침
    window.location.reload();
  } catch (error) {
    console.error('임시 저장 실패:', error.message);
    alert(error.message);
  }
}

// 게시물을 최종 제출하는 함수
async function submitPost(event) {
  event.preventDefault();

  const content = contentElement.value;
  if (!content) {
    alert('내용을 입력하세요.');
    return;
  }

  const postRequest = preparePostData(currentDraftPostId, content, imagesArray,
      'SAVE');

  try {
    const data = await submitPostData('/api/posts/reg', postRequest,
        imagesArray);
    const postId = data.data.postId || data.data;

    showComplete(); // 게시물 등록 완료 화면 표시

    setTimeout(() => {
      completeContainer.style.display = 'none';
      closeModal(); // 모달 창 닫기
      resetForm(); // 폼 초기화
    }, 2000);
  } catch (error) {
    console.error('게시물 등록 실패:', error.message);
    alert(error.message);
  }
}

// 폼 데이터와 상태를 초기화하는 함수
function resetForm() {
  // 폼 필드 초기화
  contentElement.value = '';
  charCountElement.textContent = '0 / 2200';

  // 이미지 미리보기 초기화
  imagePreviewContainer.innerHTML = '';
  imagesArray = [];
  currentImageIndex = 0;

  // 태그 초기화
  tagsArray = [];
  updateTagList();

  // 기타 필요한 초기화
  document.getElementById('images').value = ''; // 파일 입력 필드 초기화
  dragDropText.style.display = 'block';
  shareButton.style.display = 'block';
  leftContent.classList.remove('fullscreen');
}

// 임시 저장된 글 목록 불러오기 함수
async function loadDraft() {
  const tempPostsContainer = document.getElementById('tempPostsContainer');
  tempPostsContainer.innerHTML = '';

  try {
    const response = await fetch(`/api/posts/temp-posts/list`, {
      credentials: 'include', // 인증 정보를 포함
    });

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('text/html')) {
      window.location.href = '/login'; // 세션 만료 시 로그인 페이지로 리디렉션
      return;
    }

    if (contentType && contentType.includes('application/json')) {
      const tempPosts = await response.json();

      if (tempPosts.data.length === 0) {
        tempPostsContainer.innerHTML = '<p>임시 저장된 글이 없습니다.</p>';
      } else {
        tempPosts.data.forEach(post => {
          const postElement = document.createElement('div');
          postElement.classList.add('temp-post');

          const postContent = post.content.length > 40
              ? post.content.substring(0, 40) + '...' : post.content;
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
      }

      tempPostsContainer.style.display = 'flex'; // 임시 저장된 글 리스트 표시
    } else {
      console.error('서버 응답이 예상치 않은 형식입니다:', contentType);
    }

  } catch (error) {
    console.error('임시 저장 글 불러오기 중 오류 발생:', error);
    alert('임시 저장 글 불러오기 중 오류가 발생했습니다.');
  }
}

// 임시 저장된 글 이어서 작성하기 위한 함수
function continueDraft(post) {
  contentElement.value = post.content;
  updateCharCount();
  currentDraftPostId = post.postId; // 현재 작성 중인 게시물의 ID 설정

  imagePreviewContainer.innerHTML = '';

  imagesArray = post.imageUrls.map(image => ({
    url: image.filepath,
    filename: image.filename,
  }));

  displayImages(); // 이미지 미리보기 업데이트

  document.getElementById('images').disabled = true;
  shareButton.style.display = 'none';
  dragDropText.style.display = 'none';

  closeDraftModal();
  openModal();
}

// 현재 이미지를 화면에 표시하는 함수
function displayImages() {
  imagePreviewContainer.innerHTML = '';

  if (imagesArray.length > 0) {
    const img = document.createElement('img');
    img.src = imagesArray[currentImageIndex].url;
    img.classList.add('photo');
    img.style.objectFit = 'cover';
    imagePreviewContainer.appendChild(img);

    prevButton.style.display = imagesArray.length > 1 ? 'block' : 'none';
    nextButton.style.display = imagesArray.length > 1 ? 'block' : 'none';
  }
}

// 임시 저장된 글 삭제 함수
async function deleteTempPost(postId, event) {
  event.stopPropagation(); // 이벤트 전파 방지 (글 삭제 시 글 열림 방지)

  try {
    const response = await fetch(`/api/posts/temp-delete/${postId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        credentials: 'include',
      },
    });

    if (response.ok) {
      const postElement = document.querySelector(
          `.delete-link[onclick="deleteTempPost(${postId}, event)"]`).parentElement.parentElement;
      postElement.remove();
    } else {
      const errorData = await response.json();
      alert('임시 저장 글 삭제에 실패했습니다.');
    }
  } catch (error) {
    console.error('임시 저장 글 삭제 중 오류 발생:', error);
    alert('임시 저장 글 삭제 중 오류가 발생했습니다.');
  }
}

// 이전 이미지 보기 함수
function prevImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === 0 ? imagesArray.length - 1
        : currentImageIndex - 1;
    displayImages();
  }
}

// 다음 이미지 보기 함수
function nextImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === imagesArray.length - 1 ? 0
        : currentImageIndex + 1;
    displayImages();
  }
}
