# swpp-2023-project-team-1
# Development Rule
## 1. Git
### 1.1 Branch Naming
#### main
The branch that can be released as product.

`main`

#### develop
The branch being developed for next release.

`develop`

#### feature
The branch where features are developed.

`feat/{feature-name}`

#### release
The branch preparing for this release

`release-{version}`

#### hotfix
The branch that fixes bugs encountered in the released version

`hotfix-{version}`

### 1.2 Commit Rule
|type|description|
|---|---|
|feat|New features|
|fix|Bug fix|
|build|Modifying build-related files, installing or deleting modules|
|chore|Other minor changes|
|docs|Document modifications|
|style|Code style and Format|
|refactor|Code refactoring|
|test|Modifying for test code|
|perf|Performance improvements|

`feat: {new feature}`
`chore: {minor changes}`

* * *

## 2. Merge and Resolve Conflicts
### 2.1 Rebase and Merge
Rebase does not leave any commit history, so the commit history becomes cleaner.

<img src="https://i.ibb.co/mtydnwh/rebase.jpg" width="500px"/>

### 2.2 Resolve Conflicts in Local

To resolve conflicts that occur when trying to merge your branch into the main branch

```bash
### Now you are in {your branch}

### Move to main branch and pull
git chekout main
git pull

### Move to {your branch} and rebase main
git checkout {your branch}
git rebase main

### Resolve Conflicts on your code editor.

### Push your code
git push origin -f "your branch"
```

### 2.3 Pull Request

`Rebase and Merge`

* * *
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
uvicorn app.main:spire_app --reload --log-level debug --host 0.0.0.0 --port 8000
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
uvicorn app.main:spire_app --reload --host 0.0.0.0
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
