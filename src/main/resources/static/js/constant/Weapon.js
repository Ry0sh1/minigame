const rifle = {
    name: 'rifle',
    speed: 5,
    range: 240,
    reloadFrames: 10,
    damage: 10,
    src: "/texture/rifle-side.png"
}
const sniper = {
    name: 'sniper',
    speed: 8,
    range: 400,
    reloadFrames: 50,
    damage: 100,
    src: "/texture/sniper-side.png"
}
const shotgun = {
    name: 'shotgun',
    speed: 8,
    range: 165,
    reloadFrames: 30,
    damage: 20,
    scatter: Math.PI / 6, //45 Grad
    bullets: 8,
    src: "/texture/shotgun-side.png"
}
const weapons = [rifle, sniper, shotgun];