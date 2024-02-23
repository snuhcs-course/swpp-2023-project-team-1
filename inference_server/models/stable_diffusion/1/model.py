import json
import numpy as np
import torch
import triton_python_backend_utils as pb_utils

from diffusers import AutoPipelineForText2Image, AutoPipelineForInpainting, UNet2DConditionModel,  EulerDiscreteScheduler
from huggingface_hub import hf_hub_download
from safetensors.torch import load_file

from io import BytesIO
import base64
from PIL import Image

class TritonPythonModel:
    def initialize(self, args):
        self.output_dtype = pb_utils.triton_string_to_numpy(
            pb_utils.get_output_config_by_name(
                json.loads(args["model_config"]), "OUTPUT_IMAGES"
            )["data_type"]
        )
        
        self.base = "stabilityai/stable-diffusion-xl-base-1.0"
        self.repo = "ByteDance/SDXL-Lightning"
        self.ckpt = "sdxl_lightning_4step_unet.safetensors" # Use the correct ckpt for your step setting!
        # Load model.
        self.unet = UNet2DConditionModel.from_config(self.base, subfolder="unet", use_safetensors=True, cache_dir="pretrained_model").to("cuda", torch.float16)
        self.unet.load_state_dict(load_file(hf_hub_download(self.repo, self.ckpt, local_dir="pretrained_model", local_dir_use_symlinks=True), device="cuda"))
        self.pipe = AutoPipelineForText2Image.from_pretrained(self.base, unet=self.unet, torch_dtype=torch.float16, variant="fp16", use_safetensors=True, cache_dir="pretrained_model").to("cuda")

        # Ensure sampler uses "trailing" timesteps.
        self.pipe.scheduler = EulerDiscreteScheduler.from_config(self.pipe.scheduler.config, timestep_spacing="trailing")

        self.inpainting_pipe = AutoPipelineForInpainting.from_pipe(self.pipe).to("cuda")
        self.inpainting_pipe.scheduler = EulerDiscreteScheduler.from_config(self.inpainting_pipe.scheduler.config, timestep_spacing="trailing")
                
    def execute(self, requests):
        responses = []
        for request in requests:
            input_image_tensor=pb_utils.get_input_tensor_by_name(request, "INPUT_IMAGE")
            mask_tensor=pb_utils.get_input_tensor_by_name(request, "MASK")
            strength_base_tensor=pb_utils.get_input_tensor_by_name(request, "STRENGTH_BASE")
            
            prompt = pb_utils.get_input_tensor_by_name(request, "PROMPT").as_numpy()[0].decode()
            negative_prompt = pb_utils.get_input_tensor_by_name(request, "NEGATIVE_PROMPT").as_numpy()[0].decode()      
            num_images_per_prompt =  pb_utils.get_input_tensor_by_name(request, "SAMPLES").as_numpy()[0]
              
            pipe_args = {
                "prompt": prompt,
                "negative_prompt": negative_prompt,
                "guidance_scale": 0,
                "num_inference_steps": 4,
                "num_images_per_prompt": num_images_per_prompt,
            }
                        
            image = None
            inference_response = None
            
            if (input_image_tensor is not None) and (mask_tensor is not None) and (strength_base_tensor is not None):
                input_image = Image.open(BytesIO(base64.b64decode(input_image_tensor.as_numpy()[0].decode()))).convert("RGB").resize((1024, 1024))
                mask = Image.open(BytesIO(base64.b64decode(mask_tensor.as_numpy()[0].decode()))).convert("RGB").resize((1024, 1024))
                strength_base =  strength_base_tensor.as_numpy()[0]
                
                image = self.inpainting_pipe(image=input_image, mask_image=mask, strength=strength_base, **pipe_args,).images                   
                
            
            elif (input_image_tensor is None) and (mask_tensor is None) and (strength_base_tensor is None):
                image = self.pipe(**pipe_args,).images         

            if image is not None:
                img_str = np.zeros(shape = (len(image),), dtype = self.output_dtype)
            
                for i, img in enumerate(image):
                    buffer = BytesIO()
                    img.save(buffer, format="PNG")
                    buffer.seek(0)
                    img_str[i] = base64.b64encode(buffer.getvalue()).decode()
            
                inference_response = pb_utils.InferenceResponse(
                    output_tensors=[
                        pb_utils.Tensor(
                            "OUTPUT_IMAGES",
                            img_str.astype(self.output_dtype),
                        )
                    ]
                )
            else:
                inference_response = pb_utils.InferenceResponse(
                    error=pb_utils.TritonError("Must all image, mask and base strength be given or none of them", pb_utils.TritonError.INVALID_ARG)
                )
                          
            responses.append(inference_response)

        return responses