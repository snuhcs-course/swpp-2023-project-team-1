from fastapi.testclient import TestClient
from app.main import spire_app
import json
import pytest

from httpx import AsyncClient

access_token = None
refresh_token = None
server_ip_address = "0.0.0.0"
port_num = 8000

@pytest.mark.asyncio
async def test_register():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/register",
            content=json.dumps(
                {
                    "email": "test",
                    "username": "test",
                    "password": "test"
                }
            ),
        )
        assert response.status_code == 200
        response_data = response.json()
        assert isinstance(response_data['access_token'], str)  
        assert isinstance(response_data['refresh_token'], str)  
        assert isinstance(response_data['user_id'], str)  
        assert response_data['username'] == "test"

    global access_token 
    access_token = response_data['access_token']
    global refresh_token 
    refresh_token = response_data['refresh_token']

@pytest.mark.asyncio
async def test_register_duplicated_username():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/register",
            content=json.dumps(
                {
                    "email": "test",
                    "username": "test",
                    "password": "test"
                }
            ),
        )
        assert response.status_code == 400

@pytest.mark.asyncio
async def test_login_correct():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/login",
            content=json.dumps(
                {
                    "email": "test",
                    "password": "test"
                }
            ),
        )
        assert response.status_code == 200
        response_data = response.json()
        assert isinstance(response_data['access_token'], str)  
        assert isinstance(response_data['refresh_token'], str)  
        assert isinstance(response_data['user_id'], str)  
        assert response_data['username'] == "test"

@pytest.mark.asyncio
async def test_login_wrong():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/login",
            content=json.dumps(
                {
                    "email": "test",
                    "password": "test2"
                }
            ),
        )
        assert response.status_code == 401

@pytest.mark.asyncio
async def test_logout_correct():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.get(
            "/api/auth/logout", 
            headers=headers
        )
        assert response.status_code == 200

@pytest.mark.asyncio
async def test_logout_wrong():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:

        response = await ac.get(
            "/api/auth/logout"
        )
        assert response.status_code == 401

@pytest.mark.asyncio
async def test_check_correct():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test", "username": "test"}
        )
        assert response.status_code == 200
        assert response.json() == {
        "email_exists": True,
        "username_exists": True
    }
        
@pytest.mark.asyncio
async def test_check_wrong_email():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test123412", "username": "test"}
        )
        assert response.status_code == 200
        assert response.json() == {
        "email_exists": False,
        "username_exists": True
    }
        
@pytest.mark.asyncio
async def test_check_wrong_username():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test", "username": "test12341243"}
        )
        assert response.status_code == 200
        assert response.json() == {
        "email_exists": True,
        "username_exists": False
    }
        
@pytest.mark.asyncio
async def test_check_wrong_both():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test123412", "username": "test12341234"}
        )
        assert response.status_code == 200
        assert response.json() == {
        "email_exists": False,
        "username_exists": False
    }
        
@pytest.mark.asyncio
async def test_check_wrong_params():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
        )
        assert response.status_code == 400
    
@pytest.mark.asyncio
async def test_verify():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/verify",
            content=json.dumps(
                {
                    "access_token": access_token 
                }
            ),
        )
        assert response.status_code == 200

@pytest.mark.asyncio
async def test_verify():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/verify",
            content=json.dumps(
                {
                    "access_token": access_token 
                }
            ),
        )
        assert response.status_code == 200

@pytest.mark.asyncio
async def test_verify():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/verify",
            content=json.dumps(
                {
                    "access_token": access_token 
                }
            ),
        )
        assert response.status_code == 200

@pytest.mark.asyncio
async def test_unregister_correct():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
        assert response.status_code == 200

@pytest.mark.asyncio
async def test_unregister_wrong():
    async with AsyncClient(base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:

        response = await ac.delete(
            "/api/auth/unregister"
        )
        assert response.status_code == 401

# Code verify testing problem