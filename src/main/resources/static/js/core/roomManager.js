import * as ws from "../network/ws.js";
import * as ui from "../ui.js";



export function handleExitRoom(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    ws.exitRoom(roomCode, {userToken: userToken});
    ui.renderRoom(null);
    sessionStorage.clear(); //todo в будущем можно поправить
}

export async function handleCopyCode(){
    let roomCode = sessionStorage.getItem("roomCode");
    try{
        await navigator.clipboard.writeText(roomCode);
    }catch (err){
        alert("Error : " + err.message);
    }
}


export async function joinRoom(roomCode) {
    let roomSubscription = null;

    let userToken = sessionStorage.getItem("userToken");
    const username = sessionStorage.getItem("username");
    const userId = sessionStorage.getItem("userId");

    try {
        const payload = {
            username: username,
            userToken: userToken,
            userId: userId
        };

        const user = await ws.getOrCreateUser(roomCode, payload);

        sessionStorage.setItem("userToken", user.token);
        sessionStorage.setItem("userId", user.userId);

        roomSubscription = ws.subscribeToRoom(roomCode, (roomData) => {
            ui.renderRoom(roomData);
        });

    } catch (err) {
        alert("Error : " + err.message);

        if (roomSubscription && typeof roomSubscription.unsubscribe === 'function') {
            roomSubscription.unsubscribe();
        }

    }
}


export function handleStartGame(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    try{
        ws.sendGameAction("start", roomCode, userToken);
    }catch (err){
        alert("Error : " + err.message);
    }
}

export function handlePauseGame(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    try{
        ws.sendGameAction("pause", roomCode, userToken);
    }catch (err){
        alert("Error : " + err.message);
    }
}

export function handleResumeGame(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    try{
        ws.sendGameAction("resume", roomCode, userToken);
    }catch (err){
        alert("Error : " + err.message);
    }
}

export function handleDeleteRoom(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    try{
        ws.sendGameAction("delete", roomCode, userToken);
    }catch (err){
        alert("Error : " + err.message);
    }
}
