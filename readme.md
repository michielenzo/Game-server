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

You can also open a terminal inside the container:  
`docker exec -it game-server-container /bin/bash`

Or deploy it with docker compose:
`docker compose up -d`
The -d flag is used t run i as a deamon.

## Websocket messaging

Endpoint:  
`ws://<ip-server>:8080/player`

### sendRoomStateToClients:

```json
{
  "roomState": {
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
  "messageType": "sendRoomStateToClients"
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

### BackToRoomToServer:

```json
{
  "playerId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "backToRoomToServer"
}
```

### BackToRoomToClient:  

```json
{
  "playerId": "129e117e-95ff-4a65-9846-e307cf128740",
  "messageType": "backToRoomToServer"
}
```  
"messageType": "backToRoomToServer"!!!????? Dit staat nu in de server zo.

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
        "x": 100,
        "y": 500,
        "health": 5,
        "shield": false
      }
    ],
    "meteorites": [
      {
        "x": 772,
        "y": 479
      }
    ],
    "powerUps": [
      {
        "type": "inverter",
        "x": 493,
        "y": 493
      },
      {
        "type": "med_kit",
        "x": 262,
        "y": 522
      },
      {
        "type": "shield",
        "x": 389,
        "y": 509
      }
    ]
  },
  "messageType": "sendSpaceBallsGameStateToClients"
}
```
