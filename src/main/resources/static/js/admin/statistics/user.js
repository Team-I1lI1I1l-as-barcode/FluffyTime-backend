// 전역 변수로 차트 인스턴스 저장
let userChart = null;
let postChart = null;

window.addEventListener("load", loadStatisticsData);

const userChartRefreshBtn = document.getElementById("userChartRefreshBtn");
const contentChartRefreshBtn = document.getElementById("contentChartRefreshBtn");

userChartRefreshBtn.addEventListener("click", () => refreshChart('user'));
contentChartRefreshBtn.addEventListener("click", () => refreshChart('post'));

async function loadStatisticsData() {
  const userStats = await getDailyCountStatistics("api/admin/statistics/user");
  const postStats = await getDailyCountStatistics("api/admin/statistics/contents");

  generateUserStatisticsChart(userStats);
  generatePostStatisticsChart(postStats);
}

async function getDailyCountStatistics(url) {
  try {
    const response = await fetch(url, {
      method: 'GET'
    });

    if (!response.ok) {
      throw new Error("잘못된 요청");
    }
    return await response.json();
  } catch (error) {
    console.error("userDailyCounts 로드 중 오류 발생", error.message);
    return null;
  }
}

function generateUserStatisticsChart(response) {
  const dailyCounts = response.dailyCountsStatistics;
  const dateAtArray = setDateAtArray(31);
  const countArray = setDailyCountArray(dateAtArray, dailyCounts);
  const ctx = document.getElementById('userCountChart').getContext('2d');

  if (userChart) {
    userChart.data.labels = convertToDateFormatted(dateAtArray);
    userChart.data.datasets[0].data = countArray;
    userChart.update();
  } else {
    userChart = new Chart(ctx, {
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
}

function generatePostStatisticsChart(response) {
  const dailyCounts = response.dailyCountsStatistics;
  const dateAtArray = setDateAtArray(31);
  const countArray = setDailyCountArray(dateAtArray, dailyCounts);
  const ctx = document.getElementById('contentsCountChart').getContext('2d');

  if (postChart) {
    postChart.data.labels = convertToDateFormatted(dateAtArray);
    postChart.data.datasets[0].data = countArray;
    postChart.update();
  } else {
    postChart = new Chart(ctx, {
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
}

async function refreshChart(chartType) {
  let url;
  let updateChartFunc;

  if (chartType === 'user') {
    url = "api/admin/statistics/user";
    updateChartFunc = generateUserStatisticsChart;
  } else if (chartType === 'post') {
    url = "api/admin/statistics/contents";
    updateChartFunc = generatePostStatisticsChart;
  } else {
    console.error("Unknown chart type:", chartType);
    return;
  }

  const data = await getDailyCountStatistics(url);
  if (data) {
    updateChartFunc(data);
  }
}

function convertToDateFormatted(dateArray) {
  return dateArray.map(dateStr => {
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${month}-${day}`;
  });
}

function setDailyCountArray(dateAtArray, dailyUserCounts) {
  const counts = new Array(dateAtArray.length).fill(0);

  Object.keys(dailyUserCounts).forEach(date => {
    const index = dateAtArray.indexOf(date);

    if (index !== -1) {
      counts[index] = dailyUserCounts[date];
    }
  });

  return counts;
}

function setDateAtArray(setMaxDay) {
  const dates = [];
  for (let i = setMaxDay - 1; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    dates.push(`${year}-${month}-${day}`);
  }
  return dates;
}
