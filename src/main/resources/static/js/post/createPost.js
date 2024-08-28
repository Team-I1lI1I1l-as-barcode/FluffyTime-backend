let currentImageIndex = 0; // 현재 선택된 이미지의 인덱스를 저장
let imagesArray = []; // 사용자가 업로드한 이미지 파일과 URL을 저장하는 배열
let currentDraftPostId = null; // 현재 작성 중인 게시물의 임시 저장 ID를 저장
let tagsSet = new Set(); // 게시물 tag 배열

// 여러 DOM 요소들을 가져옵니다.
const postModalElement = document.getElementById('postModal'); // 게시물 작성 모달
const draftModalElement = document.getElementById('draftModal'); // 임시 저장 모달
const imagePreviewContainer = document.getElementById('imagePreviewContainer'); // 이미지 미리보기
const dragDropText = document.getElementById('dragDropText');
const shareButton = document.getElementById('shareButton'); // 이미지 선택 버튼 요소
const leftContent = document.getElementById('leftContent'); // 모달의 왼쪽 컨텐츠 영역(이미지 선택)
const prevButton = document.getElementById('prevButton');
const nextButton = document.getElementById('nextButton');
const charCountElement = document.getElementById('charCount'); // 글자 수
const contentElement = document.getElementById('content');
const completeContainer = document.getElementById('complete-container'); // 게시물 등록 완료 후 표시되는 사진
const imgElement = document.getElementById('profileImage'); // 사용자 프로필 이미지
const nicknameElement = document.getElementById('nicknameDisplay'); // 사용자 닉네임


// 태그 관련
const tagsInputElement = document.getElementById('tagsInput');
const tagList = document.getElementById('tagList');
tagsInputElement.addEventListener("keydown", addTag)

// 태그 정규표현식
// 다양한 언어 문자 허용, 숫자 허용, '_' 허용, 유니코드 문자 지원
const tagPattern = /^[\p{L}\p{N}_]+$/u;

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


// 프로필 정보를 ~~
fetch('/api/mypage/profiles/info', {
  method: 'GET',
  credentials: 'include',
})
.then(response => response.json())
.then(data => {
  const profileImageUrl = data.fileUrl;
  const nickname = data.nickname; // 사용자 닉네임

  // 닉네임이 존재하면 표시, 그렇지 않으면 '닉네임 없음'으로 설정
  if (nickname) {
    nicknameElement.textContent = nickname;
  } else {
    nicknameElement.textContent = '닉네임 없음';
  }

  // 프로필 이미지가 존재하면 이미지 설정, 그렇지 않으면 기본 이미지로 설정
  if (profileImageUrl) {
    imgElement.src = profileImageUrl;
  } else {
    imgElement.src = '/image/profile/profile.png';
  }
});

// 게시물 작성 모달을 여는 함수
function openPostCreationModal() {
  postModalElement.style.display = 'flex'; // 모달을 화면에 표시
}

// 게시물 작성 모달을 닫는 함수
function closePostCreationModal() {
  postModalElement.style.display = 'none'; // 모달을 화면에서 숨김
  window.location.reload(); // 페이지 새로고침
}

// 임시 저장 모달을 여는 함수
function openDraftModal() {
  draftModalElement.style.display = 'flex'; // 임시 저장 모달을 화면에 표시
  loadDraft(); // 임시 저장된 글 목록을 불러오는 함수 호출
}

// 임시 저장 모달을 닫는 함수
function closeDraftModal() {
  draftModalElement.style.display = 'none'; // 임시 저장 모달을 화면에서 숨김
}

// 이미지 미리보기 및 파일 처리 함수
function previewImages(event) {
  const files = event.target.files; // 사용자가 선택한 파일들
  imagePreviewContainer.innerHTML = ''; // 기존 미리보기를 초기화
  imagesArray = []; // 이미지 배열을 초기화

  // 파일 개수 제한 확인
  if (files.length > 10) {
    alert('이미지는 최대 10장까지 업로드할 수 있습니다.');
    event.target.value = ''; // 파일 입력 초기화
    return;
  }

  // 선택한 파일들을 반복 처리하여 미리보기와 배열에 저장
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader(); // 파일을 읽기 위한 FileReader 객체 생성
    reader.onload = function (e) { // 파일 읽기가 완료되면 실행
      imagesArray.push({
        file: file, // 파일 객체 저장
        url: e.target.result, // 파일의 Data URL 저장 (미리보기에 사용)
      });
      if (i === 0) { // 첫 번째 파일이면 미리보기 화면에 표시
        displayImage(e.target.result);
      }
    };
    reader.readAsDataURL(file); // 파일을 Data URL 형식으로 읽음
  }

  // 파일이 하나라도 선택되었다면 UI 업데이트
  if (files.length > 0) {
    dragDropText.style.display = 'none'; // 드래그 앤 드롭 안내 텍스트 숨기기
    shareButton.style.display = 'none'; // 이미지 선택 버튼 숨기기
    leftContent.classList.add('fullscreen'); // 왼쪽 컨텐츠 영역을 전체 화면 모드로 전환
    prevButton.style.display = files.length > 1 ? 'block' : 'none'; // 이전 버튼 표시 여부
    nextButton.style.display = files.length > 1 ? 'block' : 'none'; // 다음 버튼 표시 여부
  }
}

// 글자 수를 업데이트하는 함수
function updateCharCount() {
  const content = contentElement.value; // 입력된 내용 가져오기
  charCountElement.textContent = `${content.length} / 2200`; // 글자 수 업데이트
}

// 게시물 데이터를 준비하는 함수
function preparePostData(tempId, content, tagsSet ,status) {
  return {
    tempId: tempId, // 임시 저장된 게시물의 ID (없으면 null)
    content: content, // 게시물 내용
    tags: Array.from(tagsSet),
    tempStatus: status, // 게시물 상태 (임시 저장 또는 최종 저장)
  };
}

// 게시물 데이터를 서버로 전송하는 함수
async function submitPostData(url, postRequest, images) {
  const formData = new FormData(); // 서버로 전송할 데이터를 담을 FormData 객체 생성
  formData.append('post',
      new Blob([JSON.stringify(postRequest)], {type: 'application/json'})); // 게시물 데이터를 JSON으로 변환하여 FormData에 추가

  // 이미지 파일을 FormData에 추가
  if (images && images.length > 0) {
    for (let i = 0; i < images.length; i++) {
      if (images[i].file) {
        formData.append('images', images[i].file);
      }
    }
  } else {
    formData.append('images', new Blob([]), ''); // 이미지가 없을 경우 빈 데이터를 추가
  }

  // 서버에 POST 요청 전송
  const response = await fetch(url, {
    method: 'POST',
    body: formData,
    credentials: 'include' // 인증 정보를 포함
  });

  const contentType = response.headers.get('content-type'); // 응답의 Content-Type 헤더 확인
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json(); // JSON 응답 파싱
    return data; // 데이터 반환
  } else {
    throw new Error('서버 응답이 JSON 형식이 아닙니다.'); // JSON 응답이 아닌 경우 오류 처리
  }
}

// 게시물을 임시 저장하는 함수
async function saveAsTemp(event) {
  event.preventDefault(); // 기본 폼 제출 동작 방지

  const content = contentElement.value; // 게시물 내용 가져오기
  if (!content) {
    alert('내용을 입력하세요.'); // 내용이 없을 경우 경고
    return;
  }

  if (imagesArray.length === 0) {
    alert('사진을 등록하세요.'); // 이미지가 없을 경우 경고
    return;
  }

  const postRequest = preparePostData(null, content, tagsSet,'TEMP'); // 임시 저장 요청 데이터 준비

  await submitPostData('/api/posts/temp-reg', postRequest, imagesArray); // 서버로 임시 저장 요청 전송
  alert('임시 저장되었습니다.'); // 임시 저장 완료 알림
  closePostCreationModal(); // 모달 닫기
  resetForm(); // 폼 초기화

  window.location.reload(); // 페이지 새로고침
}

// 게시물을 최종 제출하는 함수
async function submitPost(event) {
  event.preventDefault(); // 기본 폼 제출 동작 방지

  const content = contentElement.value; // 게시물 내용 가져오기
  if (!content) {
    alert('내용을 입력하세요.'); // 내용이 없으면 경고 창 띄우고 종료
    return;
  }

  if (imagesArray.length === 0) {
    alert('사진을 등록하세요.'); // 이미지가 없을 경우 경고 창 띄우고 종료
    return;
  }

  const postRequest = preparePostData(currentDraftPostId, content, tagsSet,'SAVE'); // 최종 제출 요청 데이터 준비

  const data = await submitPostData('/api/posts/reg', postRequest, imagesArray); // 서버로 게시물 등록 요청 전송
  const postId = data?.data?.postId || data.data; // 응답에서 게시물 ID 가져오기

  document.querySelector('.post-right-content').style.display = 'none'; // 게시물 작성 부분 숨기기
  document.querySelector('.post-left-content').style.display = 'none'; // 등록한 이미지 숨기기

  showComplete(); // 게시물 등록 완료 화면 표시

  setTimeout(() => {
    window.location.reload(); // 2초 후 페이지 새로고침
  }, 2000);
}

// 게시물 등록 완료 화면을 표시하는 함수
function showComplete() {
  completeContainer.style.display = 'block'; // 완료 메시지 표시
}

// 폼 데이터와 상태를 초기화하는 함수
function resetForm() {
  contentElement.value = ''; // 게시물 내용 초기화
  charCountElement.textContent = '0 / 2200'; // 글자 수 초기화

  imagePreviewContainer.innerHTML = ''; // 이미지 미리보기 초기화
  imagesArray = []; // 이미지 배열 초기화
  currentImageIndex = 0; // 이미지 인덱스 초기화

  document.getElementById('images').value = ''; // 파일 입력 필드 초기화
  dragDropText.style.display = 'block'; // 드래그 앤 드롭 텍스트 표시
  shareButton.style.display = 'block'; // 이미지 선택 버튼 표시
  leftContent.classList.remove('fullscreen'); // 왼쪽 컨텐츠 영역에서 전체 화면 모드 해제
}

// 임시 저장된 글 목록을 불러오는 함수
async function loadDraft() {
  const tempPostsContainer = document.getElementById('tempPostsContainer'); // 임시 저장된 글을 표시할 컨테이너 요소
  tempPostsContainer.innerHTML = ''; // 기존 목록 초기화

  try {
    const response = await fetch(`/api/posts/temp-posts/list`, {
      credentials: 'include', // 인증 정보를 포함하여 요청
    });

    const contentType = response.headers.get('content-type'); // 응답의 Content-Type 헤더 확인
    if (contentType && contentType.includes('text/html')) {
      window.location.href = '/login'; // 세션이 만료된 경우 로그인 페이지로 리디렉션
      return;
    }

    if (contentType && contentType.includes('application/json')) {
      const tempPosts = await response.json(); // JSON 응답 파싱

      console.log(tempPosts)

      if (tempPosts.length === 0) {
        tempPostsContainer.innerHTML = '<p>임시 저장된 글이 없습니다.</p>'; // 임시 저장된 글이 없을 경우 표시
      } else {
        tempPosts.forEach(post => { // 임시 저장된 각 글에 대해 반복
          const postElement = document.createElement('div'); // 글을 표시할 div 생성
          postElement.classList.add('temp-post'); // 스타일 적용을 위한 클래스 추가

          const postContent = post.content.length > 40
              ? post.content.substring(0, 40) + '...' : post.content; // 글 내용이 길면 40자까지 표시 후 생략
          const postDate = new Date(post.createdAt).toLocaleDateString(); // 글 작성 날짜 포맷팅

          postElement.innerHTML = `
            <div class="post-details">
              <p class="post-content">${postContent}</p>
              <span class="post-date">${postDate}</span>
              <span class="delete-link" onclick="deleteTempPost(${post.postId}, event)">삭제</span> <!-- 삭제 링크 -->
            </div>
          `;
          postElement.onclick = () => continueDraft(post); // 글 클릭 시 이어서 작성
          tempPostsContainer.appendChild(postElement); // 컨테이너에 글 추가
        });
      }
    }
  } catch (error) {
    console.error('임시 저장된 글 목록을 불러오는 중 오류 발생:', error);
    tempPostsContainer.innerHTML = '<p>임시 저장된 글을 불러오는 중 오류가 발생했습니다.</p>'; // 오류 메시지 표시
  }
}

// 임시 저장된 글을 이어서 작성하기 위한 함수
function continueDraft(post) {
  contentElement.value = post.content; // 게시물 내용을 입력 필드에 설정
  updateCharCount(); // 글자 수 업데이트
  currentDraftPostId = post.postId; // 현재 작성 중인 게시물의 ID 설정
  tagsSet = new Set(post.tags);
  console.log(tagsSet)
  displayTagList();

  imagePreviewContainer.innerHTML = ''; // 이미지 미리보기 초기화

  imagesArray = post.imageUrls.map(image => ({
    url: image.filepath, // 이미지 URL 배열로 변환
    filename: image.filename,
  }));

  displayImages(); // 이미지 미리보기 업데이트

  document.getElementById('images').disabled = true; // 이미지 입력 비활성화
  shareButton.style.display = 'none'; // 이미지 선택 버튼 숨기기
  dragDropText.style.display = 'none'; // 드래그 앤 드롭 텍스트 숨기기

  closeDraftModal(); // 임시 저장 모달 닫기
  openPostCreationModal(); // 게시물 작성 모달 열기
}

// 선택된 이미지를 화면에 표시하는 함수
function displayImage(url) {
  const img = document.createElement('img'); // 이미지 요소 생성
  img.src = url; // 이미지 소스 설정
  img.classList.add('photo'); // 이미지에 스타일 적용을 위한 클래스 추가
  img.style.objectFit = 'cover'; // 이미지가 컨테이너에 맞게 크기 조정
  imagePreviewContainer.appendChild(img); // 이미지 요소를 미리보기 컨테이너에 추가
}

// 현재 이미지를 화면에 표시하는 함수
function displayImages() {
  imagePreviewContainer.innerHTML = ''; // 기존 미리보기 초기화

  if (imagesArray.length > 0) {
    const img = document.createElement('img'); // 이미지 요소 생성
    img.src = imagesArray[currentImageIndex].url; // 이미지 소스 설정
    img.classList.add('photo'); // 이미지에 스타일 적용을 위한 클래스 추가

    img.style.objectFit = 'cover'; // 이미지가 컨테이너에 맞게 크기 조정

    leftContent.classList.add('fullscreen'); // 왼쪽 컨텐츠 영역을 전체 화면 모드로 전환
    imagePreviewContainer.appendChild(img); // 이미지 요소를 미리보기 컨테이너에 추가

    prevButton.style.display = imagesArray.length > 1 ? 'block' : 'none'; // 이전 버튼 표시 여부
    nextButton.style.display = imagesArray.length > 1 ? 'block' : 'none'; // 다음 버튼 표시 여부
  }
}

// 임시 저장된 글을 삭제하는 함수
async function deleteTempPost(postId, event) {
  event.stopPropagation(); // 이벤트 전파 방지 (글 삭제 시 글 열림 방지)

  const response = await fetch(`/api/posts/temp-delete/${postId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      credentials: 'include',
    },
  });

  if (response.ok) {
    const postElement = document.querySelector(
        `.delete-link[onclick="deleteTempPost(${postId}, event)"]`).parentElement.parentElement; // 삭제할 글 요소
    postElement.remove(); // 글 요소 제거
  }
}

// 이전 이미지 보기 함수
function prevImage(event) {
  event.preventDefault(); // 기본 동작 방지
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === 0 ? imagesArray.length - 1
        : currentImageIndex - 1; // 현재 이미지 인덱스 업데이트
    displayImages(); // 업데이트된 이미지 표시
  }
}

// 다음 이미지 보기 함수
function nextImage(event) {
  event.preventDefault(); // 기본 동작 방지
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === imagesArray.length - 1 ? 0
        : currentImageIndex + 1; // 현재 이미지 인덱스 업데이트
    displayImages(); // 업데이트된 이미지 표시
  }
}

// "만들기" 버튼에 이벤트 리스너 추가
document.getElementById("openModalBtn").addEventListener('click', function () {
  openPostCreationModal(); // "만들기" 버튼 클릭 시 게시물 작성 모달 열기
});

