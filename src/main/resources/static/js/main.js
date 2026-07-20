import * as ui from './ui.js'
import * as api from './network/api.js'
import * as main from './core/mainPageManager.js'
import * as room from './core/roomManager.js'

let roomCode = sessionStorage.getItem("roomCode");

const stream_space = document.getElementById("stream-space");




window.onload = function (){

    bindButton("create-room-btn", main.handleCreateRoomBtn);
    bindButton("join-room-btn", main.handleJoinRoomBtn);
    bindButton("login--username-submit", main.handleLoginRoomBtn);
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



