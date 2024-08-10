function EVENT(message){
    if (message.content === 'Darkness'){
        currentEvent = message.content;
        player.nearsight = true;
        document.getElementById('current_event').innerText = "Darkness!"
        document.getElementById('event_information').classList.remove('hidden');
        document.getElementById('current_event_timer').innerText = '30';
    }
    //TODO: Each Event
}

function stopEvent(){
    if (currentEvent === 'Darkness'){
        player.nearsight = false;
    }
}