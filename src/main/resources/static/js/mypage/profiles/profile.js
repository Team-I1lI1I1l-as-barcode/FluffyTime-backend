// DOM 요소 선택 함수
const getElement = (id) => document.getElementById(id);
const checkUsernameBtn = getElement("check_username_Btn"); // 중복확인
const nickname = getElement("nickname");
const email = getElement("email");
const username = getElement("username"); // 유저명 (중복확인)
const intro = getElement("intro");
const pet_name = getElement("pet_name");
const pet_sex = getElement("pet_sex");
const pet_age = getElement("pet_age");
const pet_category = getElement("pet_category");
const public_status = getElement("public_status");
const submitBtn = getElement("submit");
const deleteAccountBtn = getElement("delete_account");

let originalIntroValue = ""; // intro 필드의 원래 값을 저장하는 변수
let originalPetNameValue = ""; // intro 필드의 원래 값을 저장하는 변수

// 반려동물 나이 옵션 설정 함수
function setPetAgeOptions(maxAge) {
  console.log("setPetAgeOptions 실행");
  let none_option = document.createElement("option");
  none_option.value = "none";
  none_option.text = "==선택==";
  none_option.selected = true;
  pet_age.add(none_option);
  for (let i = 1; i <= maxAge; i++) {
    let option = document.createElement("option");
    option.value = i;
    option.text = i === 0 ? "-" : `${i} 살`;
    pet_age.add(option);
  }
}

// 반려동물 이름 존재 여부에 따라 성별/나이/카테고리 선택 활성/비활성화
function petOptionsStatus(status) {
  const pet_sex = document.getElementById("pet_sex");
  const pet_age = document.getElementById("pet_age");
  const pet_category = document.getElementById("pet_category");
  if (status) { // 반려동물 이름이 존재하면 옵션 활성화
    pet_sex.disabled = false;
    pet_age.disabled = false;
    pet_category.disabled = false;
  } else { // 반려동물 이름이 존재하지 않으면 비활성화
    pet_sex.disabled = true;
    pet_age.disabled = true;
    pet_category.disabled = true;
  }
}

// API 요청 함수(요청 헤더가 X)
function fetchProfile(method, func, url) {
  console.log("fetchProfile 실행");
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.json())
  .then(data => func(data))
  .catch(error => console.log("서버 오류 발생: " + error));
}

// API 요청 함수(요청 헤더가 0)
function fetchProfileJson(method, func, url, dto) {
  console.log("fetchProfileJson 실행");
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(dto)
  })
  .then(response => response.json())
  .then(data => func(data, dto.username))
  .catch(error => console.log("서버 오류 발생: " + error));
}

// 프로필 데이터 불러오기 함수 - 프로필이 없다는 에러가 뜨면 프로필 등록 api로 이동 / 없다면 데이터 불러오기
function handleProfileData(data) {
  if (data.code === "404") {
    // 프로필 등록 api 부르기
    console.log("handleProfileData 실행 >> createProfile 살행");
    createProfile(window.location.pathname.split('/').pop());
  } else if (data.code !== "404" && data.code !== "200") {
    console.log("handleProfileData 에러 발생 >> " + data.message);
    alert(data.mesaage);
    window.location.href = "/";
  } else {
    console.log("handleProfileData 응답 Success");
    nickname.innerText = data.nickname;
    email.innerText = data.email;
    username.value = data.nickname;
    intro.value = data.intro;
    pet_name.value = data.petName;
    pet_sex.value = data.petSex;
    pet_age.value = data.petAge;
    pet_category.value = data.petCategory;
    public_status.checked = data.publicStatus === "1";

    originalIntroValue = intro.value.trim(); // intro 필드의 원래 값을 저장
    originalPetNameValue = pet_name.value.trim(); // pet_name 필드의 원래 값을 저장
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
pet_category.addEventListener('change', activateSubmitButton);

// 계정 활성화 비활성화 선택시 제출 버튼 상태 변경
public_status.addEventListener('change', activateSubmitButton);

// 프로필 등록 함수
function createProfile(nickname) {
  console.log("createProfile 실행");
  fetchProfile("POST", handleCreateProfile,
      `/api/mypage/profiles/reg?nickname=${encodeURIComponent(nickname)}`)
  console.log("fetchProfile 실행");
}

// 프로필 등록 api 후처리 함수
function handleCreateProfile(data) {
  if (data.code === "404") {
    console.log(data.message);
    alert(data.message);
    window.location.href = "/";
  } else if (data.result === true) {
    console.log("handleCreateProfile 실행 >> true");
    alert("프로필 등록이 완료되었습니다.");
    window.location.reload();
  } else {
    console.log("handleCreateProfile 실행 >> false");
    alert("프로필 등록이 실패되었습니다.");
    window.location.href = "/";
  }
}

// 프로필 데이터 수정 함수
function saveProfileData(data, nickname) {
  if (data.code !== "200") {
    console.log("saveProfileData 실행 >> 에러 발생 " + data.message);
    alert(data.message);
    window.location.href = "/";
  } else if (data.result) {
    console.log("saveProfileData 실행 >>  true");
    alert("업데이트가 완료되었습니다.");
    window.location.href = `/mypage/profile/edit/${nickname}`;// 새로고침
  } else {
    console.log("saveProfileData 실행 >>  false");
    alert("업데이트가 실패하였습니다.")
  }
}

// 중복 확인
function check_username(data) {
  if (data.result) {
    console.log("check_username 실행 >> 중복 0");
    alert("이미 존재합니다.");
  } else {
    console.log("check_username 실행 >> 중복 X");
    alert("사용 가능합니다");
    submitBtn.disabled = false; // 제출하기 버튼 활성화
  }
}

// 프로필 수정 요청 DTO 구성
function createReqeustDto(nickname) {
  console.log("createReqeustDto 실행");
  // 최신 폼 필드 값을 가져와서 ProfileRequestDto 생성
  const ProfileRequestDto = {
    nickname: nickname,
    username: username.value,
    intro: intro.value,
    petName: pet_name.value,
    petSex: pet_sex.value,
    petAge: pet_age.value,
    petCategory: pet_category.value,
    publicStatus: public_status.checked ? "1" : "0"
  };
  return ProfileRequestDto;
}

// 회원 탈퇴
function withdrawAccount(data) {
  if (data.result === true) {
    console.log("withdrawAccount 실행 >> true");
    alert("회원 탈퇴가 완료되었습니다.");
    window.location.href = "/";
  } else {
    console.log("withdrawAccount 실행 >> false");
    alert("회원 탈퇴가 실패되었습니다. 다시 시도해주세요.");
    window.location.href = "/";
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

  // 현재 URL 경로에서 파라미터 추출
  const nickname = window.location.pathname.split('/').pop();

  // 프로필 정보 불러오기
  fetchProfile("GET", handleProfileData,
      `/api/mypage/profiles/info?nickname=${encodeURIComponent(nickname)}`);

  // 닉네임 변경시 중복확인 버튼 활성화
  username.addEventListener('keyup', () => {
    checkUsernameBtn.disabled = false;
  });

  // 중복 확인 버튼 클릭시 api 요청(유저명 중복 여부)
  checkUsernameBtn.addEventListener('click', event => {
    event.preventDefault(); // 기본 폼 제출 방지
    fetchProfile("GET", check_username,
        `/api/mypage/profiles/check-username?nickname=${encodeURIComponent(
            username.value)}`);
  });

  // 제출하기 버튼을 누를시 api 요청 (프로필 수정)
  submitBtn.addEventListener('click', event => {
    event.preventDefault(); // 기본 폼 제출 방지
    fetchProfileJson("PATCH", saveProfileData, "/api/mypage/profiles/edit",
        createReqeustDto(nickname));

  });

  // 회원 탈퇴 버튼을 누를시 api 요청
  deleteAccountBtn.addEventListener('click', event => {
    event.preventDefault(); // 기본 폼 제출 방지
    const deleteAccountMessage = confirm("계정이 영구 삭제됩니다. 정말 탈퇴하시겠습니까?");
    if (deleteAccountMessage) { // 확인 클릭시
      console.log(nickname + " 회원님 탈퇴 알림창에 확인 클릭 ");
      fetchProfile("GET", withdrawAccount,
          `/api/users/withdraw?nickname=${encodeURIComponent(nickname)}`);
    } else {
      console.log(nickname + "회원 탈퇴가 취소되었습니다.");
      alert("회원 탈퇴가 취소되었습니다. ")
    }

  });
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initialize;
