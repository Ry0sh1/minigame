function DELETE_BULLET(message){
    if (bullets.has(message.content)) bullets.delete(message.content);
    if (playerBullets.has(message.content)) playerBullets.delete(message.content);
}