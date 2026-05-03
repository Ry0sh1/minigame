function TOWER(message) {
    const split = message.content.split(",");
    const x = split[0];
    const y = split[1];
    const id = split[2];

    let bomb = new Bomb('server', id, x, y, false);

    bombs.set(id, bomb);
}