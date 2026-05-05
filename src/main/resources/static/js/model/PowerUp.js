class PowerUp {
    id;
    name;
    x;
    y;
    width = settings.powerUpHitBoxWidth;
    height = settings.powerUpHitBoxHeight;
    constructor(id,name,x,y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }


}