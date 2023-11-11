from fastapi.testclient import TestClient
from app.main import spire_app
import json
import pytest
import pytest_asyncio, asyncio
from httpx import AsyncClient
from app.schemas.user import UserCreate, UserUpdate


access_token = None
refresh_token = None
access_token_1 = None
refresh_token_1 = None
user_id_1 = None
username_1 = None
access_token_2 = None
refresh_token_2 = None
user_id_2 = None
username_2 = None

html = """
<!DOCTYPE html>
<html>
    <head>
        <title>Spire</title>
    </head>
    <body>
        <h1>Hello! I'm Spire API</h1>
    </body>
</html>
"""

server_ip_address = "0.0.0.0"
port_num = 8000

client = TestClient(spire_app)

@pytest_asyncio.fixture(scope='session', autouse=True)
def event_loop(request):
    """Create an instance of the default event loop for each test case."""
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()

@pytest.mark.asyncio
async def test_default():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/"
        )
    assert response.status_code == 200
    assert response.text == html

############## Auth ##############

@pytest.mark.asyncio
async def test_register():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
async def test_login_unauthorized_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.get(
            "/api/auth/logout", 
            headers=headers
        )
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_logout_unauthorized_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:

        response = await ac.get(
            "/api/auth/logout"
        )
    assert response.status_code == 401

@pytest.mark.asyncio
async def test_check_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test", "username": "test"}
        )
    assert response.status_code == 200
    assert response.json() == {
        "email_exists": True,
        "username_exists": True
    }

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test123412", "username": "test"}
        )
    assert response.status_code == 200
    assert response.json() == {
        "email_exists": False,
        "username_exists": True
    }

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
            params={"email": "test", "username": "test12341243"}
        )
    assert response.status_code == 200
    assert response.json() == {
        "email_exists": True,
        "username_exists": False
    }

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
async def test_check_wrong_params_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/auth/check",
        )
    assert response.status_code == 400
    
@pytest.mark.asyncio
async def test_verify_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
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
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_unregister_unauthorized_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:

        response = await ac.delete(
            "/api/auth/unregister"
        )
    assert response.status_code == 401


############## Code verify necessary ##############

############## User ##############
@pytest.mark.asyncio
async def test_register_users_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/register",
            content=json.dumps(
                {
                    "email": "test1",
                    "username": "test1",
                    "password": "test1"
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['access_token'], str)  
    assert isinstance(response_data['refresh_token'], str)  
    assert isinstance(response_data['user_id'], str)  
    assert response_data['username'] == "test1"

    global access_token_1 
    access_token_1 = response_data['access_token']
    global refresh_token_1 
    refresh_token_1 = response_data['refresh_token']
    global user_id_1
    user_id_1 = response_data['user_id']
    global username_1
    username_1 = response_data['username']

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/register",
            content=json.dumps(
                {
                    "email": "test2",
                    "username": "test2",
                    "password": "test2"
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['access_token'], str)  
    assert isinstance(response_data['refresh_token'], str)  
    assert isinstance(response_data['user_id'], str)  
    assert response_data['username'] == "test2"

    global access_token_2 
    access_token_2 = response_data['access_token']
    global refresh_token_2
    refresh_token_2 = response_data['refresh_token']
    global user_id_2
    user_id_2 = response_data['user_id']
    global username_2
    username_2 = response_data['username']

@pytest.mark.asyncio
async def test_get_my_info_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/user/me", 
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['email'], str)  
    assert response_data['id'] == user_id_1
    assert response_data['username'] == username_1
    assert isinstance(response_data['bio'], str) or response_data['bio'] == None
    assert isinstance(response_data['profile_image_url'], str) or response_data['profile_image_url'] == None


############## Patch my info necessary ##############


@pytest.mark.asyncio
async def test_get_user_info_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/user/" + user_id_2, 
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['email'] == "test2"
    assert response_data['id'] == user_id_2
    assert response_data['username'] == username_2
    assert response_data['bio'] == None
    assert response_data['profile_image_url'] == None


@pytest.mark.asyncio
async def test_request_follow_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Requested user " + user_id_2 +  " follow"

@pytest.mark.asyncio
async def test_request_follow_user_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/8e6f6dc9-bfcf-44ac-8081-58db10f4e18c/follow_request",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_FOUND"

@pytest.mark.asyncio
async def test_request_follow_request_already_exists_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__ALREADY_EXISTS" # Should be modified to "FOLLOW__REQUEST_ALREADY_EXISTS"

@pytest.mark.asyncio
async def test_request_follow_myself_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/follow_request",
            headers=headers
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__MYSELF"

@pytest.mark.asyncio
async def test_cancel_request_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/user/" + user_id_2 + "/cancel_request",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Canceled user " + user_id_2 +  " follow request"

@pytest.mark.asyncio
async def test_cancel_request_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/user/" + user_id_2 + "/cancel_request",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__NOT_FOUND"

@pytest.mark.asyncio
async def test_accept_request_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/accept_request",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Accepted user " + user_id_1 +  " follow request"

@pytest.mark.asyncio
async def test_accept_request_already_accepted_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/accept_request",
            headers=headers
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__ALREADY_ACCEPTED"

@pytest.mark.asyncio
async def test_request_follow_already_exists_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__ALREADY_EXISTS"

@pytest.mark.asyncio
async def test_accept_request_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/accept_request",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__NOT_FOUND"


@pytest.mark.asyncio
async def test_cancel_request_wrong_status_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/user/" + user_id_2 + "/cancel_request",
            headers=headers
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__WRONG_STATUS"

@pytest.mark.asyncio
async def test_unfollow_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/user/" + user_id_2 + "/unfollow",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Unfollowed user " + user_id_2

@pytest.mark.asyncio
async def test_reject_follow_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/accept_request",
            headers=headers
        )

        response = await ac.delete(
            "/api/user/" + user_id_1 + "/reject_follow",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Rejected user " + user_id_1 + " follow"

@pytest.mark.asyncio
async def test_reject_follow_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:

        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.delete(
            "/api/user/" + user_id_1 + "/reject_follow",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_follow_info_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/follow_request",
            headers=headers
        )

        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/accept_request",
            headers=headers
        )

        response = await ac.get(
            "/api/user/" + user_id_2 + "/follow_info",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['follower_cnt'] == 1
    assert response_data['following_cnt'] == 0
    assert response_data['follower_status'] == -1
    assert response_data['following_status'] == -1

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/user/" + user_id_1 + "/follow_request",
            headers=headers
        )

        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/user/" + user_id_2 + "/accept_request",
            headers=headers
        )

        response = await ac.get(
            "/api/user/" + user_id_2 + "/follow_info",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['follower_cnt'] == 1
    assert response_data['following_cnt'] == 1
    assert response_data['follower_status'] == 1
    assert response_data['following_status'] == 1

@pytest.mark.asyncio
async def test_get_follow_info_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/user/8e6f6dc9-bfcf-44ac-8081-58db10f4e133/follow_info",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "FOLLOW__NOT_FOUND" # Should modify

@pytest.mark.asyncio
async def test_get_followers_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/user/" + user_id_2 + "/followers",
            params={"limit": 10, "offset": 0}
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 1
    assert response_data['items'][0] == 1
    assert response_data['next_cursor'] == None


@pytest.mark.asyncio
async def test_unregister_user1_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_unregister_user2_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
    assert response.status_code == 200
