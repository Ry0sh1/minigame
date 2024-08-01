function drawVision() {
    ctx.strokeStyle = "rgba(0,0,0,0.5)";
    ctx.lineWidth = 1;

    let playerVisionAngle = 40;

    let playerCenterX = (player.x + player.width / 2) - camera.x;
    let playerCenterY = (player.y + player.height / 2) - camera.y;

    let angle = Math.atan2(mouseY - playerCenterY, mouseX - playerCenterX);

    let startAngle = angle + playerVisionAngle * Math.PI / 180;
    let endAngle = angle - playerVisionAngle * Math.PI / 180;

    let length = 200;
    let numLines = 80;
    let angleStep = (startAngle - endAngle) / (numLines - 1);

    let lines = [];

    for (let i = 0; i < numLines; i++) {
        let currentAngle = startAngle - i * angleStep;
        let x2 = playerCenterX + length * Math.cos(currentAngle);
        let y2 = playerCenterY + length * Math.sin(currentAngle);
        let line = new Line(playerCenterX, playerCenterY, x2, y2);

        for (let obstacle of obstacles) {
            let collision = false;
            let intersection = lineIntersectsRect(line, obstacle);
            if (intersection !== null) {
                collision = true;
                x2 = intersection.x;
                y2 = intersection.y;
                break;
            }
        }

        lines.push(new Line(playerCenterX, playerCenterY, x2, y2));
    }

    ctx.beginPath();
    ctx.arc(playerCenterX, playerCenterY, settings.playerVisionRadius, startAngle, endAngle);
    ctx.lineTo(playerCenterX, playerCenterY);
    for (let i = 0; i < lines.length - 1; i++){
        ctx.moveTo(playerCenterX, playerCenterY);
        ctx.lineTo(lines[i].x2, lines[i].y2);
        ctx.lineTo(lines[i+1].x2, lines[i+1].y2);
    }
    ctx.closePath();
    ctx.clip();
}

function lineIntersectsRect(line, obstacle) {
    let left = new Line(obstacle.x - camera.x, obstacle.y - camera.y - camera.y, obstacle.x - camera.x, obstacle.y + obstacle.height - camera.y);
    let top = new Line(obstacle.x - camera.x, obstacle.y - camera.y - camera.y, obstacle.x + obstacle.width - camera.x, obstacle.y - camera.y);
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