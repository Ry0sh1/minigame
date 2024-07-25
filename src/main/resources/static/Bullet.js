class Bullet {
    id;
    x;
    y;
    width;
    height;
    speed;
    direction;
    constructor(id,x, y, direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = 5;
        this.height = 5;
        this.speed = 4;
        this.direction = direction;
    }

    move(){
        if (this.direction === 0){
            this.y -= this.speed;
        }
        else if (this.direction === 1){
            this.x += this.speed;
        }
        if (this.direction === 2){
            this.y += this.speed;
        }
        else if (this.direction === 3){
            this.x -= this.speed;
        }
    }
}



// Einmal Keydrücken dann bleibt die richtung zum schießen:::Mann kann nur in eine Richtung immer schießen::: Player speed and Colission