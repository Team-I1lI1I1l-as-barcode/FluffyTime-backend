let postId;

//어느 게시글의 댓글인지
document.addEventListener('DOMContentLoaded', async () => {
  // 현재 URL의 경로를 가져옵니다.
  const path = window.location.pathname;
  // 경로를 '/'로 분리하여 배열로 만듭니다.
  const pathSegments = path.split('/');
  // 배열의 마지막 요소가 postId입니다.
  postId = pathSegments[pathSegments.length - 1];
  await fetchComments(postId); // 페이지 로드 시 postId 2번의 댓글 목록을 가져옴
});

//댓글 조회
async function fetchComments() {
  const response = await fetch(`/api/comments/post/${postId}`);
  if (!response.ok) {
    console.error('댓글 목록 가져오기 실패!', response.status);
    return [];
  }
  const comments = await response.json();
  const commentList = document.getElementById('comment-list');
  commentList.innerHTML = ''; // 기존 댓글 목록 초기화
  for (const comment of comments) {

    // 댓글 아이디
    const commentDiv = document.createElement('div');
    commentDiv.className = 'comment';
    commentDiv.dataset.id = comment.commentId;

    // 프로필 이미지
    const profileImg = document.createElement('img');
    profileImg.src = comment.profileImageurl || '/image/profile/profile.png'; // 프로필 이미지 가져오기
    profileImg.className = 'profile-img';

    // 프로필 사진 클릭 시 유저페이지로 이동
    profileImg.addEventListener('click', () => {
      const nickname = comment.nickname;
      window.location.href = `/userpages/${nickname}`;
    })

    //닉네임
    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = comment.nickname;

    //댓글 내용
    const contentSpan = document.createElement('span');
    contentSpan.className = 'text';
    contentSpan.innerHTML = highlightMentions(comment.content);

    // 좋아요 버튼 추가
    const likeButton = document.createElement('span');
    likeButton.className = 'like-button material-icons';
    likeButton.innerHTML = comment.liked ? 'favorite' : 'favorite_border'; // 좋아요 상태에 따라 버튼 모양 설정

    // 좋아요 상태에 따른 클래스 적용
    if (comment.liked) {
      likeButton.classList.add('liked');
    } else {
      likeButton.classList.remove('liked');
    }

    // 좋아요 개수
    const likeCountSpan = document.createElement('span');
    likeCountSpan.className = 'like-count';
    likeCountSpan.textContent = `${comment.likeCount}`;

    // 좋아요 버튼 클릭 이벤트
    likeButton.onclick = () => {
      toggleLikeComment(comment.commentId, likeButton, likeCountSpan);
    };

    //좋아요 목록 모달 보여줌 (개수 클릭 시)
    likeCountSpan.onclick = async = async () => {
      const users = await fetchUsersWhoLikedComment(comment.commentId);
      showLikeUserModalComment(users);
    }

    // 좋아요 버튼/개수 묶음
    const likedDiv = document.createElement('div');
    likedDiv.className = 'liked-box';
    likedDiv.appendChild(likeButton);
    likedDiv.appendChild(likeCountSpan);

    // nicknameSpan과 contentSpan을 한 번 더 묶음
    const nicknameContentDiv = document.createElement('div');
    nicknameContentDiv.className = 'nickname-content';
    nicknameContentDiv.appendChild(nicknameSpan);
    nicknameContentDiv.appendChild(contentSpan);

    // 프로필 이미지와 닉네임/댓글 내용을 하나의 div로 묶음
    const profileContentDiv = document.createElement('div');
    profileContentDiv.className = 'profile-content';
    profileContentDiv.appendChild(profileImg);
    profileContentDiv.appendChild(nicknameContentDiv);
    profileContentDiv.appendChild(likedDiv);

    // 버튼들 묶음
    const editDeleteButtonsDiv = document.createElement('div');
    editDeleteButtonsDiv.className = 'edit-delete-buttons';

    // 댓글 수정 및 삭제 버튼
    if (comment.author) {
      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEdit(comment.commentId, comment.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteComment(comment.commentId);

      editDeleteButtonsDiv.appendChild(editButton);
      editDeleteButtonsDiv.appendChild(deleteButton);
    }

    // 답글 버튼
    const replyButton = document.createElement('button');
    replyButton.textContent = '답글';
    replyButton.onclick = () => toggleReplyInput(comment.commentId);

    editDeleteButtonsDiv.appendChild(replyButton);

    // 댓글 내용을 commentDiv에 추가
    commentDiv.appendChild(profileContentDiv);
    commentDiv.appendChild(editDeleteButtonsDiv);

    // 답글 목록 추가
    const repliesDiv = document.createElement('div');
    repliesDiv.className = 'replies';
    fetchReplies(comment.commentId, repliesDiv);

    commentDiv.appendChild(repliesDiv);
    commentList.appendChild(commentDiv);
  }
}

// 답글 입력 칸 토글
function toggleReplyInput(commentId) {
  let replyDiv = document.querySelector(
      `.reply-section[data-id='${commentId}']`);
  if (replyDiv) {
    replyDiv.remove();
  } else {
    replyDiv = document.createElement('div');
    replyDiv.className = 'reply-section';
    replyDiv.dataset.id = commentId;

    const replyTextarea = document.createElement('textarea');
    replyTextarea.id = `reply-textarea-${commentId}`;
    replyTextarea.placeholder = '답글 내용을 입력하세요...';
    replyTextarea.oninput = () => handleReplyInput(commentId);

    const replyContentPreview = document.createElement('div');
    replyContentPreview.id = `contentPreview-reply-${commentId}`;
    replyContentPreview.className = 'contentPreview-reply';

    const replyButton = document.createElement('button');
    replyButton.textContent = '답글 달기';
    replyButton.onclick = () => postReply(commentId, replyTextarea.value);

    // 버튼을 감싸는 컨테이너 생성
    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'button-container';
    buttonContainer.appendChild(replyButton);

    replyDiv.appendChild(replyTextarea);
    replyDiv.appendChild(replyContentPreview);
    replyDiv.appendChild(buttonContainer);

    const commentDiv = document.querySelector(
        `.comment[data-id='${commentId}']`);
    commentDiv.appendChild(replyDiv);

    // 스크롤을 하단으로 이동
    commentDiv.scrollIntoView({behavior: 'smooth', block: 'end'});
  }
}

//답글 조회
async function fetchReplies(commentId, replyDiv) {
  const response = await fetch(`/api/replies/comment/${commentId}`);
  if (!response.ok) {
    console.error('답글 목록 가져오기 실패!', response.status);
    return;
  }
  const replies = await response.json();

  // 처음엔 최대 2개의 답글만 표시
  const initialReplies = replies.slice(0, 2);

  const loadMoreButton = document.createElement('span');
  loadMoreButton.textContent = '더보기';
  loadMoreButton.className = 'load-more-button';

  // 답글 렌더링
  function renderReply(reply) {
    const replyElement = document.createElement('div');
    replyElement.className = 'reply';
    replyElement.dataset.id = reply.replyId;

    // 프로필 이미지
    const profileImg = document.createElement('img');
    profileImg.src = reply.profileImageurl || '/image/profile/profile.png'; // 프로필 이미지 가져오기
    profileImg.className = 'profile-img';

    // 프로필 사진 클릭 시 유저페이지로 이동
    profileImg.addEventListener('click', () => {
      const nickname = reply.nickname;
      window.location.href = `/userpages/${nickname}`;
    })

    // 닉네임 및 답글 내용
    const nicknameSpan = document.createElement('span');
    nicknameSpan.className = 'nickname';
    nicknameSpan.textContent = reply.nickname;

    const contentSpan = document.createElement('span');
    contentSpan.className = 'text';
    contentSpan.innerHTML = highlightMentions(reply.content);

    // 좋아요 버튼 추가
    const likeButton = document.createElement('span');
    likeButton.className = 'like-button material-icons';
    likeButton.innerHTML = reply.liked ? 'favorite' : 'favorite_border'; // 좋아요 상태에 따라 버튼 모양 설정

    // 좋아요 상태에 따른 클래스 적용
    if (reply.liked) {
      likeButton.classList.add('liked');
    } else {
      likeButton.classList.remove('liked');
    }

    // 좋아요 개수
    const likeCountSpan = document.createElement('span');
    likeCountSpan.className = 'like-count';
    likeCountSpan.textContent = `${reply.likeCount}`;

    // 좋아요 버튼 클릭 이벤트
    likeButton.onclick = () => {
      toggleLikeReply(reply.replyId, likeButton, likeCountSpan);
    };

    //좋아요 목록 모달 보여줌 (개수 클릭 시)
    likeCountSpan.onclick = async = async () => {
      const users = await fetchUsersWhoLikedReply(reply.replyId);
      showLikeUserModalReply(users);
    }

    // 좋아요 버튼/개수 묶음
    const likedDiv = document.createElement('div');
    likedDiv.className = 'liked-box';
    likedDiv.appendChild(likeButton);
    likedDiv.appendChild(likeCountSpan);

    // nicknameSpan과 contentSpan을 한 번 더 묶음
    const nicknameContentDiv = document.createElement('div');
    nicknameContentDiv.className = 'nickname-content';
    nicknameContentDiv.appendChild(nicknameSpan);
    nicknameContentDiv.appendChild(contentSpan);

    // 프로필 이미지와 닉네임/답글 내용을 하나의 div로 묶음
    const profileContentDiv = document.createElement('div');
    profileContentDiv.className = 'profile-content';
    profileContentDiv.appendChild(profileImg);
    profileContentDiv.appendChild(nicknameContentDiv);
    profileContentDiv.appendChild(likedDiv);

    const editDeleteButtonsDiv = document.createElement('div');
    editDeleteButtonsDiv.className = 'edit-delete-buttons-reply';

    if (reply.author) {

      const editButton = document.createElement('button');
      editButton.textContent = '수정';
      editButton.onclick = () => showEditReply(reply.replyId, reply.content);

      const deleteButton = document.createElement('button');
      deleteButton.textContent = '삭제';
      deleteButton.onclick = () => deleteReply(reply.replyId, commentId);

      editDeleteButtonsDiv.appendChild(editButton);
      editDeleteButtonsDiv.appendChild(deleteButton);
    }

    replyElement.appendChild(profileContentDiv);
    replyDiv.appendChild(replyElement);
    replyElement.appendChild(editDeleteButtonsDiv);
  }

  // 초기 답글 렌더링 및 더보기 기능
  initialReplies.forEach(renderReply);

  if (replies.length > 2) {
    loadMoreButton.addEventListener('click', () => {
      const remainingReplies = replies.slice(2);

      if (loadMoreButton.textContent === '더보기') {
        remainingReplies.forEach(renderReply);
        loadMoreButton.textContent = '접기';
      } else {
        const replyElements = replyDiv.querySelectorAll('.reply');
        replyElements.forEach((replyElement, index) => {
          if (index >= 2) {
            replyElement.remove();
          }
        });
        loadMoreButton.textContent = '더보기';
      }
      replyDiv.appendChild(loadMoreButton);
    });

    replyDiv.appendChild(loadMoreButton);
  }
}

// 답글 등록
async function postReply(commentId, content) {
  try {
    // 멘션 추출
    const mentions = extractMentions(content);

    const response = await fetch('/api/replies/reg', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content, commentId}),
    });

    if (response.ok) {
      console.log('답글 등록 성공!');

      // 멘션을 서버로 전송
      if (mentions.length > 0) {
        const {replyId} = await response.json(); // 서버에서 반환된 replyId 사용
        const mentionRequest = {
          mentions: mentions,
          replyId: replyId,
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

      await fetchComments(postId); // 댓글 목록 갱신
    } else {
      console.error('답글 등록 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 등록 중 예외 발생!', error);
  }
}

// 답글 수정칸 보여주기
function showEditReply(replyId, currentContent) {
  const replyDiv = document.querySelector(`.reply[data-id='${replyId}']`);
  replyDiv.innerHTML = ''; // 기존 내용 지우기

  const editTextarea = document.createElement('textarea');
  editTextarea.value = currentContent;

  const saveButton = document.createElement('button');
  saveButton.className = 'comment-edit-button';
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateReply(replyId, editTextarea.value);

  const cancelButton = document.createElement('button');
  cancelButton.className = 'comment-cancel-button';
  cancelButton.textContent = '취소';
  cancelButton.onclick = () => fetchComments(postId); // 페이지 새로고침으로 수정 취소

  const saveCancleDiv = document.createElement('div');
  saveCancleDiv.className = 'save-cancle-button-box';
  saveCancleDiv.appendChild(saveButton);
  saveCancleDiv.appendChild(cancelButton);

  replyDiv.appendChild(editTextarea);
  replyDiv.appendChild(saveCancleDiv);
}

// 답글 수정
async function updateReply(replyId, newContent) {
  try {
    const response = await fetch(`/api/replies/update/${replyId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content: newContent}),
    });

    if (response.ok) {
      console.log('답글 수정 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('답글 수정 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 수정 중 예외 발생!', error);
  }
}

// 답글 삭제
async function deleteReply(replyId, commentId) {
  try {
    const response = await fetch(`/api/replies/delete/${replyId}`, {
      method: 'DELETE',
    });

    if (response.ok) {
      console.log('답글 삭제 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('답글 삭제 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('답글 삭제 중 예외 발생!', error);
  }
}

//댓글 등록
async function postComment() {
  const content = document.getElementById('comment-content').value;
  const statusMessage = document.getElementById('status-message');

  try {
    const response = await fetch('/api/comments/reg', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content, postId}),
    });

    // 멘션 추출
    const mentions = extractMentions(content);

    const data = await response.json();
    const commentId = data.commentId;  // 서버에서 반환된 commentId 사용

    // 멘션을 서버로 전송
    if (mentions.length > 0) {
      const mentionRequest = {
        mentions: mentions,
        commentId: commentId,
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

    if (response.ok) {
      document.getElementById('comment-content').value = '';
      document.getElementById('contentPreview').innerText = '';  // contentPreview 초기화
      console.log('댓글 등록 성공!');
      console.log('서버 응답: ', data);
      await fetchComments(postId);
    } else {

      console.error('댓글 등록 실패! 상태 코드: ', response.status, '서버 응답: ', data);
    }
  } catch (error) {
    console.error('댓글 등록 중 예외 발생!', error);
  }
}

//수정칸 보여주기
function showEdit(commentId, currentContent) {
  const commentDiv = document.querySelector(`.comment[data-id='${commentId}']`);
  commentDiv.innerHTML = ''; // 기존 내용 지우기

  const editTextarea = document.createElement('textarea');
  editTextarea.value = currentContent;

  const saveButton = document.createElement('button');
  saveButton.className = 'comment-edit-button';
  saveButton.textContent = '수정 완료';
  saveButton.onclick = () => updateComment(commentId, editTextarea.value);

  const cancelButton = document.createElement('button');
  cancelButton.className = 'comment-cancel-button';
  cancelButton.textContent = '취소';
  cancelButton.onclick = () => fetchComments(postId); // 페이지 새로고침으로 수정 취소

  const saveCancleDiv = document.createElement('div');
  saveCancleDiv.className = 'save-cancle-button-box';
  saveCancleDiv.appendChild(saveButton);
  saveCancleDiv.appendChild(cancelButton);

  commentDiv.appendChild(editTextarea);
  commentDiv.appendChild(saveCancleDiv);
}

//댓글 수정
async function updateComment(commentId, newContent) {
  try {
    const response = await fetch(`/api/comments/update/${commentId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content: newContent}),
    });

    if (response.ok) {
      console.log('댓글 수정 성공!');
      await fetchComments(postId); // 댓글 목록 갱신 (postId를 적절히 대체)
    } else {
      console.error('댓글 수정 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('댓글 수정 중 예외 발생!', error);
  }
}

//댓글 삭제
async function deleteComment(commentId) {
  console.log('Deleting comment with Id: ', commentId);
  try {
    const response = await fetch(`/api/comments/delete/${commentId}`, {
      method: 'DELETE',
    });

    if (response.ok) {
      console.log('댓글 삭제 성공!');
      await fetchComments(postId); // 댓글 목록 갱신
    } else {
      console.error('댓글 삭제 실패! 상태 코드: ', response.status);
    }
  } catch (error) {
    console.error('댓글 삭제 중 예외 발생!', error);
  }
}

// 멘션 기능
// 멘션 유형으로 입력 시 사용자 계정 이름 검색 반환
let searchTimeout; // 검색 요청 지연 타이머

async function handleInput() {
  const textarea = document.getElementById('comment-content');
  const cursorPosition = textarea.selectionStart;
  const text = textarea.value.slice(0, cursorPosition);
  const mentionIndex = text.lastIndexOf('@');

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
function displayMentionSuggestions(users, commmentId) {
  const suggestionsBox = document.getElementById('mentionSuggestions');
  suggestionsBox.innerHTML = '';
  users.forEach(user => {
    const suggestion = document.createElement('div');
    suggestion.classList.add('mention-suggestion');
    suggestion.textContent = `@${user.nickName}`;
    suggestion.addEventListener('click',
        () => selectMention(user.nickName, commmentId));
    suggestionsBox.appendChild(suggestion);
  });
  suggestionsBox.style.display = 'block';
}

// 목록 토글
function hideMentionSuggestions() {
  document.getElementById('mentionSuggestions').style.display = 'none';
}

// 목록에서 유저 선택
function selectMention(nickname, commentId) {
  let textarea;
  if (commentId) {
    textarea = document.getElementById(`reply-textarea-${commentId}`);
  } else {
    textarea = document.getElementById('comment-content');
  }

  const cursorPosition = textarea.selectionStart;
  const text = textarea.value;
  const mentionIndex = text.lastIndexOf('@', cursorPosition - 1);
  textarea.value = text.slice(0, mentionIndex) + `@${nickname} `;
  hideMentionSuggestions();
  textarea.focus();
  formatMentions(); // 댓글/답글 스타일 적용
  formatReplyMentions(commentId);
}

// 멘션 스타일 적용
function formatMentions() {
  const textarea = document.getElementById('comment-content');
  let content = textarea.value;

  // 멘션된 닉네임을 찾아서 스타일 적용
  const formattedContent = content.replace(/@(\w+)/g,
      '<span class="mention-text">@$1</span>');

  document.getElementById(
      'contentPreview').innerHTML = formattedContent.replace(/\n/g, '<br>');
}

function formatReplyMentions(commentId) {
  const replyTextarea = document.getElementById(`reply-textarea-${commentId}`);
  let content = replyTextarea.value;

  // 멘션된 닉네임을 찾아서 스타일 적용
  const formattedContent = content.replace(/@(\w+)/g,
      '<span class="mention-text">@$1</span>');

  const previewElement = document.getElementById(
      `contentPreview-reply-${commentId}`);
  if (previewElement) {
    previewElement.innerHTML = formattedContent.replace(/\n/g, '<br>');
  }
}

// 멘션 데이터 추출
function extractMentions(text) {
  const mentionPattern = /@(\w+)/g;
  const mentions = new Set();
  let match;
  while ((match = mentionPattern.exec(text)) !== null) {
    mentions.add(match[1]);
  }
  return Array.from(mentions);
}

function handleReplyInput(commentId) {
  const replyTextarea = document.getElementById(`reply-textarea-${commentId}`);
  const cursorPosition = replyTextarea.selectionStart;
  const text = replyTextarea.value.slice(0, cursorPosition);
  const mentionIndex = text.lastIndexOf('@');

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
          displayMentionSuggestions(data.list, commentId); // data.list와 commentId를 전달
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

  formatReplyMentions(commentId); // 스타일 적용
}

// 멘션 하이라이트 함수
function highlightMentions(content) {
  // '@nickname' 패턴을 찾아서 <span> 태그로 감싸기
  return content.replace(/(@\w+)/g,
      '<span style="color: #0078e8; font-weight: 500;">$1</span>');
}