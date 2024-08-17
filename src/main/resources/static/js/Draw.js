function draw() {
    ctx.fillStyle = 'rgb(51,51,51)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    drawVision();

    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    for (let [key, value] of players) {
        if (value.alive){
            ctx.save();
            ctx.translate(value.x - camera.x + 6, value.y - camera.y + 6);
            ctx.rotate(value.angle + Math.PI / 2);
            let currentImg = characterRifle;
            switch (value.weapon) {
                case shotgun: currentImg = characterShotgun; break;
                case sniper: currentImg = characterSniper; break;
                case rifle: currentImg = characterRifle; break;
            }
            ctx.drawImage(currentImg, -12, -30, currentImg.width, currentImg.height);
            ctx.restore();
        }
    }

    ctx.fillStyle = "rgb(18,116,2)";
    for (let [key, value] of bullets) {
        ctx.beginPath();
        ctx.arc(value.x - camera.x, value.y - camera.y, value.radius, 0, Math.PI * 2);
        ctx.fill();
    }
    for (let [key, value] of playerBullets) {
        ctx.beginPath();
        ctx.arc(value.x - camera.x, value.y - camera.y, value.radius, 0, Math.PI * 2);
        ctx.fill();
    }
    for (let [key, value] of heal){
        if (value.active){
            ctx.drawImage(healImage, value.x - camera.x, value.y - camera.y, healImage.width, healImage.height);
        }
    }
    for (let [key, value] of powerUps){
        value.drawPowerup(ctx);
    }
    for (let [key, value] of bombs){
        value.draw();
    }
    for (let [key, value] of laserGuns){
        value.draw();
    }
    ctx.restore();
    map.obstacles.forEach(drawObstacle);
}
function drawObstacle(obstacle) {
    ctx.fillStyle = "rgb(93,120,85)";
    ctx.fillRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);

    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.strokeRect(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.width, obstacle.height);
}
function drawVision() {
    ctx.strokeStyle = "rgba(0,0,0,0.5)";
    ctx.lineWidth = 1;

    let playerCenterX = (player.x + player.width / 2) - camera.x;
    let playerCenterY = (player.y + player.height / 2) - camera.y;

    let startAngle = player.angle + settings.playerVisionAngle * Math.PI / 180;
    let endAngle = player.angle - settings.playerVisionAngle * Math.PI / 180;

    let angleStep = (startAngle - endAngle) / (settings.playerVisionSharpness - 1);

    let lines = [];

    for (let i = 0; i < settings.playerVisionSharpness && player.nearsight !== true; i++) {
        let currentAngle = startAngle - i * angleStep;
        let x2 = playerCenterX + settings.playerVisionLength * Math.cos(currentAngle);
        let y2 = playerCenterY + settings.playerVisionLength * Math.sin(currentAngle);
        let line = new Line(playerCenterX, playerCenterY, x2, y2);

        for (let obstacle of map.obstacles) {
            let collision = false;
            let intersection = lineIntersectsRect(line, obstacle);
            if (intersection !== null) {
                collision = true;
                let currentIntersectionDistance = distance(playerCenterX, playerCenterY, x2, y2);
                let newDis = distance(playerCenterX, playerCenterY, intersection.x, intersection.y);
                if (currentIntersectionDistance > newDis){
                    x2 = intersection.x;
                    y2 = intersection.y;
                }
            }
        }

        lines.push(new Line(playerCenterX, playerCenterY, x2, y2));
    }

    ctx.beginPath();
    if (!player.nearsight){
        ctx.arc(playerCenterX, playerCenterY, settings.playerVisionRadius, startAngle, endAngle);
        ctx.lineTo(playerCenterX, playerCenterY);
        for (let i = 0; i < lines.length - 1; i++){
            ctx.moveTo(playerCenterX, playerCenterY);
            ctx.lineTo(lines[i].x2, lines[i].y2);
            ctx.lineTo(lines[i+1].x2, lines[i+1].y2);
        }
    }else {
        ctx.arc(playerCenterX, playerCenterY, settings.playerVisionRadius, 0, Math.PI * 2)
    }
    ctx.closePath();
    ctx.clip();
}

function lineIntersectsRect(line, obstacle) {
    let left = new Line(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.x - camera.x, obstacle.y + obstacle.height - camera.y);
    let top = new Line(obstacle.x - camera.x, obstacle.y - camera.y, obstacle.x + obstacle.width - camera.x, obstacle.y - camera.y);
    let right = new Line(obstacle.x + obstacle.width - camera.x, obstacle.y + obstacle.height - camera.y, obstacle.x + obstacle.width - camera.x, obstacle.y - camera.y);
    let bot = new Line(obstacle.x + obstacle.width - camera.x, obstacle.y + obstacle.height - camera.y, obstacle.x - camera.x, obstacle.y + obstacle.height- camera.y);

    let intersections = [
        getIntersection(line, left),
        getIntersection(line, top),
        getIntersection(line, right),
        getIntersection(line, bot)
    ];

    intersections = intersections.filter(point => point !== null)
        .sort((a, b) => distance(line.x1, line.y1, a.x, a.y) - distance(line.x1, line.y1, b.x, b.y));

    return intersections.length > 0 ? intersections[0] : null;
}

function crossProduct(a, b){
    return a.x * b.y - a.y * b.x;
}

function onSegment(p, q, r) {
    return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
        q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
}

function getIntersection(line1, line2) {
    let p1 = { x: line1.x1, y: line1.y1 };
    let q1 = { x: line1.x2, y: line1.y2 };
    let p2 = { x: line2.x1, y: line2.y1 };
    let q2 = { x: line2.x2, y: line2.y2 };

    let d1 = { x: q1.x - p1.x, y: q1.y - p1.y };
    let d2 = { x: q2.x - p2.x, y: q2.y - p2.y };

    let cross1 = this.crossProduct({ x: p2.x - p1.x, y: p2.y - p1.y }, d1);
    let cross2 = this.crossProduct({ x: q2.x - p1.x, y: q2.y - p1.y }, d1);
    let cross3 = this.crossProduct({ x: p1.x - p2.x, y: p1.y - p2.y }, d2);
    let cross4 = this.crossProduct({ x: q1.x - p2.x, y: q1.y - p2.y }, d2);

    if (cross1 * cross2 < 0 && cross3 * cross4 < 0) {
        let t = this.crossProduct({ x: p2.x - p1.x, y: p2.y - p1.y }, d2) / this.crossProduct(d1, d2);
        let intersectionX = p1.x + t * d1.x;
        let intersectionY = p1.y + t * d1.y;
        return { x: intersectionX, y: intersectionY };
    }

    if (cross1 === 0 && this.onSegment(p1, p2, q1)) return p2;
    if (cross2 === 0 && this.onSegment(p1, q2, q1)) return q2;
    if (cross3 === 0 && this.onSegment(p2, p1, q2)) return p1;
    if (cross4 === 0 && this.onSegment(p2, q1, q2)) return q1;

    return null;
}
function distance(x1, y1, x2, y2) {
    return Math.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2);
}