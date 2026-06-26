const subscriptions = {};


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

export function exitRoom(stompClient, roomCode, payload){
        stompClient.send("/app/rooms/" + roomCode + "/deleteUser", {}, JSON.stringify(payload));
}

