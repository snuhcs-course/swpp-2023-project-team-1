# swpp-2023-project-team-1
# Frontend
* * *
# Backend
## 1. Initial Setup
### Install Python env and dependencies

Setting up the FastAPI development environment.

[Install poetry](https://python-poetry.org/docs/#installation)

```bash
curl -sSL https://install.python-poetry.org | python3 -
```

Move from the project root path to the backend directory, create a virtual environment, and install the package.

```bash
cd backend
poetry install
```

Activate Virtual Environment
```bash
poetry shell
```

For other commands, refer to [poetry official documentation](https://python-poetry.org/docs/basic-usage/)

e.g) Add packages to `pyproject.toml` and installs them.

```bash
poetry add pytest --group local
```
e.g) Manage Environments.

```bash
poetry env use 3.11
```

### IDE setting
#### VSCode

Create a `.vscode/settings.json` file and paste the contents below.

```json
{
  "python.linting.mypyEnabled": true,
  "python.linting.pylintEnabled": false,
  "python.linting.enabled": true,
  "python.linting.flake8Enabled": false,
  "[python]": {
    "editor.defaultFormatter": "ms-python.black-formatter"
  }
}
```
* * *
## 2. Docker

The actual development environment and deployment environment are Dockerized.
Therefore, you must install Docker and run the `docker compose` command.
For Docker compose v1, use the `docker-compose` command.

### Local
In your local environment, Build and run using the docker-compose.local.yaml file. (only DB uses Docker)

```sh
docker compose -f docker-compose.local.yaml up -d --build 
uvicorn app.main:app --reload --log-level debug --host 0.0.0.0 --port 8000
```
### Production

```sh
### In ec2 environment. Build and run using the docker-compose.prod.yaml file.

docker compose -f docker-compose.prod.yaml up -d --build
```

### Build
- `docker compose up --build`: If there are code changes, rebuild and run the container.

### Run
- `docker compose up`: Runs a Docker container. If it is not built, build it once at first.
  
### Stop
- `docker compose down`: Terminates the Docker container.
- `docker compose down -v`: Terminates the Docker container and deletes the volume as well.

For detailed options, see [link](https://docs.docker.com/engine/reference/commandline/compose_down/).
* * *
## 3. Local Development

With the Docker db container running, run the command below to proceed with development. [Local Dockerfile](#local)

### Install Dependencies
```bash
poetry install
```

### Run

```bash
cd backend
poetry shell
```

```bash
uvicorn app.main:app --reload --host 0.0.0.0
```

### Test
```bash
pytest
```

### Lint
```bash
flake8 app
mypy app
```

### Format
```bash
black app
```
* * *
## 4. Migration (Alembic)
### Make Migration

```bash
alembic revision --autogenerate -m "type your commit message"
```
Afterwards, the migration history is saved in `./backend/migrations/versions`.

### Migrate
This command has no practical use. It is set to run automatically when the `docker compose up --build` command is executed.

```bash
alembic upgrade head
```

### Downgrade
```bash
alembic downgrade -1
```
* * *
=======