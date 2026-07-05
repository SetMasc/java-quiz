import * as ws from './network/ws.js';


export function hide(id){
    const el = document.getElementById(id);
    if(el) el.classList.add("hide");
}

export function show(id){
    const el = document.getElementById(id);
    if(el) el.classList.remove("hide");
}

export function setCode(roomCode){
    const code_placeholder = document.getElementById("room-screen--code-placeholder");
    code_placeholder.innerText = "Room " + roomCode;
}

export function toggleLogin(visible){
    if(visible){
        show("room-screen");
        show("room-screen--login");
    }else{
        hide("room-screen--login");
    }
}

export function toggleLobby(visible){
    if(visible){
        show("room-screen");
        show("room-screen--lobby");
    }else{
        hide("room-screen--lobby");
    }
}

export function toggleSelection(visible){
    if(visible){
        show("selection-screen");
        hide("room-screen");
        toggleLogin(!visible);
        toggleLobby(!visible);
    }else {
        hide("selection-screen");
    }
}


export function renderRoom(currentRoom) {
    let userId = sessionStorage.getItem("userId");
    if (currentRoom) {
        setCode(currentRoom.roomCode);
        switch (currentRoom.status) {
            case "LOBBY": {
                toggleLobby(true);
                renderPlayers(currentRoom.users, userId);
                break;
            }
            case "CLOSED": {
                toggleSelection(true);
                break;
            }
        }
    }else{
        toggleSelection(true);
        sessionStorage.setItem("roomCode", null);
    }
}



export async function renderPlayers(players, userId) {
    const users_list = document.getElementById("lobby--users-list");
    users_list.textContent = null;
    players.forEach(item => {
        const li = document.createElement('li');
        li.textContent = item.username;
        if (item.userId == userId) {
            li.textContent += " (you)"
            li.style.order = -1;
        }

        users_list.appendChild(li);
    });
}