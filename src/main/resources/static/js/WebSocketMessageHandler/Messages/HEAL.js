function HEAL(message){
    const healID = parseInt(message.content);
    if (heal.has(healID)){
        const h = heal.get(healID);
        h.active = false;
        heal.set(healID, h);
        if (player.hp + settings.heal <= 100){
            player.hp += settings.heal;
        }else {
            player.hp = 100;
        }
        document.getElementById('hp').innerText = `${player.hp}`;
    }
}