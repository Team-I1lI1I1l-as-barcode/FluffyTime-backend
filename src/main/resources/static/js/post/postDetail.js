let currentPostId; // 현재 게시물의 ID를 저장하는 변수
let currentImageIndex = 0;
let imageUrls = []; // 이미지 URL 배열을 저장
let currentBookmarkId = null;  // 북마크 ID를 저장할 변수
let tagsSet = new Set();

// 사용자 프로필 정보 로드
async function loadPostData(postId) {
  console.log(`게시물 데이터 로드 시작: ${postId}`);
  try {
    const response = await fetch(`/api/posts/detail/${postId}`);
    console.log('API 호출 성공');

    if (!response.ok) {
      console.error('서버 응답 상태:', response.status);
      throw new Error('서버 응답이 올바르지 않습니다: ' + response.statusText);
    }

    const postData = await response.json();
    console.log('API로부터 받은 데이터:', postData);

    if (!postData || !postData.content) {
      console.error('postData가 비어 있거나 유효하지 않습니다.');
      throw new Error('게시물 데이터가 올바르지 않습니다.');
    }

    currentPostId = postId;

    // 게시물 내용을 화면에 설정
    document.getElementById('postContent').innerText = postData.content;

    // 사용자 정보 설정
    const nicknameElement = document.getElementById('nicknameDisplay');
    const profileImageElement = document.getElementById('profileImage');
    const petNameElement = document.getElementById('petNameDisplay');
    const petSexElement = document.getElementById('petSexDisplay');
    const petAgeElement = document.getElementById('petAgeDisplay');
    const tagList = document.getElementById('tagList');

    // 닉네임 설정
    nicknameElement.textContent = postData.nickname || '닉네임 없음';

    // 프로필 이미지 설정 (없으면 기본 이미지 사용)
    profileImageElement.src = postData.profileImageurl || '/image/profile/profile.png';

    // 펫 정보 설정 (없으면 공백으로 설정)
    petNameElement.textContent = postData.petName || '';
    petSexElement.textContent = postData.petSex || '';
    petAgeElement.textContent = postData.petAge !== null ? postData.petAge : '';

    // 이미지 URL 배열을 업데이트
    imageUrls = postData.imageUrls || []; // postData.imageUrls가 undefined일 경우 빈 배열로 초기화

    console.log(imageUrls);

    // 이미지 컨테이너 업데이트 및 이미지 추가
    updateImageContainer('imageContainer', imageUrls);

    // 이미지가 한 장일 경우 슬라이드 버튼을 숨김
    toggleSlideButtons(imageUrls.length, 'prevButton', 'nextButton');

    // 태그 보여주기
    if(postData.tags.length === 0) {
      const noneTagElement = document.createElement('span');
      noneTagElement.className = 'noneTag';
      noneTagElement.innerText="태그 없음"
      tagList.appendChild(noneTagElement); // 태그 리스트에 추가
    } else {
      tagsSet = new Set(postData.tags)
      displayTagList()
    }

    function displayTagList() {
      tagList.innerHTML = ''; // 기존 태그 리스트 초기화
      tagsSet.forEach(tag => {
        const tagElement = document.createElement('span');
        tagElement.className = 'tag';

        const tagText = document.createElement('span');
        tagText.className = 'tag-text';
        tagText.textContent = `#${tag}`;

        tagElement.appendChild(tagText); // 태그 텍스트 추가
        tagList.appendChild(tagElement); // 태그 리스트에 추가
      });
    }

    // 댓글 기능 상태에 따라 댓글 섹션과 댓글 작성 폼을 설정
    const commentSection = document.getElementById('comment-list');
    const commentForm = document.querySelector('.comment-form');
    const commentToggleButton = document.querySelector('.dropdown-menu a.comment-toggle');

    if (postData.commentsDisabled) {
      commentSection.style.display = 'none';
      commentForm.style.display = 'none'; // 댓글 작성 폼 숨기기 추가
      commentToggleButton.textContent = '댓글 기능 설정';
    } else {
      commentSection.style.display = 'block';
      commentForm.style.display = 'flex'; // 댓글 작성 폼 보이기 추가
      commentToggleButton.textContent = '댓글 기능 해제';
    }
    console.log(`초기화 후 comment section display 상태: ${commentSection.style.display}`);

  } catch (error) {
    console.error('게시물 데이터 로드 중 오류 발생:', error.message);
    alert('게시물 데이터를 로드하는 중 오류가 발생했습니다. 페이지를 새로고침 해주세요.');
  }
}

// 이미지 컨테이너를 업데이트하는 함수
function updateImageContainer(containerId, urls) {
  const container = document.getElementById(containerId); // 컨테이너 요소를 가져옴
  container.innerHTML = ''; // 기존의 이미지를 초기화

  urls.forEach((imageObj, index) => { // 각 이미지 URL에 대해 반복
    const img = document.createElement('img'); // 새로운 이미지 요소 생성
    img.src = imageObj.filepath; // 이미지 경로를 설정
    img.alt = `image ${index + 1}`; // 이미지의 alt 속성을 설정
    img.style.display = index === 0 ? 'block' : 'none'; // 첫 번째 이미지만 보이도록 설정
    img.className = index === 0 ? 'active' : ''; // 첫 번째 이미지를 활성화 상태로 설정
    img.style.width = '100%'; // 이미지가 컨테이너에 꽉 차도록 설정
    img.style.height = '100%'; // 이미지가 컨테이너에 꽉 차도록 설정
    img.style.objectFit = 'cover'; // 이미지를 컨테이너에 맞게 자름
    img.onerror = () => { // 이미지 로드 실패 시 처리
      console.error('이미지 로드 실패:', imageObj.filepath);
      img.parentElement.removeChild(img); // 이미지 요소를 제거
    };
    container.appendChild(img); // 이미지 요소를 컨테이너에 추가
  });
}

// 현재 인덱스에 해당하는 이미지를 표시하는 함수
function showImage(index, containerId) {
  console.log(`이미지 표시: ${index}`);
  const images = document.querySelectorAll(`#${containerId} img`); // 컨테이너 내의 모든 이미지 요소를 가져옴
  images.forEach((img, idx) => {
    img.style.display = idx === index ? 'block' : 'none'; // 현재 인덱스의 이미지만 표시
    img.className = idx === index ? 'active' : ''; // 활성화된 이미지에 클래스를 추가
  });
}

// 이전 이미지를 표시하는 함수
function prevImage(event) {
  event.preventDefault(); // 기본 이벤트 동작을 방지
  console.log('이전 이미지 표시');
  if (imageUrls.length > 1) { // 이미지가 여러 장일 때만 작동
    // 현재 인덱스가 0이면 마지막 이미지로, 아니면 이전 이미지로 이동
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1 : currentImageIndex - 1;
    showImage(currentImageIndex, 'imageContainer'); // 이미지를 업데이트
  }
}

// 다음 이미지를 표시하는 함수
function nextImage(event) {
  event.preventDefault(); // 기본 이벤트 동작을 방지
  console.log('다음 이미지 표시');
  if (imageUrls.length > 1) { // 이미지가 여러 장일 때만 작동
    // 현재 인덱스가 마지막이면 첫 번째 이미지로, 아니면 다음 이미지로 이동
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0 : currentImageIndex + 1;
    showImage(currentImageIndex, 'imageContainer'); // 이미지를 업데이트
  }
}

// 슬라이드 버튼을 표시하거나 숨기는 함수
function toggleSlideButtons(length, prevBtnId, nextBtnId) {
  const displayValue = length > 1 ? 'block' : 'none'; // 이미지가 여러 장이면 버튼을 보임
  document.getElementById(prevBtnId).style.display = displayValue; // 이전 버튼
  document.getElementById(nextBtnId).style.display = displayValue; // 다음 버튼
}

// 수정 모달을 여는 함수
function openEditModal() {
  console.log('수정 모달 열기');
  document.getElementById('editModal').style.display = 'flex'; // 수정 모달을 보이게 설정
  document.getElementById('editContent').value = document.getElementById('postContent').innerText; // 현재 게시물 내용을 수정 필드에 설정

  // 이미지 인덱스를 초기화
  currentImageIndex = 0;

  // 이미지 컨테이너를 업데이트
  updateImageContainer('editImagePreviewContainer', imageUrls);

  // 슬라이드 버튼을 토글
  toggleSlideButtons(imageUrls.length, 'prevEditButton', 'nextEditButton');

  toggleDropdownMenu(); // 드롭다운 메뉴를 닫음
}

// 수정 모달을 닫는 함수
function closeEditModal() {
  console.log('수정 모달 닫기');
  document.getElementById('editModal').style.display = 'none'; // 수정 모달을 숨김
}

// 게시물을 수정하는 함수
async function submitEdit() {
  console.log('게시물 수정 시작');
  const editedContent = document.getElementById('editContent').value; // 수정된 게시물 내용을 가져옴

  // 수정된 게시물 데이터를 FormData 객체에 추가
  const formData = new FormData();
  formData.append('post', new Blob([JSON.stringify({ content: editedContent })], { type: 'application/json' }));

  try {
    // 서버에 수정된 게시물 데이터를 전송
    const response = await fetch(`/api/posts/edit/${currentPostId}`, {
      method: 'POST',
      body: formData,
      credentials: 'include'  // 인증 정보를 포함하여 요청을 보냄
    });

    if (!response.ok) { // 응답 상태가 정상인지 확인
      const errorData = await response.json();
      throw new Error(errorData.message || '게시물 수정 실패');
    }

    console.log('게시물 수정 완료');

    // 화면에 수정된 게시물 내용을 업데이트
    document.getElementById('postContent').innerText = editedContent;

    alert('게시물이 수정되었습니다.'); // 성공 메시지 표시
    closeEditModal(); // 수정 모달을 닫음
  } catch (error) {
    console.error('게시물 수정 중 오류 발생:', error);
    alert(error.message || '게시물 수정에 실패했습니다.'); // 오류 메시지 표시
  }
}

// 게시물을 삭제하는 함수
async function deletePost() {
  console.log('게시물 삭제 시작');
  if (currentPostId) { // 현재 게시물 ID가 있는지 확인
    try {
      // 서버에 게시물 삭제 요청을 보냄
      const response = await fetch(`/api/posts/delete/${currentPostId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) { // 응답 상태가 정상인지 확인
        const errorData = await response.json();
        throw new Error(errorData.message || '게시물 삭제 실패');
      }

      console.log('게시물 삭제 완료');
      alert('게시물이 삭제되었습니다.'); // 성공 메시지 표시
      window.location.href = '/'; // 메인 페이지로 이동
    } catch (error) {
      console.error('게시물 삭제 중 오류 발생:', error);
      alert(error.message || '게시물 삭제에 실패했습니다.'); // 오류 메시지 표시
    }
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.'); // 게시물 ID가 없을 경우 오류 메시지 표시
  }
}

// 이전 수정 이미지를 표시하는 함수
function prevEditImage(event) {
  event.preventDefault(); // 기본 이벤트 동작을 방지
  console.log('이전 수정 이미지 표시');
  if (imageUrls.length > 1) { // 이미지가 여러 장일 때만 작동
    currentImageIndex = (currentImageIndex === 0) ? imageUrls.length - 1 : currentImageIndex - 1; // 이전 이미지로 이동
    showImage(currentImageIndex, 'editImagePreviewContainer'); // 이미지를 업데이트
  }
}

// 다음 수정 이미지를 표시하는 함수
function nextEditImage(event) {
  event.preventDefault(); // 기본 이벤트 동작을 방지
  console.log('다음 수정 이미지 표시');
  if (imageUrls.length > 1) { // 이미지가 여러 장일 때만 작동
    currentImageIndex = (currentImageIndex === imageUrls.length - 1) ? 0 : currentImageIndex + 1; // 다음 이미지로 이동
    showImage(currentImageIndex, 'editImagePreviewContainer'); // 이미지를 업데이트
  }
}

// 모달을 여는 함수
function openPostDetailModal() {
  console.log('모달 열기');
  document.getElementById('postModal').style.display = 'flex'; // 모달을 보이게 설정
}

// 모달을 닫는 함수
function closePostDetailModal() {
  console.log('모달 닫기');
  document.getElementById('postModal').style.display = 'none'; // 모달을 숨김
  window.history.back(); // 이전 페이지로 이동
}

// 드롭다운 메뉴를 토글하는 함수
function toggleDropdownMenu() {
  console.log('드롭다운 메뉴 토글');
  const dropdownMenu = document.getElementById('dropdownMenu');
  dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block'; // 드롭다운 메뉴를 보이거나 숨김
}

// 링크를 클립보드에 복사하는 함수
function copyLinkToClipboard() {
  const url = window.location.href; // 현재 페이지의 URL을 가져옴

  navigator.clipboard.writeText(url)
  .then(() => {
    alert('링크가 클립보드에 복사되었습니다.'); // 성공 메시지 표시
  })
  .catch(err => {
    console.error('링크 복사 중 오류 발생:', err);
    alert('링크 복사에 실패했습니다.'); // 오류 메시지 표시
  });
  toggleDropdownMenu();
}

// 페이지 로드 시 댓글 상태를 확인하여 초기 설정
document.addEventListener('DOMContentLoaded', async () => {
  console.log('페이지 로드 완료');

  const path = window.location.pathname;
  const pathSegments = path.split('/');
  const postId = pathSegments[pathSegments.length - 1];

  if (postId && !isNaN(postId)) {
    currentPostId = postId; // currentPostId를 설정
    loadPostData(postId); // 게시물 데이터를 로드
    openPostDetailModal(); // 모달을 열기
    initializeBookmarkState(postId); // 북마크 상태 확인 및 초기화

    const isAuthor = await checkIfUserIsAuthor(postId);

    if (isAuthor) {
      // 작성자인 경우 숨겨진 메뉴 항목을 표시
      document.querySelector('.delete').style.display = 'block';
      document.querySelector('.edit').style.display = 'block';
      document.querySelector('.like-hide').style.display = 'block';
      document.querySelector('.comment-toggle').style.display = 'block';
      document.querySelector('.report').style.display = 'none'; // 작성자인 경우 신고 숨김
    } else {
      document.querySelector('.report').style.display = 'block'; // 작성자가 아닌 경우 신고 표시
    }

    // 댓글/답글 버튼 클릭 시
    const commentButton = document.getElementById('commentButton');
    commentButton.onclick = function () {
      postComment(postId);
    };
  } else {
    console.error('URL에서 postId를 찾을 수 없습니다.');
  }
});

// 북마크 상태 업데이트 함수
async function initializeBookmarkState(postId) {
  try {
    // 북마크 상태 확인 요청
    const response = await fetch(`/api/bookmarks/check?postId=${postId}`, {
      method: 'GET',
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error('북마크 상태를 확인할 수 없습니다.');
    }

    const isBookmarked = await response.json(); // 서버에서 Boolean 값을 반환

    // 북마크 상태에 따라 아이콘 설정
    const bookmarkIcon = document.getElementById('bookmarkIcon');
    bookmarkIcon.src = isBookmarked
        ? "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark_check/default/24px.svg"
        : "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark/default/24px.svg";

    // 북마크 상태에 따라 currentBookmarkId 관리
    if (isBookmarked) {
      // 서버에서 북마크 ID를 가져오는 추가 API 호출
      const bookmarkResponse = await fetch(`/api/bookmarks/post/${postId}`, {
        method: 'GET',
        credentials: 'include'
      });

      if (!bookmarkResponse.ok) {
        throw new Error('북마크 ID를 가져올 수 없습니다.');
      }

      const bookmarkData = await bookmarkResponse.json();
      if (bookmarkData && bookmarkData.length > 0) {
        currentBookmarkId = bookmarkData[0].bookmarkId; // 첫 번째 북마크의 ID를 저장
      }
    } else {
      currentBookmarkId = null; // 북마크되지 않았을 경우 null로 초기화
    }
  } catch (error) {
    console.error('북마크 상태 초기화 중 오류 발생:', error.message);
  }
}

// 북마크 토글 함수
async function toggleBookmark() {
  try {
    const isBookmarked = currentBookmarkId !== null;
    const bookmarkIcon = document.getElementById('bookmarkIcon');

    if (isBookmarked) {
      // 이미 북마크된 경우, 북마크 삭제 요청
      const response = await fetch(`/api/bookmarks/delete/${currentBookmarkId}`, {
        method: 'POST',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('북마크 삭제 실패: ' + response.statusText);
      }

      currentBookmarkId = null; // 북마크 ID 초기화
      bookmarkIcon.src = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark/default/24px.svg";
    } else {
      // 북마크되지 않은 경우, 북마크 추가 요청
      const response = await fetch(`/api/bookmarks/reg`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ postId: currentPostId })
      });

      if (!response.ok) {
        throw new Error('북마크 추가 실패: ' + response.statusText);
      }

      const newBookmarkData = await response.json();
      currentBookmarkId = newBookmarkData.bookmarkId; // 새로운 북마크 ID 저장
      bookmarkIcon.src = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/bookmark_check/default/24px.svg";
    }
  } catch (error) {
    console.error('북마크 토글 중 오류 발생:', error.message);
  }
}

// 댓글 기능 토글 함수
async function toggleComments() {
  try {
    const response = await fetch(`/api/posts/toggle-comments/${currentPostId}`, {
      method: 'POST',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('댓글 기능을 토글하는 중 문제가 발생했습니다.');
    }

    // 댓글 기능 상태를 확인하여 UI를 업데이트
    const commentSection = document.getElementById('comment-list');
    const commentForm = document.querySelector('.comment-form'); // 댓글 작성 폼
    const commentToggleButtons = document.querySelectorAll('.dropdown-menu a.comment-toggle');

    // 현재 상태를 체크하여 토글
    commentToggleButtons.forEach(button => {
      if (commentSection.style.display === 'none' && commentForm.style.display === 'none') {
        commentSection.style.display = 'block';
        commentForm.style.display = 'block'; // 댓글 작성 폼 보이기
        if (button.innerText.trim() === '댓글 기능 설정') {
          button.innerText = '댓글 기능 해제';
        }
      } else {
        commentSection.style.display = 'none';
        commentForm.style.display = 'none'; // 댓글 작성 폼 숨기기
        if (button.innerText.trim() === '댓글 기능 해제') {
          button.innerText = '댓글 기능 설정';
        }
      }
    });

  } catch (error) {
    console.error('댓글 기능 토글 중 오류 발생:', error.message);
    alert('댓글 기능을 토글하는 중 오류가 발생했습니다.');
  }
}

// 작성자 여부 확인 함수
async function checkIfUserIsAuthor(postId) {
  try {
    const response = await fetch(`/api/posts/is-author/${postId}`, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('작성자 확인에 실패했습니다.');
    }

    const data = await response.json();
    return data.isAuthor; // 서버에서 반환된 true/false 값
  } catch (error) {
    console.error('작성자 확인 중 오류 발생:', error);
    return false;
  }
}
