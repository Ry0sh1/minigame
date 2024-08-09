function REACTIVATE_HEAL(message){
    const healID = parseInt(message.content);
    if (heal.has(healID)){
        const h = heal.get(healID);
        h.active = true;
        heal.set(healID, h);
    }
}