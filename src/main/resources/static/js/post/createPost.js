let currentImageIndex = 0;
let imagesArray = [];
let currentDraftPostId = null;

function openModal() {
  document.getElementById('postModal').style.display = 'flex';
}

function closeModal() {
  document.getElementById('postModal').style.display = 'none';
}

function openDraftModal() {
  document.getElementById('draftModal').style.display = 'flex';
  loadDraft();
}

function closeDraftModal() {
  document.getElementById('draftModal').style.display = 'none';
}

function showComplete() {
  document.querySelector('.content').style.display = 'none';
  document.getElementById('complete-container').style.display = 'block';
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
    event.target.value = "";
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
        previewContainer.appendChild(img);
      }
    };
    reader.readAsDataURL(file);
  }

  if (files.length > 0) {
    dragDropText.style.display = 'none';
    shareButton.style.display = 'none';
    leftContent.classList.add('fullscreen');

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
  charCount.textContent = `${content.length} / 2200`;
}

function getJwtToken() {
  const name = 'accessToken=';
  const decodedCookie = decodeURIComponent(document.cookie);
  const ca = decodedCookie.split(';');
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) === 0) {
      return c.substring(name.length, c.length);
    }
  }
  return '';
}

async function saveAsTemp(event) {
  event.preventDefault();

  const content = document.getElementById('content').value;
  const images = document.getElementById('images').files;
  const token = getJwtToken();
  console.log('Token:', token); // 디버깅: 토큰 값 확인

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

  console.log('Sending request with token'); // 디버깅: 요청 보내기 전 로그
  fetch('/api/posts/temp-reg', {
    method: 'POST',
    body: formData,
    headers: {
      'Authorization': 'Bearer ' + token
    }
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
  const token = getJwtToken();
  console.log('Token:', token); // 디버깅: 토큰 값 확인

  if (!content) {
    alert('내용을 입력하세요.');
    return;
  }

  if (images.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
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

  console.log('Sending request with token'); // 디버깅: 요청 보내기 전 로그
  fetch('/api/posts/reg', {
    method: 'POST',
    body: formData,
    headers: {
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      throw new Error('게시물 등록에 실패했습니다.');
    }
  }).then(postId => {
    document.querySelector('.content').style.display = 'none';
    document.getElementById('complete-container').style.display = 'block';
    setTimeout(() => {
      document.getElementById('complete-container').style.display = 'none';
      window.location.href = `/html/post/postDetail.html?postId=${postId}`;
    }, 3000);
  }).catch(error => {
    alert(error.message);
  });
}

async function loadDraft() {
  const token = getJwtToken();
  const tempPostsContainer = document.getElementById('tempPostsContainer');
  tempPostsContainer.innerHTML = '';

  try {
    console.log('Loading drafts with token'); // 디버깅: 요청 보내기 전 로그
    const response = await fetch(`/api/posts/temp-posts/list`, {
      headers: {
        'Authorization': 'Bearer ' + token
      }
    });
    if (response.ok) {
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const tempPosts = await response.json();
        tempPosts.forEach(post => {
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
        tempPostsContainer.style.display = 'flex';
      } else {
        throw new Error("Received content is not JSON");
      }
    } else {
      alert('임시 저장 글 불러오기에 실패했습니다.');
    }
  } catch (error) {
    alert('임시 저장 글 불러오기 중 오류가 발생했습니다.');
    console.error('Error:', error);
  }
}

function continueDraft(post) {
  document.getElementById('content').value = post.content;
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
    img.style.objectFit = 'cover';
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
  imagesArray.splice(index, 1);
  displayImages();
}

async function deleteTempPost(postId, event) {
  event.stopPropagation();
  const token = getJwtToken();
  try {
    console.log('Deleting draft with token'); // 디버깅: 요청 보내기 전 로그
    const response = await fetch(`/api/posts/temp-delete/${postId}`, {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token
      }
    });
    if (response.ok) {
      loadDraft();
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
    displayImages();
  }
}

function nextImage(event) {
  event.preventDefault();
  if (imagesArray.length > 1) {
    currentImageIndex = (currentImageIndex === imagesArray.length - 1) ? 0
        : currentImageIndex + 1;
    displayImages();
  }
}
