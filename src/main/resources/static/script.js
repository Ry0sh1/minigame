const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");

const player = {
    x: 50,
    y: 50,
    width: 20,
    height: 20,
    speed: 5,
}

function gameLoop(){
    requestAnimationFrame(gameLoop);

    update();
    draw();
}

function update() {

}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "rgb(255,0,0)";
    ctx.fillRect(player.x, player.y, player.width, player.height);
}

document.addEventListener('keypress', (e) => {
    switch (e.key){
        case 'w': player.y-=player.speed; break;
        case 'a': player.x-=player.speed; break;
        case 's': player.y+=player.speed; break;
        case 'd': player.x+=player.speed; break;
    }
});

requestAnimationFrame(gameLoop);