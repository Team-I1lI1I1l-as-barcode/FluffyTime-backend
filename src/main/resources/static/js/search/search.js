let currentTab = 'name';
// 현재 선택된 탭, 초기값은 'name'

// 탭을 전환하려 클릭할 때 호출되는 함수
async function selectTab(tab) {
  currentTab = tab;
  console.log('현재 탭: ' + tab);

  // html의 모든 탭 버튼에서 'active' 클래스를 제거한 후, 현재 선택된 탭 버튼에 'active' 클래스를 추가
  document.querySelectorAll('.tabs button').forEach(button => {
    button.classList.remove('active');
  });
  document.getElementById('tab-' + tab).classList.add('active');

  // 검색 입력 필드초기화
  document.getElementById('search-input').value = '';

  //엔터키로도 검색 가능
  await document.getElementById('search-input').addEventListener('keypress',
      e => {
        if (e.key === 'Enter') {
          populateList();
        }
      });
}

// 검색 결과를 가져오는 함수
async function getSearchResult(tab, query) {
  try {
    let url = '/api/search/';
    // 탭에 따라 적절한 URL을 설정
    if (tab === 'name') {
      url += 'names'; // 이름 검색
    } else if (tab === 'tag') {
      url += 'tags'; // 태그 검색
    } else {
      url += 'accounts'; // 계정 검색
    }
    // url += '?page=${page}&perPage=${itemsPerPage}';

    // 검색 쿼리를 포함한 JSON 객체 생성
    const jsonData = {
      query: query
    };

    // API 호출
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(jsonData) // 요청 본문에 쿼리 추가
    });

    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error("Expected JSON response, but got something else");
    }

    // 서버로부터 JSON 응답을 받음
    const data = await response.json();

    // 응답 상태가 OK가 아닌 경우, 오류 처리
    if (!response.ok) {
      alert("Request not handled: " + (data.message || "Unknown error"));
      throw new Error(data.message || "Request failed");
    }
    return data.list; // 검색 결과 리스트 반환

  } catch (error) {
    console.error("Error during fetch:", error);
    return [];
  }
}

// 리스트를 업데이트하는 함수
async function populateList() {

  // 검색어를 가져와 소문자로 변환
  const query = document.getElementById('search-input').value.toLowerCase();

  // 현재 탭과 검색 쿼리를 기반으로 검색 결과를 가져옴
  const list = await getSearchResult(currentTab, query);
  const resultsList = document.getElementById('results-list');
  resultsList.innerHTML = ''; // 이전 검색 결과를 지움

  // 검색 결과를 리스트 항목으로 추가
  list.forEach(item => {
    const li = document.createElement('li');

    const img = document.createElement('img');
    img.src = item.imageUrl; // 이미지 URL을 설정

    const details = document.createElement('div');
    details.className = 'details';

    const id = document.createElement('div');
    id.className = 'id';

    const name = document.createElement('div');

    if (currentTab === "tag") {
      id.textContent = `ID: ${item.tagId}`; // 태그 ID 표시

      name.textContent = item.tagName; // 태그 이름 표시
    } else {
      id.textContent = item.nickName; // 닉네임 표시

      name.textContent = item.petName; //  이름 표시
    }

    details.appendChild(id);
    details.appendChild(name);

    li.appendChild(img);
    li.appendChild(details);

    // 클릭 이벤트 리스너를 추가
    li.addEventListener('click', () => {
      // 원하는 페이지로 리다이렉트
      window.location.href = `userpages/${id.textContent}`;
    });

    resultsList.appendChild(li);
  });
}

// 사이드바 마이페이지 코드
document.getElementById("mypageBtn").addEventListener('click', event => {
  //window.location.href = "/mypage/test";
  fetch("/api/mypage/info", {
    method: "GET", // GET 요청
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // 에러 메시지 포함하여 alert 호출
        console.log("fetchMyPage 응답 에러 발생 >> " + errorData.message);
        alert('Error: ' + errorData.message);
        window.location.href = "/";
      });
    }
    return response.json();
  })  // 서버에서 보낸 응답을 JSON 형식으로 변환
  .then(data => {
    window.location.href = `/mypage/${data.nickname}`;
  })
  .catch(error => {
    console.log("서버 오류 발생:" + error);
  });
});

// 페이지 로드 시 초기 리스트를 업데이트
window.onload = selectTab(currentTab);
