function HEAL(message){
    const h = heal.get(parseInt(message.content));
    h.active = false;
    heal.set(parseInt(message.content), h);
    if (message.player !== username){
        return;
    }
    if (player.hp + settings.heal <= 100){
        player.hp += settings.heal;
    }else {
        player.hp = 100;
    }
    document.getElementById('hp').innerText = `${player.hp}`;
}