import * as ui from './ui.js'
import * as api from './network/api.js'
import * as ws from './network/ws.js'
import * as main from './core/mainPageManager.js'
import * as room from './core/roomManager.js'

let stompClient = null;
let roomCode = sessionStorage.getItem("roomCode");
let currentRoom = null;
let userToken = sessionStorage.getItem("userToken")
let userId = null;
let username= sessionStorage.getItem("username");

const stream_space = document.getElementById("stream-space");




window.onload = function (){

    bindButton("create-room-btn", main.handleCreateRoomBtn);
    bindButton("join-room-btn", main.handleJoinRoomBtn);
    bindButton("login--username-submit", main.handleJoinRoomBtn);
    bindButton("room-screen--exit-btn", room.handleExitRoom);

    bindButton("room-screen--code-placeholder", room.handleCopyCode);
}

window.onclose = function () {

}

document.addEventListener('DOMContentLoaded', async () => {
    const pathSegments = window.location.pathname.split('/').filter(segment => segment.length > 0);
    if (pathSegments[0] === 'invite' && pathSegments[1]) {
        const inviteCode = pathSegments[1];
        roomCode = inviteCode;
        sessionStorage.setItem("roomCode", roomCode);

        try {
            await api.getRoom(roomCode);
            ui.hide("selection-screen");
            ui.setCode(roomCode);
            ui.toggleLogin(true);
        } catch (err) {
            alert("Error : " + err.message);
            window.location.pathname = "/";
        }

    }else{
        window.history.replaceState({}, document.title, "/");
    }
});




function bindButton(id, handler) {
    const btn = document.getElementById(id);
    if (btn) btn.addEventListener("click", handler);
}










async function joinRoom() {
    userToken = sessionStorage.getItem("userToken");

    try {
        currentRoom = ws.subscribeToRoom(stompClient, roomCode, (room) => {
            currentRoom = room;
            renderRoom();
        });

        const payload = {
            username: username,
            userToken: userToken,
            userId: userId
        }

        const user = await ws.getOrCreateUser(stompClient, roomCode, payload);

        userId = user.userId;
        userToken = user.token;
        sessionStorage.setItem("userToken", userToken);

        renderRoom();

    } catch (err) {
        if (currentRoom) {
            currentRoom.unsubscribe();
        }
        alert("Error : " + err.message);
    }
}



