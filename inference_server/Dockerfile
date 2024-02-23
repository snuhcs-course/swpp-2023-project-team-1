FROM nvcr.io/nvidia/tritonserver:23.10-py3 

RUN apt-get update && apt-get install git && apt-get install ffmpeg libsm6 libxext6  -y &&\ 
    mkdir clone_model && cd clone_model &&\
    git init && git remote add -f origin https://github.com/snuhcs-course/swpp-2023-project-team-1.git &&\
    git config core.sparseCheckout true && echo inference_server/models/ >> .git/info/sparse-checkout &&\
    git pull origin main &&\
    cd .. && mv clone_model/inference_server/models models && rm -rf clone_model

WORKDIR /opt/tritonserver/models/stable_diffusion/1
RUN pip install -r requirements.txt

WORKDIR /opt/tritonserver/models/open_seed/1
RUN python3 -m pip install 'git+https://github.com/MaureenZOU/detectron2-xyz.git' &&\
    pip install git+https://github.com/cocodataset/panopticapi.git &&\
    python3 -m pip install -r requirements.txt

WORKDIR /opt/tritonserver/models/open_seed/1/Mask2Former
RUN pip install -r requirements.txt

WORKDIR /opt/tritonserver
