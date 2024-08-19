function VIEW_ANGLE(message){
    if (message.player !== username){
        const p = players.get(message.player);
        p.angle = parseFloat(message.content);
    }
}