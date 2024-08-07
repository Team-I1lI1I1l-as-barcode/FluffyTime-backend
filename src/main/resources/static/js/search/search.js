let currentTab = 'name'; // 현재 선택된 탭을 나타내는 변수, 초기값은 'name'

// 탭을 선택할 때 호출되는 함수
async function selectTab(tab) {
  currentTab = tab; // 선택된 탭을 업데이트
  // 모든 탭 버튼에서 'active' 클래스를 제거
  document.querySelectorAll('.tabs button').forEach(button => {
    button.classList.remove('active');
  });
  // 현재 선택된 탭 버튼에 'active' 클래스를 추가
  document.getElementById('tab-' + tab).classList.add('active');
  // 검색 입력 필드를 비우고 리스트를 다시 업데이트
  document.getElementById('search-input').value = '';
  await populateList(); // 비동기로 리스트를 업데이트
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

    // 서버로부터 JSON 응답을 받음
    const data = await response.json();

    // 응답 상태가 OK가 아닌 경우, 오류 처리
    if (!response.ok) {
      alert("request not handled");
      throw new Error(data.message || "error");
    }
    return data.list; // 검색 결과 리스트 반환
  } catch (error) {
    console.error(error); // 오류를 콘솔에 출력
  }
}

// 리스트를 업데이트하는 함수
async function populateList() {
  // 검색 입력 필드의 값을 읽어와 소문자로 변환
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
      id.textContent = `ID: ${item.userId}`; // userId 표시

      name.textContent = item.petName; //  이름 표시
    }

    details.appendChild(id);
    details.appendChild(name);

    li.appendChild(img);
    li.appendChild(details);

    resultsList.appendChild(li);
  });
}

// 페이지 로드 시 초기 리스트를 업데이트
window.onload = populateList;
