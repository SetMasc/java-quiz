import * as ui from './ui.js'
import * as api from './api.js'
import * as ws from './ws.js'

let stompClient = null
let room_code = sessionStorage.getItem("room_code");
let user_token = sessionStorage.getItem("user_token")
let username= null;

const stream_space = document.getElementById("stream-space");

const socket = new SockJS('/ws-quiz');
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Successfully connected to sockets: ' + frame);

});


window.onload = function (){

    bindButton("create-room-btn", handleCreateRoom);

}

function bindButton(id, handler) {
    const btn = document.getElementById(id);
    if (btn) btn.addEventListener("click", handler);
}

async function handleCreateRoom() {
    try {
        const data = await api.createRoom();
        username = "admin";
        sessionStorage.setItem("username", username);
        room_code = data;
        ui.hide("selection-screen");
        await joinRoom();
    } catch (err) {
        alert("Error : " + err.message);
    }
}

async function joinRoom() {
    user_token = sessionStorage.getItem("user_token");

    const payload = {
        username: username,
        user_token: user_token
    }

    try {
        await api.checkRoom(room_code);
        stompClient.subscribe(`/topic/room/${room_code}`, function (response) {
        const playersList = JSON.parse(response.body);
            user_token = sessionStorage.getItem("user_token");
            Object.values(playersList).forEach(u => {
                if(u.token !== user_token){
                    u.token = null;
                }
            });
            ui.renderPlayers(playersList, user_token);
        });
        //todo надо поправить порядок выполнения или разнести получение токена и вход в комнату
        // в разные функции, т.к. условие подписки выполняется раньше чем я обнавляю токен
        // либо переместить проверку, ЛИБО!!!!!!! вынести ее в бэк!!!


        const data = await ws.joinRoom(stompClient, room_code, payload);
        user_token = data.token;
        sessionStorage.setItem("user_token", user_token);
        const isHost = data.isHost;
        const room_status = data.room_status;



        switch (room_status){
            case "LOBBY":{
                ui.showLobby(isHost);
            }
        }
    }
    catch (err){
        alert("Error : " + err.message);
    }finally {
        sessionStorage.setItem("user_token", user_token);
    }
}



