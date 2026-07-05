import * as ws from "../network/ws.js";
import * as api from "../network/api.js";



export function joinRoom(){}




export async function handleExitRoom(){
    let userToken = sessionStorage.getItem("userToken");
    let roomCode = sessionStorage.getItem("roomCode");

    ws.exitRoom(roomCode, {userToken: userToken});
    let currentRoom = JSON.parse(await api.getRoom(roomCode));
    let userId = sessionStorage.getItem("userId");
    console.log(currentRoom);
    let currentUser = currentRoom.users.find(user => user.userId === userId);
    renderRoom();   //todo
}

export async function handleCopyCode(){
    let roomCode = sessionStorage.getItem("roomCode");
    try{
        await navigator.clipboard.writeText(roomCode);
    }catch (err){
        alert("Error : " + err.message);
    }
}