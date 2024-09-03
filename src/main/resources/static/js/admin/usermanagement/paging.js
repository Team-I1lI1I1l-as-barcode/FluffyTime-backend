window.addEventListener("load", function () {
  fetchData(0);
});

async function fetchData(page) {
  try {

    const response = await fetch(`/api/admin/management/users?page=${page}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    const data = await response.json();
    console.log(data);

    updatePage(data);
  } catch (error) {
    console.log(error)
  }
}

function updatePage(data) {
  const userTableBody= document.querySelector(".userTableBody");
  const pagination = document.getElementById('pagination');
  const paginationList = document.getElementById('paginationList');

  // Clear existing content
  userTableBody.innerHTML = '';
  paginationList.innerHTML = '';

  data.content.forEach(user => {
    const row = document.createElement('tr');
    row.innerHTML = `
            <td>${user.email}</td>
            <td>${user.nickname}</td>
            <td>${new Date(user.registrationAt).toLocaleDateString()}</td>
            <td>${user.roles}</td>
            <td><button>삭제</button></td>
        `;
    userTableBody.appendChild(row);
  })

  // Update pagination
  for (let i = 0; i < data.totalPages; i++) {
    const li = document.createElement('li');
    li.innerHTML = `<a href="#" onclick="fetchData(${i})">${i+1}</a>`;
    paginationList.appendChild(li);
  }

  pagination.style.display = data.totalPages > 1 ? 'block' : 'none';
}