function EVENT(message){
    if (message.content === 'Darkness'){
        currentEvent = message.content;
        player.nearsight = true;
        document.getElementById('current_event').innerText = "Darkness!"
        document.getElementById('event_information').classList.remove('hidden');
    }
    //TODO: Each Event
}