function STOP_EVENT(message){
    if (message.content === 'Darkness'){
        currentEvent = null;
        player.nearsight = false;
        document.getElementById('current_event').classList.add('hidden');
        document.getElementById('current_event').innerText = "";
        document.getElementById('event_information').classList.add('hidden');
    }
    //TODO: Each Event
}