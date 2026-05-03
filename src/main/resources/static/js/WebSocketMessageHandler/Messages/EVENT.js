function EVENT(message){
    if (message.content === 'Darkness') {
        currentEvent = message.content;
        player.nearsight = true;
        changeEventLabel("Darkness!");
    }
    if (message.content === 'Destruction') {
        currentEvent = message.content;
        changeEventLabel("Destruction!");
    }
    if (message.content === 'Tower') {
        currentEvent = message.content;
        changeEventLabel("The Tower!");
    }
}

function changeEventLabel(name) {
    document.getElementById('current_event').innerText = name;
    document.getElementById('event_information').classList.remove('hidden');
}