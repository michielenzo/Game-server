## Deployment 

Build the docker image:  
`docker build -t game-server .`  
Execute this command from the root of this project

Run the image in a container:  
`docker run -p 8080:8080 --name game-server-container game-server`  
Port 8080 of the container is bound to port 8080 of the host machine.
You can use the -d flag to run it as daemon.

You can run a command inside the container:  
`docker exec game-server-container ls`  
ls here is our example command to run inside the container

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
  "messageType": "sendLobbyStateToClients"
}
```





