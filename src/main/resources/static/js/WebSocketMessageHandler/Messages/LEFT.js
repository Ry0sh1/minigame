function LEFT(message){
    if (players.has(message.player)) players.delete(message.player);
}