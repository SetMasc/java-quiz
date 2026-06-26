


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
    show("room-screen");
    if(visible){
        show("room-screen--login");
    }else{
        hide("room-screen--login");
    }
}

export function toggleLobby(visible){
    show("room-screen");
    if(visible){
        show("room-screen--lobby");
    }else{
        hide("room-screen--lobby");
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