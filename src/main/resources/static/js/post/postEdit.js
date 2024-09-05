let currentPostId = null; // 현재 게시물의 ID를 저장하는 변수
let currentImageIndex = 0; // 현재 이미지 인덱스를 저장하는 변수
let imagesArray = []; // 업로드된 이미지 파일과 URL을 저장하는 배열
let tagsSet = new Set();
const tagsInputElement = document.getElementById('tagsInput');
const tagList = document.getElementById('tagList');

tagsInputElement.addEventListener("keydown", addTag)
// 태그 정규표현식
// 다양한 언어 문자 허용, 숫자 허용, '_' 허용, 유니코드 문자 지원
const tagPattern = /^[\p{L}\p{N}_]+$/u;

// 페이지 로드 시 게시물 데이터를 불러와서 폼에 채우는 함수
async function loadEditPostData(postId) {
  currentPostId = postId;
  console.log(`게시물 수정 데이터 로드 시작: ${postId}`);

  try {
    const response = await fetch(`/api/posts/detail/${postId}`);
    if (!response.ok) {
      throw new Error('서버 응답이 올바르지 않습니다.');
    }

    const postData = await response.json();
    document.getElementById('content').value = postData.content;  // 게시물 내용을 폼에 채우기
    document.getElementById('nicknameDisplay').textContent = postData.nickname || '닉네임 없음';
    document.getElementById('profileImage').src = postData.profileImageurl || '/image/profile/profile.png';
    document.getElementById('charCount').textContent = `${postData.content.length} / 2200`;

    // 좋아요 숨기기 상태 설정
    document.getElementById('hideLikes').checked = postData.hideLikeCount;

    // 댓글 기능 해제 상태 설정
    document.getElementById('disableComments').checked = postData.commentsDisabled;

    // 태그 보여주기
    tagsSet = new Set(postData.tags)
    displayTagList()

    // 이미지 URL 배열을 업데이트
    imagesArray = postData.imageUrls || [];
    updateImageContainer('imagePreviewContainer', imagesArray);
    toggleSlideButtons(imagesArray.length, 'prevButton', 'nextButton');
  } catch (error) {
    console.error('게시물 수정 데이터 로드 중 오류 발생:', error.message);
    alert('게시물 데이터를 로드하는 중 오류가 발생했습니다.');
  }
}

function addTag(event) {
  // 엔터 키의 키 코드는 13
  if (event.key === 'Enter') {
    if(tagsSet.size > 10) {
      alert("태그는 10개까지만 등록 가능합니다.")
      tagsInputElement.value=""
      return
    }

    let tag = tagsInputElement.value
    tag = tag.trim().replace(/^#/,"")

    if(tag.length > 20) {
      alert("태그 길이는 최대 20자까지만 가능합니다.");
      return;
    }

    if(tagPattern.test(tag)) {
      tagsSet.add(tag);
      tagsInputElement.value=""
      displayTagList();
    } else {
      alert("등록할 수 없는 태그입니다.")
    }
  }
}

function displayTagList() {
  tagList.innerHTML = ''; // 기존 태그 리스트 초기화
  tagsSet.forEach(tag => {
    const tagElement = document.createElement('span');
    tagElement.className = 'tag';

    const tagText = document.createElement('span');
    tagText.className = 'tag-text';
    tagText.textContent = `#${tag}`;

    const removeBtn = document.createElement('button');
    removeBtn.textContent = 'x';
    removeBtn.className = 'remove-btn';
    removeBtn.addEventListener("click", (event) => removeTag(event,tag))

    tagElement.appendChild(tagText); // 태그 텍스트 추가
    tagElement.appendChild(removeBtn); // 삭제 버튼 추가
    tagList.appendChild(tagElement); // 태그 리스트에 추가
  });
}

function removeTag(event, tag) {
  event.preventDefault()
  // 태그 배열에서 해당 태그를 제거
  tagsSet.delete(tag);
  console.log(tagsSet)
  displayTagList(); // DOM 업데이트
}

// 이미지와 동영상 파일을 미리보기로 처리하는 함수
function previewImages(event) {
  const files = event.target.files;
  const container = document.getElementById('imagePreviewContainer');
  container.innerHTML = '';
  imagesArray = [];

  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader();
    reader.onload = function (e) {
      const fileUrl = e.target.result;
      const fileExtension = file.name.split('.').pop().toLowerCase();

      imagesArray.push({ file: file, url: fileUrl, filepath: fileUrl });

      if (i === 0) {
        displayImageOrVideo(fileUrl, fileExtension);
      }
    };
    reader.readAsDataURL(file);
  }

  if (files.length > 0) {
    document.getElementById('shareButton').style.display = 'none';
    document.getElementById('prevButton').style.display = files.length > 1 ? 'block' : 'none';
    document.getElementById('nextButton').style.display = files.length > 1 ? 'block' : 'none';
  }
}

// 선택된 이미지나 동영상을 화면에 표시하는 함수
function displayImageOrVideo(url, fileExtension) {
  const container = document.getElementById('imagePreviewContainer');
  container.innerHTML = '';

  let mediaElement;
  if (fileExtension === 'mp4' || fileExtension === 'mov' || fileExtension === 'webm') {
    mediaElement = document.createElement('video');
    mediaElement.controls = true;
  } else {
    mediaElement = document.createElement('img');
  }

  mediaElement.src = url;
  mediaElement.style.width = '100%';
  mediaElement.style.height = '100%';
  mediaElement.style.objectFit = 'cover';

  container.appendChild(mediaElement);
}

// 이미지 및 동영상 컨테이너를 업데이트하는 함수
function updateImageContainer(containerId, urls) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';

  urls.forEach((mediaObj, index) => {
    let mediaElement;
    const fileExtension = mediaObj.filepath.split('.').pop().toLowerCase(); // 파일 확장자 추출

    if (fileExtension === 'mp4' || fileExtension === 'mov' || fileExtension === 'webm') {
      mediaElement = document.createElement('video');
      mediaElement.controls = true;
      mediaElement.src = mediaObj.url || mediaObj.filepath; // 동영상 파일 경로 설정
    } else {
      mediaElement = document.createElement('img');
      mediaElement.src = mediaObj.url || mediaObj.filepath; // 이미지 파일 경로 설정
    }

    mediaElement.alt = `media ${index + 1}`;
    mediaElement.style.display = index === 0 ? 'block' : 'none';
    mediaElement.className = index === 0 ? 'active' : '';
    mediaElement.style.width = '100%';
    mediaElement.style.height = '100%';
    mediaElement.style.objectFit = 'cover';

    mediaElement.onerror = () => {
      console.error('미디어 로드 실패:', mediaObj.url || mediaObj.filepath);
      mediaElement.parentElement.removeChild(mediaElement);
    };

    container.appendChild(mediaElement);
  });
}

// 이전 이미지를 표시하는 함수
function prevImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === 0) ? imagesArray.length - 1 : currentImageIndex - 1;
    showImage(currentImageIndex, 'imagePreviewContainer');
  }
}

// 다음 이미지를 표시하는 함수
function nextImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === imagesArray.length - 1) ? 0 : currentImageIndex + 1;
    showImage(currentImageIndex, 'imagePreviewContainer');
  }
}

// 현재 인덱스에 해당하는 이미지를 표시하는 함수 (동영상도 포함)
function showImage(index, containerId) {
  const mediaElements = document.querySelectorAll(`#${containerId} img, #${containerId} video`);
  mediaElements.forEach((media, idx) => {
    media.style.display = idx === index ? 'block' : 'none';
    media.className = idx === index ? 'active' : '';
  });
}

// 슬라이드 버튼을 표시하거나 숨기는 함수
function toggleSlideButtons(length, prevBtnId, nextBtnId) {
  const displayValue = length > 1 ? 'block' : 'none';
  document.getElementById(prevBtnId).style.display = displayValue;
  document.getElementById(nextBtnId).style.display = displayValue;
}

// 게시물 수정 완료 후 서버로 데이터를 전송하는 함수
async function submitEditPost() {
  const content = document.getElementById('content').value;
  const hideLikes = document.getElementById('hideLikes').checked; // 좋아요 숨김 상태 가져오기
  const disableComments = document.getElementById('disableComments').checked; // 댓글 기능 해제 상태 가져오기

  // formData 객체 생성 및 게시물 데이터 추가
  const formData = new FormData();
  formData.append('post', new Blob([JSON.stringify({
    content: content,
    tags: Array.from(tagsSet), // 태그 배열
    hideLikeCount: hideLikes,  // 좋아요 숨김 상태 추가
    commentsDisabled: disableComments // 댓글 기능 해제 상태 추가
  })], { type: 'application/json' }));

  // 이미지 파일이 있는 경우 formData에 추가
  imagesArray.forEach(imageObj => {
    if (imageObj.file) {
      formData.append('images', imageObj.file);
    }
  });

  try {
    // 서버로 POST 요청 전송
    const response = await fetch(`/api/posts/edit/${currentPostId}`, {
      method: 'POST',
      body: formData,
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error('게시물 수정 실패');
    }

    alert('게시물이 수정되었습니다.');
    // 페이지 이동
    window.location.replace(`/posts/detail/${currentPostId}`);
  } catch (error) {
    console.error('게시물 수정 중 오류 발생:', error.message);
    alert('게시물 수정에 실패했습니다.');
  }
}

// 모달을 닫는 함수
function closePostEditModal() {
  document.getElementById('postModal').style.display = 'none';
  // window.history.back(); 제거
}

// 글자 수를 업데이트하는 함수
function updateCharCount() {
  const content = document.getElementById('content').value;
  document.getElementById('charCount').textContent = `${content.length} / 2200`;
}

// 페이지 로드 시 게시물 데이터를 불러오는 함수 호출
document.addEventListener('DOMContentLoaded', () => {
  const postId = window.location.pathname.split('/').pop();
  if (postId && !isNaN(postId)) {
    loadEditPostData(postId);
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }

  // 히스토리 상태 초기화
  history.replaceState({ noBackEx: true }, '', window.location.href);
});
