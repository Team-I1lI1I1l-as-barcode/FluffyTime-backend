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
    if (tagsSet.size > 10) {
      alert("태그는 10개까지만 등록 가능합니다.")
      tagsInputElement.value = ""
      return
    }

    let tag = tagsInputElement.value
    tag = tag.trim().replace(/^#/, "")

    if (tag.length > 20) {
      alert("태그 길이는 최대 20자까지만 가능합니다.");
      return;
    }

    if (tagPattern.test(tag)) {
      tagsSet.add(tag);
      tagsInputElement.value = ""
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
    removeBtn.addEventListener("click", (event) => removeTag(event, tag))

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

// 멘션 관련
// 멘션 유형으로 입력 시 사용자 계정 이름 검색 반환
let searchTimeout; // 검색 요청 지연 타이머

async function handleInput() {
  const textarea = document.getElementById('content');
  const cursorPosition = textarea.selectionStart;
  const text = textarea.value.slice(0, cursorPosition);
  const mentionIndex = text.lastIndexOf('@');

  //글자 수 업데이트
  const content = contentElement.value; // 입력된 내용 가져오기
  charCountElement.textContent = `${content.length} / 2200`; // 글자 수 업데이트

  if (mentionIndex !== -1) {
    const mentionText = text.slice(mentionIndex + 1);
    if (mentionText.length > 0) {
      // 이전 검색 요청 취소
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }

      // 검색 요청 지연
      searchTimeout = setTimeout(async () => {
        try {
          const response = await fetch(`/api/search/accounts`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({query: mentionText})
          });
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          const data = await response.json();
          displayMentionSuggestions(data.list); // data.list를 사용하여 배열을 전달
        } catch (error) {
          console.error('Fetch error:', error);
        }
      }, 300); // 300ms 지연 후 검색
    } else {
      hideMentionSuggestions();
    }
  } else {
    hideMentionSuggestions();
  }

  formatMentions(); // 스타일 적용
}

// 목록 보여줌
function displayMentionSuggestions(users) {
  const suggestionsBox = document.getElementById('mentionSuggestions');
  suggestionsBox.innerHTML = '';
  users.forEach(user => {
    const suggestion = document.createElement('div');
    suggestion.classList.add('mention-suggestion');
    suggestion.textContent = `@${user.nickName}`;
    suggestion.addEventListener('click', () => selectMention(user.nickName));
    suggestionsBox.appendChild(suggestion);
  });
  suggestionsBox.style.display = 'block';
}

// 목록 토글
function hideMentionSuggestions() {
  document.getElementById('mentionSuggestions').style.display = 'none';
}

// 목록에서 유저 선택
function selectMention(nickname) {
  const textarea = document.getElementById('content');
  const cursorPosition = textarea.selectionStart;
  const text = textarea.value;
  const mentionIndex = text.lastIndexOf('@', cursorPosition - 1);
  textarea.value = text.slice(0, mentionIndex) + `@${nickname} `;
  hideMentionSuggestions();
  textarea.focus();
  formatMentions(); // 스타일 적용
}

// 멘션 스타일 적용
function formatMentions() {
  const textarea = document.getElementById('content');
  let content = textarea.value;

  // 멘션된 닉네임을 찾아서 스타일 적용
  const formattedContent = content.replace(/@(\w+)/g,
      '<span class="mention-text">@$1</span>');

  document.getElementById(
      'contentPreview').innerHTML = formattedContent.replace(/\n/g, '<br>');
}

// 멘션 데이터 추출
function extractMentions(text) {
  const mentionPattern = /@(\w+)/g;
  const mentions = [];
  let match;
  while ((match = mentionPattern.exec(text)) !== null) {
    mentions.push(match[1]);
  }
  return mentions;
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

// 이미지 및 동영상 미리보기 및 파일 처리 함수
function previewImages(event) {
  const files = event.target.files; // 사용자가 선택한 파일들
  imagePreviewContainer.innerHTML = ''; // 기존 미리보기를 초기화
  imagesArray = []; // 이미지 배열을 초기화

  // 파일 개수 제한 확인
  if (files.length > 10) {
    alert('이미지와 동영상은 최대 10개까지 업로드할 수 있습니다.');
    event.target.value = ''; // 파일 입력 초기화
    return;
  }

  // 선택한 파일들을 반복 처리하여 미리보기와 배열에 저장
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const reader = new FileReader(); // 파일을 읽기 위한 FileReader 객체 생성

    reader.onload = function (e) { // 파일 읽기가 완료되면 실행
      if (file.type.startsWith('image/')) {
        imagesArray.push({
          file: file, // 파일 객체 저장
          url: e.target.result, // 파일의 Data URL 저장 (미리보기에 사용)
          type: 'image', // 파일 타입을 저장
        });
      } else if (file.type.startsWith('video/')) {
        imagesArray.push({
          file: file, // 파일 객체 저장
          url: e.target.result, // 파일의 Data URL 저장 (미리보기에 사용)
          type: 'video', // 파일 타입을 저장
        });
      }

      // 첫 번째 파일이면 미리보기 화면에 표시
      if (i === 0) {
        displayMedia(imagesArray[0]);
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

// 선택된 미디어(이미지 또는 동영상)를 화면에 표시하는 함수
function displayMedia(media) {
  imagePreviewContainer.innerHTML = ''; // 기존 미리보기 초기화

  if (media.type === 'image') {
    const img = document.createElement('img'); // 이미지 요소 생성
    img.src = media.url;                       // 이미지의 소스 설정
    img.style.objectFit = 'cover';             // 이미지가 컨테이너에 맞도록 설정
    img.style.width = '100%';                  // 이미지의 너비를 100%로 설정
    img.style.height = '100%';                 // 이미지의 높이를 100%로 설정
    img.style.position = 'absolute';           // 이미지의 위치를 절대 위치로 설정
    img.style.top = '0';                       // 이미지의 상단을 컨테이너 상단에 맞춤
    img.style.left = '0';                      // 이미지의 왼쪽을 컨테이너 왼쪽에 맞춤
    imagePreviewContainer.appendChild(img);    // 이미지 요소를 미리보기 컨테이너에 추가
  } else if (media.type === 'video') {
    const video = document.createElement('video'); // 비디오 요소 생성
    video.src = media.url;                          // 비디오의 소스 설정
    video.controls = true;                          // 비디오 컨트롤 표시
    video.style.objectFit = 'cover';                // 비디오가 컨테이너에 맞도록 설정
    video.style.width = '100%';                     // 비디오의 너비를 100%로 설정
    video.style.height = '100%';                    // 비디오의 높이를 100%로 설정
    video.style.position = 'absolute';              // 비디오의 위치를 절대 위치로 설정
    video.style.top = '0';                          // 비디오의 상단을 컨테이너 상단에 맞춤
    video.style.left = '0';                         // 비디오의 왼쪽을 컨테이너 왼쪽에 맞춤
    imagePreviewContainer.appendChild(video);       // 비디오 요소를 미리보기 컨테이너에 추가
  }
}

// 현재 미디어(이미지 또는 동영상)를 화면에 표시하는 함수
function displayImages() {
  if (imagesArray.length > 0) {
    displayMedia(imagesArray[currentImageIndex]);
    prevButton.style.display = imagesArray.length > 1 ? 'block' : 'none'; // 이전 버튼 표시 여부
    nextButton.style.display = imagesArray.length > 1 ? 'block' : 'none'; // 다음 버튼 표시 여부
  }
}

// 이전 미디어 보기 함수
function prevImage(event) {
  event.preventDefault(); // 기본 동작 방지
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === 0 ? imagesArray.length - 1
        : currentImageIndex - 1; // 현재 이미지 인덱스 업데이트
    displayImages(); // 업데이트된 미디어 표시
  }
}

// 다음 미디어 보기 함수
function nextImage(event) {
  event.preventDefault(); // 기본 동작 방지
  if (imagesArray.length > 1) {
    currentImageIndex = currentImageIndex === imagesArray.length - 1 ? 0
        : currentImageIndex + 1; // 현재 이미지 인덱스 업데이트
    displayImages(); // 업데이트된 미디어 표시
  }
}

// 글자 수를 업데이트하는 함수
function updateCharCount() {

}

// 게시물 데이터를 준비하는 함수
function preparePostData(tempId, content, tagsSet, status) {
  const hideLikes = document.getElementById('hideLikes').checked;  // 좋아요 숨김 상태 가져오기
  const disableComments = document.getElementById('disableComments').checked;  // 댓글 기능 해제 상태 가져오기

  return {
    tempId: tempId, // 임시 저장된 게시물의 ID (없으면 null)
    content: content, // 게시물 내용
    tags: Array.from(tagsSet), // 태그 배열
    tempStatus: status, // 게시물 상태 (임시 저장 또는 최종 저장)
    hideLikeCount: hideLikes, // 좋아요 숨김 여부
    commentsDisabled: disableComments // 댓글 기능 해제 여부
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

  const postRequest = preparePostData(currentDraftPostId, content, tagsSet,
      'TEMP'); // 임시 저장 요청 데이터 준비

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

  // 멘션 추출
  const mentions = extractMentions(content);

  const postRequest = preparePostData(currentDraftPostId, content, tagsSet,
      'SAVE'); // 최종 제출 요청 데이터 준비

  const data = await submitPostData('/api/posts/reg', postRequest, imagesArray); // 서버로 게시물 등록 요청 전송
  const postId = data?.data?.postId || data.data; // 응답에서 게시물 ID 가져오기

  // 멘션을 서버로 전송
  if (mentions.length > 0) {
    for (const nickname of mentions) {
      const mentionRequest = {
        mentionedUserNickname: nickname,
        postId: data,
        content: content
      };
      await fetch('/api/mentions/reg', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(mentionRequest)
      });
    }
  }

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

      // 임시 저장된 글이 있으면 날짜를 기준으로 내림차순으로 정렬
      tempPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

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
  displayTagList(); // 태그 리스트 표시

  imagesArray = post.imageUrls.map(image => ({
    url: image.filepath, // 이미지 URL을 설정
    type: image.mimetype.startsWith('image/') ? 'image' : 'video', // 파일 타입을 이미지 또는 비디오로 설정
    file: null, // 임시 저장된 글에서는 원본 파일 객체가 없으므로 null로 설정
  }));

  displayImages(); // 이미지 및 동영상 미리보기 표시

  document.getElementById('images').disabled = true; // 이미지 입력 비활성화
  shareButton.style.display = 'none'; // 이미지 선택 버튼 숨기기
  dragDropText.style.display = 'none'; // 드래그 앤 드롭 텍스트 숨기기

  document.getElementById('hideLikes').checked = post.hideLikeCount; // 숨김 상태 반영
  document.getElementById('disableComments').checked = post.commentsDisabled; // 댓글 해제 상태 반영

  closeDraftModal(); // 임시 저장 모달 닫기
  openPostCreationModal(); // 게시물 작성 모달 열기
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

