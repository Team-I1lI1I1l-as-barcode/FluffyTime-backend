window.addEventListener("load", loadStatisticsData);

async function loadStatisticsData() {
  generateUserStatisticsChart(await getDailyCountStatistics("api/admin/statistics/user"));
  generatePostStatisticsChart(await getDailyCountStatistics("api/admin/statistics/contents"));
}

async function getDailyCountStatistics(url) {
  try {

    const response = await fetch(url,{
      method: 'POST'
    })

    if(!response.ok) {
      new Error("잘못된 요청")
      return null
    }
    return await response.json()

  } catch (error) {
    console.error("userDailyCounts 로드 중 오류 발생", error.message);
  }
}

function generateUserStatisticsChart(response) {
  let dailyCounts = response.dailyCountsStatistics
  const dateAtArray = setDateAtArray(31);
  const countArray = setDailyCountArray(dateAtArray, dailyCounts);

  const ctx = document.getElementById('userCountChart');

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: convertToDateFormatted(dateAtArray),
      datasets: [{
        label: '',
        data: countArray,
        backgroundColor: 'rgba(255, 204, 0, 0.7)',
        borderColor: 'rgba(255, 204, 0, 0.1)',
        borderWidth: 1
      }]
    },
    options: {
      scales: {
        x: {
          grid: {
            display: false
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            display: false
          },
          ticks: {
            stepSize: 1
          }
        }
      },
      plugins: {
        legend: {
          display: false
        }
      }
    }
  });
}

function generatePostStatisticsChart(response) {
  let dailyCounts = response.dailyCountsStatistics
  const dateAtArray = setDateAtArray(31);
  const countArray = setDailyCountArray(dateAtArray, dailyCounts);

  const ctx = document.getElementById('contentsCountChart');

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: convertToDateFormatted(dateAtArray),
      datasets: [{
        label: '일일 콘텐츠 등록 현황',
        data: countArray,
        backgroundColor: 'rgba(255, 204, 0, 0.7)',
        borderColor: 'rgba(255, 204, 0, 0.1)',
        borderWidth: 1
      }]
    },
    options: {
      scales: {
        x: {
          grid: {
            display: false
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            display: false
          },
          ticks: {
            stepSize: 1
          }
        }
      },
      plugins: {
        legend: {
          display: false
        }
      }
    }
  });
}

function convertToDateFormatted(dateArray) {
  // 날짜 문자열을 `Date` 객체로 변환하고 형식을 맞추기
  return dateArray.map(dateStr => {
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 월 (1부터 시작하므로 +1)
    const day = String(date.getDate()).padStart(2, '0'); // 일
    return `${month}-${day}`;
  });
}

function setDailyCountArray(dateAtArray, dailyUserCounts) {
  const counts = new Array(dateAtArray.length).fill(0); // Initialize with 0s

  Object.keys(dailyUserCounts).forEach( date => {
    const index = dateAtArray.indexOf(date);

    if (index !== -1) {
      counts[index] = dailyUserCounts[date];
    }
  });

  return counts;
}

function setDateAtArray(setMaxDay) {
  const dates = [];
  for (let i = setMaxDay-1; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-indexed
    const year = date.getFullYear();
    dates.push(`${year}-${month}-${day}`);
  }
  return dates;
}