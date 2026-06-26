import * as ui from "./ui.js";
import * as api from "./api.js";


export async function getOrCreateUser(stompClient, roomCode, payload){
    return await new Promise((resolve, reject) => {
        let timeout;

        const subscription = stompClient.subscribe('/user/queue/token', (response) => {
            if(timeout){
                clearTimeout(timeout);
            }
            const responseData = JSON.parse(response.body);
            subscription.unsubscribe();
            resolve(responseData);
        });

        stompClient.send("/app/rooms/" + roomCode + "/newUser", {}, JSON.stringify(payload));

        timeout = setTimeout(() => {
            subscription.unsubscribe();
            reject(new Error("Response-wait timeout"));
        }, 10000);
    });
}

export function subscribeToRoom(stompClient, roomCode, onRoomUpdate) {
    return stompClient.subscribe(`/topic/room/${roomCode}`, function (response) {
        const room = JSON.parse(response.body);
        console.log("Получено обновление комнаты по сокету:", room);

        if (onRoomUpdate) {
            onRoomUpdate(room);
        }
    });
}