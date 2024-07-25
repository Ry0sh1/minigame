class Bullet {
    x;
    y;
    width;
    height;
    speed;
    direction;
    constructor(x, y, direction) {
        this.x = x;
        this.y = y;
        this.width = 5;
        this.height = 5;
        this.speed = 4;
        this.direction = direction;
    }

    move(){
        if (this.direction === "ArrowDown"){
            this.y += this.speed;
        }else if (this.direction ==="ArrowUp"){
            this.y -= this.speed;
        }else if (this.direction ==="ArrowRight"){
            this.x += this.speed;
        }else if (this.direction ==="ArrowLeft"){
            this.x -= this.speed;
        }
    }
}



// Einmal Keydrücken dann bleibt die richtung zum schießen:::Mann kann nur in eine Richtung immer schießen::: Player speed and Colission