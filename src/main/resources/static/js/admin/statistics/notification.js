let eventSource = null;

window.addEventListener("load",async () => {
  console.log("Page loaded, initializing...");
  connect(); // SSE 연결
})

function connect() {

  if (eventSource) {
    console.log(
        "SSE connection already exists, closing previous connection...");
    eventSource.close();
  }

  eventSource = new EventSource("http://localhost:8080/api/admin/connect")

  eventSource.onopen = () => console.log("SSE connection opened")

  eventSource.addEventListener("connect", (e) => {
    const {data: receivedConnectData } = e;
    console.log("connect event data: ", receivedConnectData); // 'connected'
  })


  eventSource.addEventListener("count", (e) => {
    const {data: receivedConnectData } = e;
    console.log("count: ", receivedConnectData); // 'connected'
  })

  eventSource.onclose = () => {
    console.log("SSE connection closed");
    eventSource = null; // 연결이 닫힌 후에는 eventSource를 null로 설정하여 추적 초기화
  };
}