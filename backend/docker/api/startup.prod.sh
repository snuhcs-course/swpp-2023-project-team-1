#!/bin/bash

echo "Run Alembic Migrations"
echo "Create initial data in DB and Run FastAPI with gunicorn"
alembic --raiseerr upgrade head &&\

# python -m app.initial_data && \
uvicorn app.main:app --host 0.0.0.0 --port 8000