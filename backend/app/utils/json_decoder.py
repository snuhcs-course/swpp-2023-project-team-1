import time
from fastapi.encoders import jsonable_encoder
import ujson
from app.models.post import Post
from app.models.notification import Notification


def load_posts_json_fields(
    items: list[dict],
    json_keys: list[str] = ["image"],
):
    for item in items:
        for key in json_keys:
            if key in item:
                if item[key] is not None:
                    item[key] = ujson.loads(item[key])


def normalize_post(item: list[Post] | Post) -> dict:
    if type(item) == list:
        d = jsonable_encoder(item)
        load_posts_json_fields(d)
        return d
    else:
        d = jsonable_encoder(item)
        load_posts_json_fields([d])
        return d

def normalize_notification(item: list[Notification] | Notification) -> dict:
    if type(item) == list:
        d = jsonable_encoder(item)
        load_posts_json_fields(d)
        return d
    else:
        d = jsonable_encoder(item)
        load_posts_json_fields([d])
        return d