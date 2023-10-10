#!/bin/bash
echo "Dockerizing..."
dockerize -wait tcp://db:5432 -timeout 20s

echo "Run Alembic Migrations"
echo "Create initial data in DB and Run FastAPI with uvicorn"
alembic --raiseerr upgrade head &&\

# python -m app.initial_data && \
uvicorn app.main:app --host 0.0.0.0 --port 8000