## Deployment 

Build the docker image:  
`docker build -t game-server .`  
Execute this command from the root of this project

Run the image in a container:  
`docker run -p 8080:8080 --name game-server-container game-server`  
Port 8080 of the container is bound to port 8080 of the host machine.
You can use the -d flag to run it as a daemon.

You can run a command inside the container:  
`docker exec game-server-container ls`  
ls here is our example command to run inside the container.

## Websocket messaging

Endpoint:  
`ws://<ip-server>:8080/player`

### sendLobbyStateToClients:

```json
{
  "lobbyState": {
    "gameMode": "Space balls",
    "players": [
      {
        "id": "129e117e-95ff-4a65-9846-e307cf128740",
        "status": "available",
        "name": "129e117e-95ff-4a65-9846-e307cf128740"
      }
    ]
  },
  "yourId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "sendLobbyStateToClients"
}
```

### ChooseNameToServer:

```json
{
  "playerId": "129e117e-95ff-4a65-9846-e307cf128740",
  "chosenName": "Bob the builder",
  "messageType": "chooseNameToServer"
}
```

### StartGameToServer:

```json
{
  "messageType": "startGameToServer"
}
```

### BackToLobbyToServer:

```json
{
  "playerId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "backToLobbyToServer"
}
```

### BackToLobbyToClient:  

```json
{
  "playerId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "backToLobbyToServer"
}
```  
"messageType": "backToLobbyToServer"!!!????? Dit staat nu in de server zo.

### SendInputStateToServer:  

```json
{
  "sessionId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "sendInputStateToServer",
  "wKey": true,
  "aKey": false,
  "sKey": false,
  "dKey": true
}
```  

### SendSpaceBallsGameStateToClients:  

```json
{
  "gameState": {
    "players": [
      {
        "sessionId": "6a472a35-3862-45e9-8842-ce5366b41771",
        "name": "6a472a35-3862-45e9-8842-ce5366b41771",
        "xPosition": 100,
        "yPosition": 500,
        "health": 5,
        "hasShield": false,
        "width": 50,
        "height": 50
      }
    ],
    "fireBalls": [
      {
        "xPosition": 772,
        "yPosition": 479,
        "diameter": 50
      }
    ],
    "powerUps": [
      {
        "type": "inverter",
        "xPosition": 493,
        "yPosition": 493,
        "width": 40,
        "height": 40
      },
      {
        "type": "med_kit",
        "xPosition": 262,
        "yPosition": 522,
        "width": 40,
        "height": 40
      },
      {
        "type": "shield",
        "xPosition": 389,
        "yPosition": 509,
        "width": 40,
        "height": 40
      }
    ]
  },
  "messageType": "sendSpaceBallsGameStateToClients"
}
```