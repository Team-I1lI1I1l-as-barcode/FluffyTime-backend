// 반려동물 나이 옵션 설정 동적 생성 1~100
const maxAge = 100;
let petAge = document.getElementById("pet_age");

for (let i = 1; i < 101; i++) {
  let option = document.createElement("option");
  option.value = i;
  option.text = i + " 살";
  petAge.add(option);
}

// 프로필 편집 페이지에 접속하자마자 API 요청
window.onload = function () {
// 현재 URL 경로에서 파라미터 추출하기
  const pathSegments = window.location.pathname.split('/');
// URL 파라미터의 맨 마지막에 존재하는 유저아이디 추출하기
  const userId = pathSegments[pathSegments.length - 1];

  let nickname = document.getElementById("nickname"); // 닉네임
  let email = document.getElementById("email"); // 이메일
  let username = document.getElementById("username"); // 유저명(닉네임)
  let intro = document.getElementById("intro") // 소개
  let pet_name = document.getElementById("pet_name"); // 반려동물 이름
  let pet_sex = document.getElementById("pet_sex"); // 반려동물 성별
  let pet_age = document.getElementById("pet_age"); // 반려동물 나이
  let pet_category = document.getElementById("pet_category") // 카테고리
  let public_status = document.getElementById("public_status"); // 프로필 공개/비공개 여부

  // 프로필 정보 불러오기 api 요청
  fetch(`/api/mypage/profiles/info?userId=${encodeURIComponent(userId)}`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.json()) // 서버에서 보낸 응답을 JOSN 형식으로 변환
  .then(data => {
    if (data.code !== "200") { // 서버가 정상적으로 응답했으나, 응답 데이터 안에 오류 정보가 포함되어있는 경우
      alert(data.message);
      window.location.href = "/";
    } else { // 성공적인 응답을 받은 경우
      nickname.innerText = data.nickname;
      email.innerText = data.email;
      username.value = data.nickname;
      intro.value = data.intro;
      pet_name.value = data.petName;
      pet_sex.value = data.petSex;
      pet_age.value = data.petAge;
      pet_category.value = data.petCategory;

      if (data.publicStatus === "1") {
        public_status.checked = true;
      } else {
        public_status.checked = false;
      }
    }
  })
  .catch(error => {  //fetch() 호출 이후, 네트워크 통신 오류, 서버 미응답, 요청이 제대로 이루어지지 않을때 오류 출력
    alert("서버 오류 발생:" + error);
  });

}
