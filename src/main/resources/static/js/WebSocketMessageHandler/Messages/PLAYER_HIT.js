function PLAYER_HIT(message){
    if (message.content.split(",")[0] === username){
        player.hp = player.hp - parseInt(message.content.split(",")[1]);
        document.getElementById('hp').innerText = player.hp;
    }
}