const subscriptions = {};


let stompClient = null;


const socket = new SockJS('/ws-quiz');
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Successfully connected to sockets: ' + frame);
});

export async function getOrCreateUser(roomCode, payload){
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

export async function sendGameAction(action, roomCode, userToken){
    return await new Promise((resolve, reject) => {
        let timeout;

        let subscription = stompClient.subscribe('/user/game/actions', (response) => {
            if (timeout) {
                clearTimeout(timeout);
            }
            const responseData = JSON.parse(response.body);
            subscription.unsubscribe();
            if(responseData.status == "success"){
                alert(responseData.message);
            }
            resolve(responseData);
        });

        const payload = {
            userToken: userToken,
        }
        stompClient.send("/app/rooms/" + roomCode + "/" + action, {}, JSON.stringify(payload));


        timeout = setTimeout(() => {
            subscription.unsubscribe();
            reject(new Error("Response-wait timeout"));
        }, 10000);
    });
}


export function subscribeToRoom(roomCode, onRoomUpdate) {
    const topic = `/topic/room/${roomCode}`;

    unsubscribeFromTopic(topic);

    const subscription = stompClient.subscribe(topic, function (response) {
        const room = JSON.parse(response.body);

        if (onRoomUpdate) {
            onRoomUpdate(room);
        }
    });

    subscriptions[topic] = subscription;

    return subscription;
}


export function unsubscribeFromTopic(topic) {
    if (subscriptions[topic]) {
        subscriptions[topic].unsubscribe();
        delete subscriptions[topic];
    }
}

export function exitRoom(roomCode, payload){
        stompClient.send("/app/rooms/" + roomCode + "/deleteUser", {}, JSON.stringify(payload));
        unsubscribeFromTopic(`/topic/room/${roomCode}`);
}

