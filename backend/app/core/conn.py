from uuid import UUID
from fastapi import WebSocket
from starlette.websockets import WebSocketState

class ConnectionManager:
    def __init__(self):
        self.active_connections: dict[str, WebSocket] = {}

    async def connect(self, key: str, websocket: WebSocket):
        await websocket.accept()
        self.active_connections[key] = websocket

    async def disconnect(self, key: str):
        if key in self.active_connections:
            if self.active_connections[key].client_state == WebSocketState.CONNECTED:
                await self.active_connections[key].close()
            del self.active_connections[key]

    async def close_all(self):
        for key in self.active_connections:
            if self.active_connections[key].client_state == WebSocketState.CONNECTED:
                await self.active_connections[key].close()
        self.active_connections = {}


conn_manager = ConnectionManager()
