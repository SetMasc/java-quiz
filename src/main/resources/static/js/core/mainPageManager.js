import * as ui from "../ui.js";
import * as api from "../network/api.js";
import { joinRoom } from "./roomManager.js"


export async function handleCreateRoomBtn() {
    try {
        const data = await api.createRoom();
        sessionStorage.setItem("username", "admin");
        sessionStorage.setItem("roomCode", data);
        ui.hide("selection-screen");
        await joinRoom(data);
    } catch (err) {
        alert("Error : " + err.message);
    }
}

export async function handleJoinRoomBtn(){
    let join_input = document.getElementById("join-input").value;

    if(join_input){
        try{
            await api.getRoom(join_input);
            sessionStorage.setItem("roomCode", join_input);
            ui.setCode(join_input);
            ui.hide("selection-screen");
            ui.toggleLogin(true);
        }catch (err){
            alert("Error : " + err.message);
        }
    }
}

export async function handleLoginRoomBtn() {
    let username_input = document.getElementById("login--username-input").value;

    if (username_input) {
        sessionStorage.setItem("username", username_input);
        let roomCode = sessionStorage.getItem("roomCode");
        ui.toggleLogin(false);
        await joinRoom(roomCode);
    }
}