from fastapi.testclient import TestClient
from app.main import spire_app
import json
import pytest
import pytest_asyncio, asyncio
from httpx import AsyncClient
from tests.test_base64 import test_base64
from app.core.config import settings as config

fake_uuid4 = "8e6f6dc9-bfcf-44ac-8081-58db10f4e18c"

access_token = None
refresh_token = None
access_token_1 = None
refresh_token_1 = None
user_id_1 = None
username_1 = None
email_1 = "test1@gmail.com"
profile_image_url_1 = None
access_token_2 = None
refresh_token_2 = None
user_id_2 = None
username_2 = None
email_2 = "test2@gmail.com"
profile_image_url_2 = None

post_1 = {
    "content": "string",
    "image_url": "string",
    "origin_image_url": "string",
    "mask_image_url": "string",
    "id": "string",
    "created_at": "2023-11-11T13:56:04.008Z",
    "updated_at": "2023-11-11T13:56:04.008Z",
    "user": {
    "id": "string",
    "username": "string",
    "profile_image_url": "string"
    },
    "like_cnt": 0,
    "comment_cnt": 0,
    "is_liked": -1
}

post_2 = {
    "content": "string",
    "image_url": "string",
    "origin_image_url": "string",
    "mask_image_url": "string",
    "id": "string",
    "created_at": "2023-11-11T13:56:04.008Z",
    "updated_at": "2023-11-11T13:56:04.008Z",
    "user": {
    "id": "string",
    "username": "string",
    "profile_image_url": "string"
    },
    "like_cnt": 0,
    "comment_cnt": 0,
    "is_liked": -1
}

comment_id = None
notification_id = None

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
async def test_verify_token_correct():
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
async def test_refresh_token_correct():
    global access_token, refresh_token

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/refresh",
            content=json.dumps(
                {
                    "access_token": access_token,
                    "refresh_token": refresh_token
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['access_token'], str)
    access_token = response_data['access_token']
    assert isinstance(response_data['refresh_token'], str)
    refresh_token = response_data['refresh_token']

# depends on email connection
@pytest.mark.asyncio
async def test_send_email_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/email",
            content=json.dumps(
                {
                    "email": [config.TESTING_MAIL]
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Email sent to email=['{config.TESTING_MAIL}']"

@pytest.mark.asyncio
async def test_verify_code_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/verify/code",
            content=json.dumps(
                {
                    "email": "test",
                    "code": 123456
                }
            ),
        )
    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "CODE__NOT_FOUND"

@pytest.mark.asyncio
async def test_verify_password_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.post(
            "/api/auth/verify/password",
            params={"password": "test"},
            headers=headers
        )
    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Password verified successfully"

@pytest.mark.asyncio
async def test_verify_password_wrong_password_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.post(
            "/api/auth/verify/password",
            params={"password": "test1"},
            headers=headers
        )

    assert response.status_code == 401
    response_data = response.json()
    assert response_data['message'] == "USER__PASSWORD_DOES_NOT_MATCH"

@pytest.mark.asyncio
async def test_change_password_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.patch(
            "/api/auth/password",
            params={"new_password": "testpassword"},
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Password changed successfully"

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token)
        }

        response = await ac.post(
            "/api/auth/verify/password",
            params={"password": "testpassword"},
            headers=headers
        )
    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Password verified successfully"

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


############## User ##############
@pytest.mark.asyncio
async def test_register_users_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.post(
            "/api/auth/register",
            content=json.dumps(
                {
                    "email": "test1@gmail.com",
                    "username": "test_user_1",
                    "password": "test1"
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['access_token'], str)  
    assert isinstance(response_data['refresh_token'], str)  
    assert isinstance(response_data['user_id'], str)  
    assert response_data['username'] == "test_user_1"

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
                    "email": "test2@gmail.com",
                    "username": "test_user_2",
                    "password": "test2"
                }
            ),
        )
    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['access_token'], str)  
    assert isinstance(response_data['refresh_token'], str)  
    assert isinstance(response_data['user_id'], str)  
    assert response_data['username'] == "test_user_2"

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
    assert response_data['email'] == email_1
    assert response_data['id'] == user_id_1
    assert response_data['username'] == username_1
    assert isinstance(response_data['bio'], str) or response_data['bio'] == None
    assert isinstance(response_data['profile_image_url'], str) or response_data['profile_image_url'] == None

@pytest.mark.asyncio
async def test_patch_my_info_correct():
    global username_1, profile_image_url_1, username_2
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        user_update_data = json.dumps({
            "username": "test_user_1",
            "bio": "test1",
            "profile_image_url": "test1"
        })
        
        files = {
            'user_update': (None, user_update_data, 'application/json'),
            'file': ('test_image.png', open("tests/test_image.png", "rb"), 'image/png')
        }

        response = await ac.patch(
            "/api/user/me", 
            headers=headers,
            files=files)

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['email'] == email_1
    assert response_data['id'] == user_id_1
    assert response_data['username'] == username_1
    assert response_data['bio'] == "test1"
    assert isinstance(response_data['profile_image_url'], str)
    profile_image_url_1 = response_data['profile_image_url']

@pytest.mark.asyncio
async def test_patch_my_info_without_file_correct():
    global username_1, profile_image_url_1, username_2
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        user_update_data = json.dumps({
            "username": "test_user_1",
            "bio": "test1 bio",
            "profile_image_url": "test1"
        })
        
        files = {
            'user_update': (None, user_update_data, 'application/json')
        }

        response = await ac.patch(
            "/api/user/me", 
            headers=headers,
            files=files)

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['email'] == email_1
    assert response_data['id'] == user_id_1
    assert response_data['username'] == username_1
    assert response_data['bio'] == "test1 bio"
    assert response_data['profile_image_url'] == profile_image_url_1

@pytest.mark.asyncio
async def test_patch_my_info_duplicated_username_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        user_update_data = json.dumps({
            "username": "test_user_2",
            "bio": "test1",
            "profile_image_url": "test1"
        })
        
        files = {
            'user_update': (None, user_update_data, 'application/json')
        }

        response = await ac.patch(
            "/api/user/me", 
            headers=headers,
            files=files)


    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == 'USER__DUPLICATE_EMAIL_OR_USERNAME'


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
    assert response_data['email'] == email_2
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
            "/api/user/" + fake_uuid4 + "/follow_request",
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
    assert response_data['message'] == "FOLLOW__REQUEST_ALREADY_EXISTS"

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
async def test_reject_request_correct():
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

        response = await ac.delete(
            "/api/user/" + user_id_1 + "/reject_request",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == "Rejected user " + user_id_1 + " follow request"

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
            "/api/user/" + fake_uuid4 + "/follow_info",
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_FOUND" 

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
    assert response_data['items'][0]['id'] == user_id_1
    assert response_data['items'][0]['username'] == username_1
    assert response_data['next_cursor'] == None

@pytest.mark.asyncio
async def test_get_followers_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/user/"+ fake_uuid4 + "/followers",
            params={"limit": 10, "offset": 0}
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_followings_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/user/" + user_id_2 + "/followings",
            params={"limit": 10, "offset": 0}
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 1
    assert response_data['items'][0]['id'] == user_id_1
    assert response_data['items'][0]['username'] == username_1
    assert response_data['next_cursor'] == None

@pytest.mark.asyncio
async def test_get_followings_not_found_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        response = await ac.get(
            "/api/user/" + fake_uuid4 + "/followings",
            params={"limit": 10, "offset": 0}
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_FOUND"

@pytest.mark.asyncio
async def test_search_user_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/search/user/test",
            params={"limit": 10, "offset": 0}, 
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 2
    assert response_data['items'][0]['id'] == user_id_1
    assert response_data['items'][0]['username'] == username_1
    assert response_data['items'][0]['profile_image_url'] == profile_image_url_1
    assert response_data['items'][0]['is_following'] == False
    assert response_data['items'][0]['is_follower'] == False
    assert response_data['items'][1]['id'] == user_id_2
    assert response_data['items'][1]['username'] == username_2
    assert response_data['items'][1]['profile_image_url'] == None
    assert response_data['items'][1]['is_following'] == True
    assert response_data['items'][1]['is_follower'] == True
    assert response_data['next_cursor'] == None
    

############## Post ##############

@pytest.mark.asyncio
async def test_create_post_with_mask_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/post",
            headers=headers, 
            content=json.dumps(
                {
                    "post": {
                        "content": "test",
                        "image_url": "test"
                    },
                    "image": {
                        "modified_image": test_base64,
                        "origin_image": test_base64,
                        "mask_image": test_base64,
                        "prompt": "test"
                    }
                }
            ),
        )

    assert response.status_code == 200
    response_data = response.json()
    global post_1
    assert response_data['content'] == "test"
    post_1['content'] = response_data['content']
    assert isinstance(response_data['image_url'], str)
    post_1['image_url'] = response_data['image_url']
    assert isinstance(response_data['origin_image_url'], str)
    post_1['origin_image_url'] = response_data['origin_image_url']
    assert isinstance(response_data['mask_image_url'], str)
    post_1['mask_image_url'] = response_data['mask_image_url']
    assert isinstance(response_data['id'], str)
    post_1['id'] = response_data['id']
    assert isinstance(response_data['created_at'], str)
    post_1['created_at'] = response_data['created_at']
    assert isinstance(response_data['updated_at'], str)
    post_1['updated_at'] = response_data['updated_at']
    assert response_data['user']['id'] == user_id_1
    post_1['user']['id'] = response_data['user']['id']
    assert response_data['user']['username'] == username_1
    post_1['user']['username'] = response_data['user']['username']
    assert response_data['user']['profile_image_url'] == profile_image_url_1
    post_1['user']['profile_image_url'] = response_data['user']['profile_image_url']
    assert response_data['like_cnt'] == 0
    post_1['like_cnt'] = response_data['like_cnt']
    assert response_data['comment_cnt'] == 0
    post_1['comment_cnt'] = response_data['comment_cnt']
    assert response_data['is_liked'] == -1
    post_1['is_liked'] = response_data['is_liked']

@pytest.mark.asyncio
async def test_create_post_only_with_prompt_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/post",
            headers=headers, 
            content=json.dumps(
                {
                    "post": {
                        "content": "test",
                        "image_url": "test"
                    },
                    "image": {
                        "modified_image": "",
                        "origin_image": "",
                        "mask_image": "",
                        "prompt": "test"
                    }
                }
            ),
        )

    assert response.status_code == 200
    response_data = response.json()
    global post_2
    assert response_data['content'] == "test"
    post_2['content'] = response_data['content']
    assert isinstance(response_data['image_url'], str)
    post_2['image_url'] = response_data['image_url']
    assert isinstance(response_data['id'], str)
    post_2['id'] = response_data['id']
    assert isinstance(response_data['created_at'], str)
    post_2['created_at'] = response_data['created_at']
    assert isinstance(response_data['updated_at'], str)
    post_2['updated_at'] = response_data['updated_at']
    assert response_data['user']['id'] == user_id_2
    post_2['user']['id'] = response_data['user']['id']
    assert response_data['user']['username'] == username_2
    post_2['user']['username'] = response_data['user']['username']
    assert response_data['user']['profile_image_url'] == profile_image_url_2
    post_2['user']['profile_image_url'] = response_data['user']['profile_image_url']
    assert response_data['like_cnt'] == 0
    post_2['like_cnt'] = response_data['like_cnt']
    assert response_data['comment_cnt'] == 0
    post_2['comment_cnt'] = response_data['comment_cnt']
    assert response_data['is_liked'] == -1
    post_2['is_liked'] = response_data['is_liked']

@pytest.mark.asyncio
async def test_create_post_invlaid_post_image_wrong():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/post",
            headers=headers, 
            content=json.dumps(
                {
                    "post": {
                        "content": "test",
                        "image_url": "test"
                    },
                    "image": {
                        "modified_image": "string",
                        "origin_image": "string",
                        "mask_image": "string",
                        "prompt": "test"
                    }
                }
            ),
        )

    assert response.status_code == 400
    response_data = response.json()
    assert response_data['message'] == "INVALID__POST__IMAGE" # bug

@pytest.mark.asyncio
async def test_get_post_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post/" + post_1['id'],
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['content'] == post_1['content']
    assert response_data['image_url'] == post_1['image_url']
    assert response_data['origin_image_url'] == post_1['origin_image_url']
    assert response_data['mask_image_url'] == post_1['mask_image_url']
    assert response_data['id'] == post_1['id']
    assert response_data['created_at'] == post_1['created_at']
    assert response_data['updated_at'] == post_1['updated_at']
    assert response_data['user']['id'] == post_1['user']['id']
    assert response_data['user']['username'] == post_1['user']['username']
    assert response_data['user']['profile_image_url'] == post_1['user']['profile_image_url']
    assert response_data['like_cnt'] == post_1['like_cnt']
    assert response_data['comment_cnt'] == post_1['comment_cnt']
    assert response_data['is_liked'] == post_1['is_liked']

@pytest.mark.asyncio
async def test_get_post_not_found_wrong():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post/" + fake_uuid4,
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "POST__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_my_posts_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post/me" ,
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 1
    assert isinstance(response_data['items'], list)
    assert response_data['next_cursor'] == None

@pytest.mark.asyncio
async def test_get_other_user_posts_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post/user/" + user_id_2,
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 1
    assert isinstance(response_data['items'], list)
    assert response_data['next_cursor'] == None

# # unnecssary
# @pytest.mark.asyncio
# async def test_get_other_user_posts_not_found_wrong():
#     global post_1
#     async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
#         headers = {
#             'Authorization': 'Bearer {}'.format(access_token_1)
#         }

#         response = await ac.get(
#             "/api/post/user/" + fake_uuid4,
#             headers=headers
#         )

#     assert response.status_code == 404
#     response_data = response.json()
#     assert response_data['message'] == "USER__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_posts_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 2
    assert isinstance(response_data['items'], list)
    assert response_data['next_cursor'] == None

@pytest.mark.asyncio
async def test_update_post_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.patch(
            "/api/post/" + post_1['id'],
            headers=headers, 
            content=json.dumps(
                {
                    "content": "update test", 
                    "image_url": post_1['image_url'], 
                    "origin_image_url": post_1['origin_image_url'],
                    "mask_image_url": post_1['mask_image_url']

                }
            ),
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['content'] == "update test"
    post_1['content'] = response_data['content']
    assert response_data['image_url'] == post_1['image_url']
    assert response_data['origin_image_url'] == post_1['origin_image_url']
    assert response_data['mask_image_url'] == post_1['mask_image_url']
    assert response_data['id'] == post_1['id']
    assert response_data['created_at'] == post_1['created_at']
    assert response_data['updated_at'] != post_1['updated_at']
    post_1['updated_at'] = response_data['updated_at']
    assert response_data['user']['id'] == post_1['user']['id']
    assert response_data['user']['username'] == post_1['user']['username']
    assert response_data['user']['profile_image_url'] == post_1['user']['profile_image_url']
    assert response_data['like_cnt'] == post_1['like_cnt']
    assert response_data['comment_cnt'] == post_1['comment_cnt']
    assert response_data['is_liked'] == post_1['is_liked']

@pytest.mark.asyncio
async def test_like_post_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/post/" + post_2['id'] + "/like",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['user_id'] == user_id_1
    assert response_data['is_liked'] == 1
    assert response_data['post_id'] == post_2['id']
    assert isinstance(response_data['created_at'], str)
    assert isinstance(response_data['updated_at'], str)

@pytest.mark.asyncio
async def test_like_post_toggle_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/post/" + post_2['id'] + "/like",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['user_id'] == user_id_1
    assert response_data['is_liked'] == 0
    assert response_data['post_id'] == post_2['id']
    assert isinstance(response_data['created_at'], str)
    assert isinstance(response_data['updated_at'], str)

@pytest.mark.asyncio
async def test_create_comment_correct():
    global post_1
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.post(
            "/api/post/" + post_1['id'] + "/comment",
            headers=headers, 
            content=json.dumps(
                {
                    "content": "test"
                }
            )
        )

    assert response.status_code == 200
    response_data = response.json()
    global comment_id
    comment_id = response_data['id']

@pytest.mark.asyncio
async def test_update_comment_correct():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.patch(
            "/api/post/comment/" + comment_id,
            headers=headers, 
            content=json.dumps(
                {
                    "content": "update"
                }
            )
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['content'] == "update"

@pytest.mark.asyncio
async def test_update_comment_forbidden_wrong():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.patch(
            "/api/post/comment/" + comment_id,
            headers=headers, 
            content=json.dumps(
                {
                    "content": "update"
                }
            )
        )

    assert response.status_code == 403
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_OWNER" # bug

@pytest.mark.asyncio
async def test_update_comment_not_found_wrong():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.patch(
            "/api/post/comment/" + fake_uuid4,
            headers=headers, 
            content=json.dumps(
                {
                    "content": "update"
                }
            )
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "COMMENT__NOT_FOUND"

@pytest.mark.asyncio
async def test_like_comment_correct():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/post/comment/" + comment_id + "/like",
            headers=headers, 
            params={"post_id": post_1['id']}
        )

    assert response.status_code == 200
    response_data = response.json()
    print(response_data)

@pytest.mark.asyncio
async def test_like_comment_toggle_correct():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.post(
            "/api/post/comment/" + comment_id + "/like",
            headers=headers, 
            params={"post_id": post_1['id']}
        )

    assert response.status_code == 200
    response_data = response.json()
    print(response_data)

# # unnecssary
# @pytest.mark.asyncio
# async def test_like_comment_not_found_wrong():
#     global comment_id
#     async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
#         headers = {
#             'Authorization': 'Bearer {}'.format(access_token_2)
#         }

#         response = await ac.post(
#             "/api/post/comment/" + fake_uuid4 + "/like",
#             headers=headers, 
#             params={"post_id": post_1['id']}
#         )

#     assert response.status_code == 404
#     response_data = response.json()
#     assert response_data['message'] == "COMMENT__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_comments_correct():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/post/" + post_1['id'] + "/comment",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 1
    assert isinstance(response_data['items'], list)
    assert response_data['next_cursor'] == None

# # unnecssary
# @pytest.mark.asyncio
# async def test_get_comments_not_found_wrong():
#     global comment_id
#     async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
#         headers = {
#             'Authorization': 'Bearer {}'.format(access_token_1)
#         }

#         response = await ac.get(
#             "/api/post/" + fake_uuid4 + "/comment",
#             headers=headers
#         )

#     assert response.status_code == 404
#     response_data = response.json()
#     assert response_data['message'] == "POST__NOT_FOUND"

@pytest.mark.asyncio
async def test_get_notifications_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/notification/me",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert isinstance(response_data['total'], int)
    assert isinstance(response_data['items'], list)
    global notification_id
    notification_id = response_data['items'][0]['id']
    assert response_data['next_cursor'] == None

@pytest.mark.asyncio
async def test_delete_one_notification():
    global notification_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/notification/" + notification_id,
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Deleted notification {notification_id} successfully"

@pytest.mark.asyncio
async def test_delete_all_notifications():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/notification/me",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Deleted all notifications successfully"

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.get(
            "/api/notification/me",
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['total'] == 0

@pytest.mark.asyncio
async def test_delete_comment_forbidden_wrong():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.delete(
            "/api/post/comment/" + comment_id,
            headers=headers
        )

    assert response.status_code == 403
    response_data = response.json()
    assert response_data['message'] == "USER__NOT_OWNER"

@pytest.mark.asyncio
async def test_delete_comment_correct():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/post/comment/" + comment_id,
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Comment {comment_id} deleted successfully"

@pytest.mark.asyncio
async def test_delete_comment_not_found_wrong():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/post/comment/" + fake_uuid4,
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "COMMENT__NOT_FOUND"


@pytest.mark.asyncio
async def test_delete_post_correct():
    global post_1, post_2
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/post/" + post_1['id'],
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Post {post_1['id']} deleted successfully"

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.delete(
            "/api/post/" + post_2['id'],
            headers=headers
        )

    assert response.status_code == 200
    response_data = response.json()
    assert response_data['message'] == f"Post {post_2['id']} deleted successfully"

@pytest.mark.asyncio
async def test_delete_post_not_found_wrong():
    global comment_id
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/post/" + fake_uuid4,
            headers=headers
        )

    assert response.status_code == 404
    response_data = response.json()
    assert response_data['message'] == "POST__NOT_FOUND"

@pytest.mark.asyncio
async def test_unregister_users_correct():
    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_1)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
    assert response.status_code == 200

    async with AsyncClient(app=spire_app, base_url=f"http://{server_ip_address}:{str(port_num)}") as ac:
        headers = {
            'Authorization': 'Bearer {}'.format(access_token_2)
        }

        response = await ac.delete(
            "/api/auth/unregister", 
            headers=headers
        )
    assert response.status_code == 200