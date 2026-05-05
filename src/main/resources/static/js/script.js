const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext("2d");

const code = localStorage.getItem("code");
let username = localStorage.getItem("username");
let player;
let camera;
let currentEvent = null;

let weaponList = [];

let map;
let obstacleTemp;

let mouseX = 0;
let mouseY = 0;

let players = [];
let bullets = [];
let heals = [];
let powerUps = [];

const keys = ["w","a","s","d"];
const bombs = new Map();
const laserGuns = new Map();

let lastTime = 0;
const fpsInterval = 1000 / settings.fps;

function gameLoop(currentTime){
    requestAnimationFrame(gameLoop);
    const elapsed = currentTime - lastTime;

    if (elapsed > fpsInterval) {
        lastTime = currentTime - (elapsed % fpsInterval);
        draw();
    }
}

function getWeaponSrc(name){
    switch (name) {
        case 'sniper': return "/texture/sniper-side.png";
        case 'shotgun': return "/texture/shotgun-side.png";
        case 'rifle': return "/texture/rifle-side.png";
    }
}

function addChangeWeaponHTML() {
    weaponList.forEach(weapon => {
        let html = `
         <div class="card" id="${weapon.name}" onclick="weaponChange('${weapon.name}')">
            <img src="${getWeaponSrc(weapon.name)}" class="card-img-top card-image" alt="${weapon.name}">
            <div class="card-body">
                <h3 class="card-title text-center">${weapon.name}</h3>
                <div>
                    <ul class="stat-list list-unstyled px-4 py-2 m-auto">
                        <li>
                          <i class='bx bxs-chevrons-right'></i>
                          <span>BulletSpeed</span> 
                          <p>${weapon.speed}</p>
                        </li>
                        <li>
                          <i class='bx bx-revision' ></i>
                          <span>ReloadTime</span>
                          <p>${weapon.reloadFrames}</p>
                        </li>
                        <li>
                          <i class='bx bx-cross' ></i>
                          <span>Damage</span>
                          <p>${weapon.damage}</p>
                        </li>
                        <li>
                          <i class='bx bx-trending-up' ></i>
                          <span>Range</span>
                          <p>${weapon.range}</p>
                        </li>
                    </ul>
                </div>
            </div>
         </div>`;
        document.getElementById('change-weapon').insertAdjacentHTML("beforeend", html);
    })
}

fetch("/get-game-data/" + code, {method: 'GET'})
    .then(response => response.json())
    .then(gameData => {
        map = gameData.mapData;
        weaponList = gameData.weaponList;
        addChangeWeaponHTML();
        obstacleTemp = structuredClone(map.obstacles);
        document.getElementById('map-name').innerText = map.name;
        players = gameData.gameState.players;
        if (players != null) {
            players.forEach(p => {
                addPlayerCard(p);
            });
        }
        connect();
    })
