function VIEW_ANGLE(message){
    const p = players.get(message.player);
    p.angle = parseFloat(message.content);
    players.set(message.player, p);
}