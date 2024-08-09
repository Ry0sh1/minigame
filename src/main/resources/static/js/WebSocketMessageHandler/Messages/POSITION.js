function POSITION(message){
    let pos = message.content.split(',');
    if (players.has(message.player)){
        const p = players.get(message.player);
        p.x = parseInt(pos[0]);
        p.y = parseInt(pos[1]);
    }
}