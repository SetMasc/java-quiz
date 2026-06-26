let stompClient = null
let room_code = sessionStorage.getItem("room_code");
let user_token = sessionStorage.getItem("user_token")
let username= null;

var stream_space = document.getElementById("stream-space")

const socket = new SockJS('/ws-quiz');
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Successfully connected to sockets: ' + frame);
    checkSession();
});


async function createRoom(){
    result = await fetch("/api/rooms/create");
    if(result.ok){
        room_code = await result.text();
        sessionStorage.setItem("room_code", room_code);
        stream_space.innerText = "Room " + room_code + " created";
        username = "admin";
        var selection_screen = document.getElementById("selection-screen");
        selection_screen.classList.add("hide");
        joinRooom();
    }else{
        stream_space.innerText = "Creating room failed";
    }
}




async function loginRoom(){
    let selection_screen = document.getElementById("selection-screen");
    let join_input = document.getElementById("join-input");

    let login_screen = document.getElementById("login-screen");
    let username_input = document.getElementById("username-input");
    let username_submit = document.getElementById("username-submit");


    if(join_input.value){
        room_code = join_input.value;
    }

    let result = await fetch("/api/rooms/" + room_code + "/join");

    stream_space.innerText = await result.text();

    if(result.ok) {
        selection_screen.classList.add("hide");
        login_screen.classList.remove("hide");
    }

    do{
        await new Promise(res => username_submit.addEventListener('click', res, { once: true }));
        username = username_input.value;
        if (!username) {
            stream_space.innerText = "Please, enter username";
            stream_space.classList.remove("hide");
        }
    }while(!username);
    login_screen.classList.add("hide");

    //todo убрать отсюда

    joinRooom();

}

async function joinRooom(){
    let roomStatus;
    let isHost = false;

    user_token = sessionStorage.getItem("user_token");
    const payload = {
        username: username,
        user_token: user_token
    };

    await new Promise((resolve, reject) => {
        const subscription = stompClient.subscribe('/user/queue/token', (response) => {
            const responseData = JSON.parse(response.body);
            user_token = responseData.token;
            roomStatus = responseData.room_status;
            if(responseData.isHost === "true"){
                isHost = true;
            }
            sessionStorage.setItem("user_token", user_token);

            subscription.unsubscribe();
            resolve(responseData);
        });

        stompClient.send("/app/rooms/" + room_code + "/join", {}, JSON.stringify(payload));

        setTimeout(() => {
            subscription.unsubscribe();
            reject(new Error("Response-wait timeout"));
        }, 10000);
    });


    stream_space.innerText = "Room " + room_code;

    switch (roomStatus){
        case "LOBBY":{
            await showLobby();
            // stompClient.sub
        }
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
        sessionStorage.setItem("user_token", user_token);

        subscription.unsubscribe();
        showLobby();
    });

    user_token = sessionStorage.getItem("user_token");
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

async function checkSession(){
    if(!user_token) {
        console.log("No tokens in memory, nothing to check");
        return;
    }

    const response = await fetch("/api/rooms/check-affiliation", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ user_token: user_token })
    })
    const data = await response.json();
    console.log(data)
    if(data.hasActiveSession){
        let userChoice = confirm('Found active session, return to it?');

        if(userChoice){
            console.log("Returning session . . . TODO");
            if(data.host){
                console.log("User has been admin");
            }else {
                console.log("User has been player");
            }
        }else{
            console.log("Reset all saved data . . . TODO")
            // sessionStorage.clear();
        }

    }



}