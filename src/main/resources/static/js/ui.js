
export function showLobby(isHost) {
    show("lobby-screen");
}


export function hide(id){
    const el = document.getElementById(id);
    if(el) el.classList.add("hide");
}

export function show(id){
    const el = document.getElementById(id);
    if(el) el.classList.remove("hide");
}

export async function renderPlayers(players, user_token) {
    const users_list = document.getElementById("lobby-users-list");

    players.forEach(item => {
        const li = document.createElement('li');
        li.textContent = item.username;
        if (item.token === user_token) {
            li.textContent += " (you)"
        }

        users_list.appendChild(li);
    });
}