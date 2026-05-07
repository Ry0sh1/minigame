const minimapCanvas = document.getElementById('minimap');
const minimapContext = minimapCanvas.getContext("2d");

let minimapX = 0;
let minimapY = 0;

function drawMiniMap() {
    minimapContext.fillStyle = 'rgb(255,255,255)';
    minimapContext.fillRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    minimapContext.strokeStyle = 'black';
    minimapContext.strokeRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    const visibleWidth = minimapCanvas.width * settings.minimapScale;
    const visibleHeight = minimapCanvas.height * settings.minimapScale;

    minimapX = player.x - visibleWidth / 2;
    minimapY = player.y - visibleHeight / 2;

    if (minimapX < 0) minimapX = 0;
    if (minimapY < 0) minimapY = 0;
    if (minimapX + visibleWidth > map.width) minimapX = map.width - visibleWidth;
    if (minimapY +visibleHeight > map.height) minimapY = map.height - visibleHeight;

    map.obstacles.forEach(drawObstacleOnMiniMap);

    shootingPoints.forEach(drawShootingPoints);
    minimapContext.fillStyle = 'rgb(88,82,106)';
    minimapContext.fillRect((player.x - minimapX) / settings.minimapScale, (player.y - minimapY) / settings.minimapScale, 4, 4);
}

function drawObstacleOnMiniMap(obstacle) {
    minimapContext.fillStyle = settings.obstacleColor;
    minimapContext.fillRect((obstacle.x - minimapX) / settings.minimapScale, (obstacle.y - minimapY) / settings.minimapScale, obstacle.width / settings.minimapScale, obstacle.height / settings.minimapScale);

    minimapContext.strokeStyle = 'black';
    minimapContext.lineWidth = 2;
    minimapContext.strokeRect((obstacle.x  - minimapX) / settings.minimapScale, (obstacle.y - minimapY) / settings.minimapScale, obstacle.width / settings.minimapScale, obstacle.height / settings.minimapScale);
}

function drawShootingPoints(point) {
   minimapContext.fillStyle = "rgb(239,2,2)";
   minimapContext.fillRect((point.x - minimapX) / settings.minimapScale, (point.y - minimapY) / settings.minimapScale,2, 2);
}