const rifle = {
    name: 'rifle',
    speed: 5,
    range: 240,
    reloadFrames: 10,
    damage: 20
}
const sniper = {
    name: 'sniper',
    speed: 8,
    range: 400,
    reloadFrames: 40,
    damage: 60
}
const shotgun = {
    name: 'shotgun',
    speed: 8,
    range: 140,
    reloadFrames: 30,
    damage: 20,
    scatter: Math.PI / 8, //45 Grad
    bullets: 8
}