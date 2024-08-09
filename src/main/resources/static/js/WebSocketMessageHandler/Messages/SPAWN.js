function SPAWN(message){
    const content = JSON.parse(message.content);
    if (message.player === username){
        player.x = parseInt(content.x);
        player.y = parseInt(content.y);
        player.angle = 0;
        player.weapon = getWeaponFromString(content.weapon);
        players.set(username, player);
        camera = new Camera(0,0, canvas.width, canvas.height);
        document.getElementById('hp').innerText = player.hp;
        alive = true;
    }else {
        players.set(message.player, new Player(content.x,content.y, message.player, 0, getWeaponFromString(content.weapon)));
    }
}