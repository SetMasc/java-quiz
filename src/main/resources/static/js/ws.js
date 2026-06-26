



export async function joinRoom(stompClient, room_code, payload){
    return await new Promise((resolve, reject) => {
        const subscription = stompClient.subscribe('/user/queue/token', (response) => {
            const responseData = JSON.parse(response.body);
            subscription.unsubscribe();
            resolve(responseData);
        });


        stompClient.send("/app/rooms/" + room_code + "/join", {}, JSON.stringify(payload));

        setTimeout(() => {
            subscription.unsubscribe();
            reject(new Error("Response-wait timeout"));
        }, 10000);
    });
}