


export async function createRoom(){
    const result = await fetch("/api/rooms/create");
    if(!result.ok){
        throw new Error("Creating room failed");
    }
    return await result.text();
}

export async function getRoom(roomCode){
        const result = await fetch("/api/rooms/" + roomCode + "/get");
        if(!result.ok){
            throw new Error(await result.text())
        }
        return await result.text();
}

export async function checkAdmin(roomCode, userToken){
    const result = await fetch("/api/rooms/" + roomCode + "/check-admin", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Обязательный заголовок для Spring!
        },
        body: JSON.stringify({
            token: userToken
        })
    });

    if(!result.ok){
        throw new Error(await result.text());
    }
    return await result.json();
}