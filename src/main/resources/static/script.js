let stompClient = null
let room_code = localStorage.getItem("room_code");
let user_token = localStorage.getItem("user_token")
let username= null;

var stream_space = document.getElementById("stream-space")

const socket = new SockJS('/ws-quiz');
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Successfully connected to sockets: ' + frame);
});




async function createRoom(){
    result = await fetch("/api/rooms/create");
    if(result.ok){
        room_code = await result.text();
        localStorage.setItem("room_code", room_code);
        stream_space.innerText = "Room " + room_code + " created";
        username = "admin";
        var selection_screen = document.getElementById("selection-screen");
        selection_screen.classList.add("hide");
        joinLobby();
    }else{
        stream_space.innerText = "Creating room failed";
    }
}

async function joinRoom() {
    if(document.getElementById("join-input").value){
        room_code = document.getElementById("join-input").value;
    }
    result = await fetch("/api/rooms/" + room_code + "/join");
    stream_space.innerText = await result.text();
    if(result.ok) {
        var login_screen = document.getElementById("login-screen");
        var selection_screen = document.getElementById("selection-screen");

        selection_screen.classList.add("hide");
        login_screen.classList.remove("hide");
    }
}

function joinLobby() {
    let login_screen = document.getElementById("login-screen");
    if(!login_screen.classList.contains("hide")){
        username = document.getElementById("username-input").value;
        if (!username) {
            stream_space.innerText = "Please, enter username";
            stream_space.classList.remove("hide");
            return;
        }
    }


    const subscription = stompClient.subscribe("/user/queue/token", function (response) {
        const responseData = JSON.parse(response.body);
        user_token = responseData.token;
        localStorage.setItem("user_token", user_token);

        subscription.unsubscribe();
        showLobby();
    });

    user_token = localStorage.getItem("user_token");
    const payload = {
        username: username,
        user_token: user_token
    };

    stompClient.send("/app/rooms/" + room_code + "/join", {}, JSON.stringify(payload));
    login_screen.classList.add("hide");

    stream_space.innerText = "Room " + room_code;

}

async function showLobby(){
    let lobby_screen = document.getElementById("lobby-screen");
    const users_list = document.getElementById('lobby-users-list');
    const response = await fetch("/api/rooms/" + room_code + "/lobby");
    const data = await response.json();
    data.forEach(item => {
        const li = document.createElement('li');
        li.textContent = item.username;
        if(item.token === user_token){
            li.textContent += " (you)"
        }
        users_list.appendChild(li);
    });
    lobby_screen.classList.remove("hide");
}