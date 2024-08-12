let currentImageIndex = 0;
let imagesArray = [];
let currentDraftPostId = null;
let tagsArray = [];

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
const tagsInputElement = document.getElementById('tagsInput');
const tagListElement = document.getElementById('tagList');

//태그~~
// 태그 입력 필드에서 엔터를 눌렀을 때 태그 추가
// tagsInputElement.addEventListener('keypress', function (event) {
//   if (event.key === 'Enter') {
//     event.preventDefault();
//     addTag(tagsInputElement.value.trim());
//     tagsInputElement.value = '';
//   }
// });
//
// // 태그 추가 함수
// function addTag(tag) {
//   if (tag.length > 0 && tag.startsWith('#')) {
//     if (tagsArray.length >= 10) {
//       alert('최대 10개의 태그만 추가할 수 있습니다.');
//       return;
//     }
//     if (tagsArray.includes(tag)) {
//       alert('이미 추가된 태그입니다.');
//       return;
//     }
//     tagsArray.push(tag);
//     updateTagList();
//   } else {
//     alert('태그는 #으로 시작해야 합니다.');
//   }
// }
//
// // 태그 리스트 업데이트 함수
// function updateTagList() {
//   tagListElement.innerHTML = '';
//   tagsArray.forEach((tag, index) => {
//     const tagElement = document.createElement('span');
//     tagElement.classList.add('tag');
//     tagElement.innerText = tag;
//     const removeTagElement = document.createElement('span');
//     removeTagElement.innerText = '×';
//     removeTagElement.classList.add('remove-tag');
//     removeTagElement.onclick = () => removeTag(index);
//     tagElement.appendChild(removeTagElement);
//     tagListElement.appendChild(tagElement);
//   });
// }
//
// // 태그 제거 함수
// function removeTag(index) {
//   tagsArray.splice(index, 1);
//   updateTagList();
// }

// 모달 열기 함수
function openModal() {
  postModalElement.style.display = 'flex';
}

// 모달 닫기 함수
function closeModal() {
  postModalElement.style.display = 'none';
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

// 이미지 미리보기 처리 함수
function previewImages(event) {
  const files = event.target.files;
  imagePreviewContainer.innerHTML = '';
  imagesArray = [];

  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    event.target.value = '';
    return;
  }

  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader();
    reader.onload = function (e) {
      imagesArray.push({
        file: file,
        url: e.target.result,
      });
      if (i === 0) {
        displayImage(e.target.result);
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
}

// 선택된 이미지를 화면에 표시하는 함수
function displayImage(url) {
  const img = document.createElement('img');
  img.src = url;
  img.classList.add('photo');
  img.style.objectFit = 'cover';
  imagePreviewContainer.appendChild(img);
}

// 글자 수 업데이트 함수
function updateCharCount() {
  const content = contentElement.value;
  charCountElement.textContent = `${content.length} / 2200`;
}

// 게시물 데이터 준비 함수//태그 추가햇음~~
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
  const formData = new FormData();
  formData.append('post',
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
      const data = await response.json();
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
  console.log('게시물 등록 완료 화면 표시');
  document.querySelector('.content').style.display = 'none';
  completeContainer.style.display = 'block';

  const completeImage = completeContainer.querySelector('img');
  completeImage.onload = function () {
    console.log('Image successfully loaded');
  };
  completeImage.onerror = function () {
    console.log('Error loading image');
  };
}

// 게시물을 임시 저장하는 함수
async function saveAsTemp(event) {
  event.preventDefault();

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
      window.location.href = `/html/post/postDetail.html?postId=${postId}`;
    }, 2000);
  } catch (error) {
    console.error('게시물 등록 실패:', error.message);
    alert(error.message);
  }
}

// 임시 저장된 글 목록 불러오기 함수
async function loadDraft() {
  const tempPostsContainer = document.getElementById('tempPostsContainer');
  tempPostsContainer.innerHTML = '';

  try {
    const response = await fetch(`/api/posts/temp-posts/list`, {
      credentials: 'include',
    });

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('text/html')) {
      console.error('세션이 만료되었거나 인증이 필요합니다.');
      window.location.href = '/login';
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

          const postContent = post.content.length > 40 ? post.content.substring(
              0, 40) + '...' : post.content;
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

      tempPostsContainer.style.display = 'flex';
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
  currentDraftPostId = post.postId;

  imagePreviewContainer.innerHTML = '';

  imagesArray = post.imageUrls.map(image => ({
    url: image.filepath,
    filename: image.filename,
  }));

  displayImages();

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
  event.stopPropagation();

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
      console.error('임시 저장 글 삭제 실패:', errorData.message || postId);
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
