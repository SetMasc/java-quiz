import {checkAdmin} from "./network/api.js";

const stream_space = document.getElementById("stream-space");



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


export async function renderRoom(currentRoom) {
    let userId = sessionStorage.getItem("userId");
    let userToken = sessionStorage.getItem("userToken")
    let roomCode = currentRoom.roomCode;

    if (currentRoom) {
        setCode(roomCode);
        switch (currentRoom.status) {
            case "LOBBY": {
                toggleLobby(true);
                renderPlayers(currentRoom.users, userId);

                try {
                    const isAdmin = await checkAdmin(roomCode, userToken);

                    if (isAdmin) {
                        show("lobby--admin-buttons");
                    } else {
                        hide("lobby--admin-buttons");
                    }
                } catch (err) {
                    alert("Error : " + err.message);
                    hide("lobby--admin-buttons");
                }

                break;
            }
            case "CLOSED": {
                toggleSelection(true);
                break;
            }
            default: {
                stream_space.innerText = currentRoom.status;
                break;
            }
        }
    } else {
        toggleSelection(true);
        sessionStorage.setItem("roomCode", null);
    }
}



function renderPlayers(players, userId) {
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