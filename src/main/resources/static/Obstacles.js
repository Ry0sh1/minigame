const obstacles  = [
    { x: 40, y: 40, width: 60, height: 5 },
    { x: 40, y: 40, width: 5, height: 60 },
    { x: 140, y: 70, width: 5, height: 60 },
    { x: 250, y: 120, width: 75, height: 20 },
    { x: 430, y: 40, width: 5, height: 60 },
    { x: 743, y: 150, width: 5, height: 60 },
    { x: 754, y: 130, width: 100, height: 10 },
    { x: 854, y: 180, width: 50, height: 15 },
    { x: 654, y: 30, width: 50, height: 10 },
    { x: 524, y: 133, width: 70, height: 5 },
    { x: 54, y: 190, width: 100, height: 15 },
    { x: 206, y: 250, width: 20, height: 75 },
    { x: 354, y: 230, width: 60, height: 10 },
    { x: 400, y: 270, width: 10, height: 75 },
    { x: 470, y: 260, width: 70, height: 5 },
    { x: 534, y: 340, width: 50, height: 15 },
    { x: 634, y: 250, width: 50, height: 15 },
    { x: 774, y: 330, width: 50, height: 15 },
    { x: 854, y: 310, width: 10, height: 55 },
    { x: 914, y: 370, width: 50, height: 5 }
]


const heal = new Map();

heal.set(1,new Heal(1,100,100, 10,10));