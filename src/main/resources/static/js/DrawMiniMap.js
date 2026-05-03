const minimapCanvas = document.getElementById('minimap');
const minimapContext = minimapCanvas.getContext("2d");

class ShootingPoint {
    x;
    y;
    frame;

    constructor(x,y) {
        this.frame = settings.minimapShootingPointVisibleFrames;
        this.x = x;
        this.y = y;
    }
}

const points = [];

function drawMiniMap() {
    minimapContext.fillStyle = 'rgb(255,255,255)';
    minimapContext.fillRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    minimapContext.strokeStyle = 'black';
    minimapContext.strokeRect(0, 0, minimapCanvas.width, minimapCanvas.height);

    map.obstacles.forEach(drawObstacleOnMiniMap);

    drawShootingPoints();

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

function addShootingPoint(x, y) {
    points.push(new ShootingPoint(x,y));
}

function drawShootingPoints() {
    for (let i = 0; i < points.length; i++) {
        if (points[i].frame > 0) {
            minimapContext.fillStyle = "rgb(239,2,2)";
            minimapContext.fillRect(points[i].x / settings.minimapScale, points[i].y / settings.minimapScale,2, 2);
            points[i].frame--;
        } else {
            points.splice(i, 1);
        }
    }
}