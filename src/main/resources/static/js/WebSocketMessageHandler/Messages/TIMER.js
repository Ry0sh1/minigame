function TIMER(message) {
    let timeInSeconds = 60*8 - parseInt(message.content);
    secondsToReadable(timeInSeconds);
}
function secondsToReadable(timeInSeconds){
    let min = Math.floor(timeInSeconds / 60);
    let sec = timeInSeconds % 60;
    if (sec < 10){
        sec = `0${sec}`;
    }
    document.getElementById("game-timer").innerText = `0${min}:${sec}`;
}