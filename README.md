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

`[feat] {new feature}`
`[chore] {minor changes}`

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
git checkout main
git pull

### Move to {your branch} and merge main
git checkout {your branch}
git merge main

### Resolve Conflicts on your code editor.

### Push your code
git push
```

### 2.3 Pull Request

`Merge Pull request`

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

# Inference server
## 1. Initial Setup
### Accessing GPU server
Enabling Kubernetes with Docker Desktop makes life much easy. Refer to https://isn-t.tistory.com/45

Please refer to https://gpu.snucse.org/kubectl.html `서버 접근 방법` and follow the instructions.

### Quickstart (Run GPU server)
Download `spire-deployment.yaml` and `spire-service.yaml` in discord. And run following command will create a deployment.

```bash
kubectl apply -f spire-deployment.yaml
```

A deployment create and manages pods, and a pod execute a container in Kubernetes.

```bash
kubectl apply -f spire-service.yaml
```
This will create a service, which expose a deployment and one can access it via following. Please refer to secret. And also note that it takes time to server to be ready.

```bash
http://<public-node-ip>:<node-port>
```

You can run inference if at least one of pod created by a deployment is ready. Run follow command to check this.

```bash
kubectl get pods
kubectl get deployments
```

<img width="562" alt="스크린샷 2023-10-27 오후 8 43 39" src="https://github.com/snuhcs-course/swpp-2023-project-team-1/assets/125340163/a667b322-2873-4e8a-9829-a42d8adef02e">

<img width="565" alt="스크린샷 2023-10-27 오후 8 44 02" src="https://github.com/snuhcs-course/swpp-2023-project-team-1/assets/125340163/37a51ae6-148b-4167-88d3-d3b5f0a2f34b">

You can see what happening inside a pod via following command.

```bash
kubectl logs -f <name of a pod>
```

If everything is okay and ready, it should be something like this.

<img width="1795" alt="스크린샷 2023-10-26 오후 12 00 33" src="https://github.com/snuhcs-course/swpp-2023-project-team-1/assets/125340163/722dab90-4c49-48ae-aaa9-1597c613a43a">


Don't forget to delete all services, deployments and pods once you are done.

```bash
kubectl delete services --all
kubectl delete deployments --all
kubectl delete pods --all
```
## 2. Build Triton server image and deploy on k8s on-premise environment
### Build Triton server image
Download `Dockerfile` in `/inference_server`. Following command will build a docker image for our Triton inference server.

```bash
docker build -t spire_ai - < Dockerfile
```

You may need to run only one model in one container if you don't have sufficient memory.

```bash
docker run -it --name spire_stable_diffusion 0jihunlee/tritonserver:latest /bin/sh
cd models
rm -rf open_seed
exit
docker commit spire_stable_diffusion spire_ai_stable_diffusion
```

```bash
docker run -it --name spire_open_seed 0jihunlee/tritonserver:latest /bin/sh
cd models
rm -rf stable_diffusion
exit
docker commit spire_open_seed spire_ai_open_seed
```

### Deploy on k8s on-premise environment
Triton server can be deployed on various environment, but below is how we deployed this on our on-premise server. Please refer to https://github.com/triton-inference-server/server/tree/main for greater details. Our method is essentially adhoc since our permission on SNU GPU server is limited and we don't have any prior experience with k8s.


Download `.yaml` files in `/inference_server/k8s`.


First, following command will download all pretrained weights of our Triton inference server and initialize the server.

```bash
kubectl apply -f pod.yaml
```

Again, if everything is okay and ready, it should be something like this.

<img width="1679" alt="스크린샷 2023-11-24 오전 5 26 43" src="https://github.com/snuhcs-course/swpp-2023-project-team-1/assets/125340163/0ba32dc8-556e-4aab-a177-d07e3191adbe">

Delete the pod before moving on.

```bash
kubectl delete pods --all
```


Second, these two command will create a deployment and a service. Whenever these are ready, server can get requests and send responses. You must fill in the NodePort numbers in `service.yaml` in order to create a service.

```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

You can check the status of deployments and services via following commands.

```bash
kubectl get deployments
kubectl get services
```

The address for inference server will be as below. `<public-node-ip>` is node IP of SNU GPU server and `<node-port>` will be the number you selected in `service.yaml` for NodePort that corresponses to `http`.

```bash
http://<public-node-ip>:<node-port>
```

Don't forget to delete all services, deployments and pods once you are done.

```bash
kubectl delete services --all
kubectl delete deployments --all
```
