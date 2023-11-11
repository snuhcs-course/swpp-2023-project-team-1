from app.schemas.user import UserCreate, UserUpdate
from app.schemas.post import PostCreate, PostUpdate, ImageCreate

def test_schema_user_create():
    test_user_create = UserCreate(
        email="test",
        username="test",
        password="test",
    )

    test_user_create_dict = test_user_create.create_dict()
    assert test_user_create_dict['email'] == "test"
    assert test_user_create_dict['username'] == "test"
    assert test_user_create_dict['password'] == "test"

def test_schema_user_update():
    
    test_user_update = UserUpdate(
        username="test",
        bio="test",
        profile_image_url="test",
    )

    test_user_update_dict = test_user_update.update_dict()
    assert test_user_update_dict['username'] == "test"
    assert test_user_update_dict['bio'] == "test"
    assert test_user_update_dict['profile_image_url'] == "test"

def test_schema_post_create():
    test_post_create = PostCreate(
        content="test",
        image_url="test"
    )

    test_post_create_dict = test_post_create.create_dict("19742383-a552-4d66-9e2e-054d2b906d11")
    assert test_post_create_dict['content'] == "test"
    assert test_post_create_dict['image_url'] == "test"
    assert test_post_create_dict['user_id'] == "19742383-a552-4d66-9e2e-054d2b906d11"

def test_schema_post_update():
    test_post_update = PostUpdate(
        content="test",
        image_url="test"
    )

    test_post_update_dict = test_post_update.create_dict() # naming consistency check necessary
    assert test_post_update_dict['content'] == "test"
    assert test_post_update_dict['image_url'] == "test"

def test_schema_image_create():
    test_image_create = ImageCreate(
        origin_image = "test",
        mask_image = "test",
        modified_image = "test",
        prompt = "test"
    )

    test_image_create_dict = test_image_create.create_dict("19742383-a552-4d66-9e2e-054d2b906d11", "19742383-a552-4d66-9e2e-054d2b906d11")
    assert test_image_create_dict['origin_image'] == "test"
    assert test_image_create_dict['mask_image'] == "test"
    assert test_image_create_dict['modified_image'] == "test"
    assert test_image_create_dict['prompt'] == "test"
    assert test_image_create_dict['user_id'] == "19742383-a552-4d66-9e2e-054d2b906d11"
    assert test_image_create_dict['post_id'] == "19742383-a552-4d66-9e2e-054d2b906d11"