// DOM 요소 선택 함수
const getElement = id => document.getElementById(id);

const checkUsernameBtn = getElement("check_username_Btn"); // 중복확인 버튼
const nickname = getElement("nickname");
const email = getElement("email");
const username = getElement("username"); // 유저명 (중복확인)
const intro = getElement("intro");
const petName = getElement("pet_name");
const petSex = getElement("pet_sex");
const petAge = getElement("pet_age");
const petCategory = getElement("pet_category");
const publicStatus = getElement("public_status");
const submitBtn = getElement("submit");
const deleteAccountBtn = getElement("delete_account");
const imageBtn = getElement('imageBtn'); // 사진변경 버튼
const img = getElement('img'); // 이미지 미리보기

// 모달 관련 요소
const profileImageUpload = getElement('profile_image_upload'); // 사진 업로드 a태그
const profileImageDelete = getElement('profile_image_delete'); // 현재 사진 삭제 a태그
const profileModal = document.getElementById('profile-modal');
const profileOverlay = document.getElementById('profile-modal-overlay');
const closeModalButtons = document.querySelectorAll('#profile_image_cancel');

let originalIntroValue = ""; // intro 필드의 원래 값
let originalPetNameValue = ""; // pet_name 필드의 원래 값

// 반려동물 나이 옵션 설정 함수
function setPetAgeOptions(maxAge) {
  const noneOption = document.createElement("option");
  noneOption.value = "0";
  noneOption.text = "==선택==";
  noneOption.selected = true;
  petAge.add(noneOption);

  for (let i = 1; i <= maxAge; i++) {
    const option = document.createElement("option");
    option.value = i;
    option.text = `${i} 살`;
    petAge.add(option);
  }
}

// 반려동물 이름 존재 여부에 따라 성별/나이/카테고리 선택 활성화
function petOptionsStatus(status) {
  petSex.disabled = !status;
  petAge.disabled = !status;
  petCategory.disabled = !status;
}

// API 요청 함수 (요청 헤더가 X)
function fetchProfile(method, callback, url) {
  fetch(url, {
    method: method,
    headers: {'Content-Type': 'application/json'}
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.error(errorData.message);
        if (errorData.code === "GE-005") {
          createProfile(window.location.pathname.split('/').pop());
        } else {
          alert('Error: ' + errorData.message);
          window.location.href = "/";
        }
        throw new Error(errorData.message);
      });
    }
    return response.json();
  })
  .then(data => callback(data))
  .catch(error => console.error("서버 오류 발생: " + error));
}

// API 요청 함수 (요청 헤더가 0)
function fetchProfileJson(method, callback, url, dto) {
  fetch(url, {
    method: method,
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(dto)
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.error(errorData.message);
        alert('Error: ' + errorData.message);
        window.location.href = "/";
        throw new Error(errorData.message);
      });
    }
    return response.json();
  })
  .then(data => callback(data, dto.username))
  .catch(error => console.error("서버 오류 발생: " + error));
}

// 사진 등록/수정 api 요청 함수
function fetchProfileImage(method, url) {
  const profileForm = document.getElementById('profileImageForm');
  const formData = new FormData(profileForm);
  fetch(url, {
    method: method,
    body: formData
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        console.error(errorData.message);
        window.location.href = "/";
        throw new Error(errorData.message);
      });
    }
    return response.json();
  })
  .then(data => {
    if (data.result) {
      alert("사진이 정상적으로 등록되었습니다.");
      img.src = data.fileUrl; // img의 src 속성을 서버에서 반환된 URL로 업데이트
      window.location.reload(); // 새로 고침
    } else {
      alert("사진 등록에 실패하였습니다.");
    }
  })
  .catch(error => console.error("서버 오류 발생: " + error));
}

// 프로필 데이터 불러오기 함수
function handleProfileData(data) {
  nickname.innerText = data.nickname;
  email.innerText = data.email;
  username.value = data.nickname;
  intro.value = data.intro;
  petName.value = data.petName;
  petSex.value = data.petSex;
  petAge.value = data.petAge;
  petCategory.value = data.petCategory;
  publicStatus.checked = data.publicStatus === "1";
  if (data.fileUrl !== null) {
    console.log("등록된 프로필 사진을 불러옵니다.");
    img.src = data.fileUrl;
  }
  originalIntroValue = intro.value.trim();
  originalPetNameValue = petName.value.trim();

  // 반려동물 이름이 없다면, 관련 옵션 비활성화
  if (originalPetNameValue.length === 0) {
    console.log("반려동물 이름 X -> 관련 옵션 비활성화");
    petOptionsStatus(false);
  } else {
    // 반려동물 이름이 있다면, 관련 옵션 활성화
    console.log("반려동물 이름 0 -> 관련 옵션 활성화");
    petOptionsStatus(true);
  }
}

// intro 입력시 제출 버튼 활성화 여부 검토
intro.addEventListener('input', () => {
  const introValue = intro.value.trim();
  // intro 필드 값이 원래 값과 다르면 제출 버튼 활성화
  if (introValue !== originalIntroValue) {
    submitBtn.disabled = false; // 제출 버튼 활성화
  } else {
    submitBtn.disabled = true; // 제출 버튼 비활성화
  }
});

// 반려동물 이름 입력시 관련 옵션 활성화/비활성화 + 제출 버튼 활성화
pet_name.addEventListener('input', () => {
  const petNameValue = pet_name.value.trim();
  // intro 필드 값이 원래 값과 다르면 제출 버튼 활성화

  // 반려동물 이름이 비어있는 경우
  if (petNameValue.length === 0) {
    petOptionsStatus(false); // 옵션 비활성화
    submitBtn.disabled = true; // 제출 버튼 비활성화
  } else if (petNameValue !== originalPetNameValue) {
    // 반려동물 이름이 비어있지 않고, 원래 값과 다르면
    petOptionsStatus(true); // 옵션 활성화
    submitBtn.disabled = false; // 제출 버튼 활성화
  } else {
    // 반려동물 이름이 원래 값과 같으면
    petOptionsStatus(true); // 옵션 활성화
    submitBtn.disabled = true; // 제출 버튼 비활성화
  }
});

// 데이터 변화시 제출 버튼을 활성화하는 버튼
function activateSubmitButton() {
  submitBtn.disabled = false; // 제출 버튼 활성화
}

// 반려동물 성별 선택시 제출 버튼 상태 변경
pet_sex.addEventListener('change', activateSubmitButton);

// 반려동물 나이 선택시 제출 버튼 상태 변경
pet_age.addEventListener('change', activateSubmitButton);

// 카테고리 선택시 제출 버튼 상태 변경
petCategory.addEventListener('change', activateSubmitButton);

// 계정 활성화 비활성화 선택시 제출 버튼 상태 변경
publicStatus.addEventListener('change', activateSubmitButton);

// 프로필 등록 함수
function createProfile(nickname) {
  fetchProfile("POST", handleCreateProfile, "/api/mypage/profiles/reg");
  window.location.href = `/mypage/profile/edit/${nickname}`;
}

// 프로필 등록 API 후처리 함수
function handleCreateProfile(data) {
  if (!data.result) {
    alert("프로필 등록이 실패되었습니다.");
    window.location.href = "/";
  } else {
    alert("프로필 등록되었습니다.");
  }
}

// 프로필 데이터 수정 함수
function saveProfileData(data, nickname) {
  if (data.result) {
    alert("업데이트가 완료되었습니다.");
    window.location.href = `/mypage/profile/edit/${nickname}`;
  } else {
    alert("업데이트가 실패하였습니다.");
  }
}

// 중복 확인 함수
function checkUsername(data) {
  if (data.result) {
    alert("이미 존재합니다.");
  } else {
    alert("사용 가능합니다");
    submitBtn.disabled = false; // 제출하기 버튼 활성화
  }
}

// 프로필 수정 요청 DTO 구성
function createRequestDto(nickname) {
  return {
    nickname: nickname,
    username: username.value,
    intro: intro.value,
    petName: petName.value,
    petSex: petSex.value,
    petAge: petAge.value,
    petCategory: petCategory.value,
    publicStatus: publicStatus.checked ? "1" : "0"
  };
}

// 회원 탈퇴 함수
function withdrawAccount(data) {
  if (data.result === true) {
    alert("회원 탈퇴가 완료되었습니다.");
    window.location.href = "/login";
  } else {
    alert("회원 탈퇴가 실패되었습니다. 다시 시도해주세요.");
    window.location.href = "/";
  }
}

// 사진 삭제 함수
function deleteImage(data) {
  if (data.result === true) {
    console.log("프로필 이미지가 삭제되었습니다.")
    window.location.reload();
  } else {
    console.log("프로필 이미지가 실패하였습니다.")
    window.location.reload();
  }
}

// 초기화 함수
function initialize() {
  // 반려동물 나이 옵션 설정
  setPetAgeOptions(100);
  // 닉네임 중복확인 버튼 초기 비활성화
  checkUsernameBtn.disabled = true;
  // 제출하기 버튼 초기 비활성화
  submitBtn.disabled = true;
  const nickname = window.location.pathname.split('/').pop();
  const profileForm = document.getElementById('profileImageForm');
  // 초기화 - 프로필 정보 불러오기
  fetchProfile("GET", handleProfileData, "/api/mypage/profiles/info");

  // 초기화 - 유저명 변경시 중복 버튼 활성화
  username.addEventListener('keyup', () => {
    checkUsernameBtn.disabled = false;
  });

  // 초기화 - 중복 버튼 클릭시 api 요청
  checkUsernameBtn.addEventListener('click', event => {
    event.preventDefault();
    fetchProfile("GET", checkUsername,
        `/api/mypage/profiles/check-username?nickname=${encodeURIComponent(
            username.value)}`);
  });

  // 초기화 - 제출 버튼 클릭시 프로필 정보 업데이트
  submitBtn.addEventListener('click', event => {
    event.preventDefault();
    const nickname = window.location.pathname.split('/').pop();
    fetchProfileJson("PATCH", saveProfileData, "/api/mypage/profiles/edit",
        createRequestDto(nickname));
  });

  // 초기화 - 회원 탈퇴 클릭시 회원 탈퇴
  deleteAccountBtn.addEventListener('click', event => {
    event.preventDefault();
    const deleteAccountMessage = confirm("계정이 영구 삭제됩니다. 정말 탈퇴하시겠습니까?");
    if (deleteAccountMessage) {
      fetchProfile("GET", withdrawAccount, "/api/users/withdraw");
    } else {
      alert("회원 탈퇴가 취소되었습니다.");
      window.location.href = "/";
    }
  });

  // 초기화 - 사진 변경 버튼시 파일 선택 버튼이 눌림
  imageBtn.addEventListener('click', event => {
    event.preventDefault();
    const img = getElement('img');
    // 모달 창 열기
    profileModal.classList.add('show');
    profileOverlay.style.display = 'block';
  });
// 모달 닫기
  closeModalButtons.forEach(button => {
    button.addEventListener('click', (event) => {
      event.preventDefault();
      profileModal.classList.remove('show');
      profileOverlay.style.display = 'none';
    });
  });
  // 초기화 - 프로필 사진 등록
  document.getElementById("images").addEventListener('change', event => {
    event.preventDefault();
    // 기본 이미지일 경우 이미지 등록 api 요청
    if (img.src === "../../../image/profile/profile.png") {
      console.log("프로필 사진 등록 api 요청")
      fetchProfileImage('POST',
          `/api/mypage/profiles/images/reg?nickname=${encodeURIComponent(
              nickname)}`);
    } else { // 아닐 경우 이미지 업데이트 api 요청
      console.log("프로필 사진 업데이트 api 요청")
      fetchProfileImage('PATCH',
          `/api/mypage/profiles/images/edit?nickname=${encodeURIComponent(
              nickname)}`);
    }
  });

  // 초기화 - 프로필 사진 업데이트
  profileImageUpload.addEventListener('click', (event) => {
    event.preventDefault();
    document.getElementById("images").click();
  });
  // 초기화 - 프로필 사진 삭제
  profileImageDelete.addEventListener('click', (event) => {
    event.preventDefault();
    console.log("프로필 이미지 삭제");
    fetchProfile("DELETE", deleteImage,
        `/api/mypage/profiles/images/delete?nickname=${encodeURIComponent(
            nickname)}`)
  });

}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;
