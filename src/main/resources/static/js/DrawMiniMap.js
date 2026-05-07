const minimapCanvas = document.getElementById('minimap');
const minimapContext = minimapCanvas.getContext("2d");

function drawMiniMap() {
    minimapContext.fillStyle = 'rgb(255,255,255)';
    minimapContext.fillRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    minimapContext.strokeStyle = 'black';
    minimapContext.strokeRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    map.obstacles.forEach(drawObstacleOnMiniMap);

    shootingPoints.forEach(drawShootingPoints);
    minimapContext.fillStyle = 'rgb(88,82,106)';
    minimapContext.fillRect(player.x / settings.minimapScale, player.y / settings.minimapScale, 4, 4);
}

function drawObstacleOnMiniMap(obstacle) {
    minimapContext.fillStyle = settings.obstacleColor;
    minimapContext.fillRect(obstacle.x / settings.minimapScale, obstacle.y / settings.minimapScale, obstacle.width / settings.minimapScale, obstacle.height / settings.minimapScale);

    minimapContext.strokeStyle = 'black';
    minimapContext.lineWidth = 2;
    minimapContext.strokeRect(obstacle.x / settings.minimapScale, obstacle.y / settings.minimapScale, obstacle.width / settings.minimapScale, obstacle.height / settings.minimapScale);
}

function drawShootingPoints(point) {
   minimapContext.fillStyle = "rgb(239,2,2)";
   minimapContext.fillRect(point.x / settings.minimapScale, point.y / settings.minimapScale,2, 2);
}