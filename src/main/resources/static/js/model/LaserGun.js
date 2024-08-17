class LaserGun {
    id;
    x;
    y;
    angle;
    loading;
    loadingFrames;
    beamFrames;
    damage;
    copyPlayers;
    constructor(id,x,y,angle,loading) {
        this.id = id;
        let playerCenterX = (x + player.width / 2);
        let playerCenterY  = (y + player.height / 2);
        this.x = playerCenterX + settings.shootRadius * Math.cos(angle);
        this.y = playerCenterY + settings.shootRadius * Math.sin(angle);

        this.angle = angle;
        this.loading = loading;
        this.loadingFrames = settings.laserGunLoadingFrames;
        this.beamFrames = settings.laserGunBeamFrames;
        this.damage = settings.laserGunDamage;
        this.copyPlayers = new Map(players);
    }
    draw(){
        let beamEndX = this.x - camera.x + Math.cos(this.angle) * Math.max(map.width, map.height);
        let beamEndY = this.y - camera.y + Math.sin(this.angle) * Math.max(map.width, map.height);
        if (this.loading){
            ctx.strokeStyle = 'rgba(255, 0, 0, 0.5)';
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(this.x - camera.x, this.y - camera.y);
            ctx.lineTo(beamEndX, beamEndY);
            ctx.stroke();
        }else {
            ctx.strokeStyle = 'rgba(0,223,255,0.5)';
            ctx.lineWidth = 60;
            ctx.beginPath();
            ctx.moveTo(this.x - camera.x, this.y - camera.y);
            ctx.lineTo(beamEndX, beamEndY);
            ctx.stroke();
        }
    }
    update(){
        if (this.loading){
            this.loadingFrames -= 1;
            if (this.loadingFrames <= 0) this.loading = false;
        }else {
            this.beamFrames -= 1;
            if (this.beamFrames <= 0) {
                laserGuns.delete(this.id);
                return;
            }

            for (let [key,value] of this.copyPlayers){
                if (this.isPlayerInBeam(value)){
                    stompClient.send("/app/game.player-hit/" + code,
                        {},
                        JSON.stringify({type: 'PLAYER_HIT', player: player.username, content: value.username + "," + this.damage, code: code})
                    );
                    this.copyPlayers.delete(key);
                }
            }
        }
    }

    isPlayerInBeam(p) {
        // Spieler-Koordinaten (Mittelpunkt)
        let playerCenterX = p.x + p.width / 2;
        let playerCenterY = p.y + p.height / 2;

        // Start- und Endkoordinaten des Laserstrahls
        let beamStartX = this.x;
        let beamStartY = this.y;
        let beamEndX = beamStartX + Math.cos(this.angle) * Math.max(map.width, map.height);
        let beamEndY = beamStartY + Math.sin(this.angle) * Math.max(map.width, map.height);

        // Vektor des Strahls
        let beamDirX = beamEndX - beamStartX;
        let beamDirY = beamEndY - beamStartY;

        // Vektor von Start des Strahls zum Spieler
        let playerDirX = playerCenterX - beamStartX;
        let playerDirY = playerCenterY - beamStartY;

        // Projektion des Spieler-Vektors auf den Strahl-Vektor (wie weit entlang des Strahls)
        let projection = (playerDirX * beamDirX + playerDirY * beamDirY) /
            (beamDirX * beamDirX + beamDirY * beamDirY);

        // Position des Punktes auf dem Strahl, das dem Spieler am nächsten liegt
        let closestX = beamStartX + projection * beamDirX;
        let closestY = beamStartY + projection * beamDirY;

        // Abstand des Spielers zu diesem Punkt
        let distToBeam = Math.hypot(closestX - playerCenterX, closestY - playerCenterY);

        // Prüfung ob der Spieler innerhalb der Breite des Strahls ist
        let halfBeamWidth = 30;
        if (distToBeam <= halfBeamWidth) {
            // Zusätzlich sicherstellen, dass der Spieler innerhalb der Länge des Strahls ist
            if (projection >= 0 && projection <= 1) {
                return true;
            }
        }

        return false;
    }
}