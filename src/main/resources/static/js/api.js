


export async function createRoom(){
    try{
        const result = await fetch("/api/rooms/create");
        if(!result.ok){
            throw new Error("Creating room failed");
        }

        return await result.text();
    }catch (err){
        throw new Error(err.message);
    }
}

export async function checkRoom(room_code){
    const result = await fetch("/api/rooms/" + room_code + "/join");
    if(!result.ok) {
        throw new Error(result.status.toString());
    }
}

export async function checkAffiliation(user_token){
    const response = await fetch("/api/rooms/check-affiliation", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ user_token: user_token })
    })
    const data = await response.json();
    console.log(data);
    return data;
}