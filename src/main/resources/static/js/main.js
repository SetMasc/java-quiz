import * as ui from './ui.js'
import {toggleSelection} from './ui.js'
import * as api from './api.js'
import * as ws from './ws.js'

let stompClient = null;
let roomCode = sessionStorage.getItem("roomCode");
let currentRoom = null;
let userToken = sessionStorage.getItem("userToken")
let userId = null;
let username= null;

const stream_space = document.getElementById("stream-space");

const socket = new SockJS('/ws-quiz');
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Successfully connected to sockets: ' + frame);

});


window.onload = function (){

    bindButton("create-room-btn", handleCreateRoom);
    bindButton("join-room-btn", handleJoinRoom);
    bindButton("login--username-submit", loginRoom);
    bindButton("room-screen--exit-btn", handleExitRoom);

    bindButton("room-screen--code-placeholder", handleCopyCode);
}

window.onclose = function () {

}

function bindButton(id, handler) {
    const btn = document.getElementById(id);
    if (btn) btn.addEventListener("click", handler);
}

function renderRoom() {
    if (currentRoom) {
        switch (currentRoom.status) {
            case "LOBBY": {
                ui.toggleLobby(true);
                ui.renderPlayers(currentRoom.users, userId);
                break;
            }
            case "CLOSED": {
                toggleSelection(true);
                ws.unsubscribeFromTopic(`/topic/room/${roomCode}`);
                break;
            }
        }
    }else{
        ui.toggleSelection(true);
        ws.unsubscribeFromTopic(`/topic/room/${roomCode}`);
        roomCode = null;
        sessionStorage.setItem("roomCode", roomCode);
    }
}

async function handleCreateRoom() {
    try {
        const data = await api.createRoom();
        username = "admin";
        sessionStorage.setItem("username", username);
        roomCode = data;
        ui.hide("selection-screen");
        ui.setCode(roomCode);
        await joinRoom();
    } catch (err) {
        alert("Error : " + err.message);
    }
}

async function handleJoinRoom(){
    let join_input = document.getElementById("join-input").value;

    if(join_input){
        try{
            await api.getRoom(join_input);
            roomCode = join_input;
            ui.hide("selection-screen");
            ui.setCode(roomCode);
            ui.toggleLogin(true);
        }catch (err){
            alert("Error : " + err.message);
        }
    }
}

async function handleExitRoom(){
    userToken = sessionStorage.getItem("userToken");
    ws.exitRoom(stompClient, roomCode, {userToken: userToken});
    currentRoom = JSON.parse(await api.getRoom(roomCode));
    const currentUser = currentRoom.users.find(user => user.userId === userId);
    if(!currentUser){
        currentRoom = null;
    }
    renderRoom();
}

async function handleCopyCode(){
    try{
        await navigator.clipboard.writeText(roomCode);
    }catch (err){
        alert("Error : " + err.message);
    }
}

async function loginRoom() {
    let username_input = document.getElementById("login--username-input").value;

    if (username_input) {
        username = username_input;
        ui.toggleLogin(false);
        await joinRoom();
    }
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



