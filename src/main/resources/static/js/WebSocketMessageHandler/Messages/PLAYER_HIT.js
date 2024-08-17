function PLAYER_HIT(message){
    if (message.content.split(",")[0] === username){

        const damage = parseInt(message.content.split(",")[1]);
        let rest = player.shield - damage;
        if (rest >= 0){
            player.shield = player.shield - damage;
        }else {
            player.shield = 0;
            player.hp = player.hp - Math.abs(rest);
        }
        document.getElementById('hp').innerText = player.hp;
        document.getElementById('shield').innerText = player.shield;
    }
}