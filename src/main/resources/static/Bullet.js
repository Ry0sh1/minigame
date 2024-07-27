class Bullet {
    id;
    x;
    y;
    radius;
    speed;
    angle;
    constructor(id,x, y, angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = 3;
        this.speed = 8;
        this.angle = angle;
    }

    move(){
        this.x += Math.cos(this.angle) * this.speed;
        this.y += Math.sin(this.angle) * this.speed;
    }
}



