import json

import numpy as np
import torch
import triton_python_backend_utils as pb_utils

from huggingface_hub import login
from diffusers import StableDiffusionXLInpaintPipeline, DPMSolverMultistepScheduler

from io import BytesIO
import base64
from PIL import Image


class TritonPythonModel:
    def initialize(self, args):
        self.output_dtype = pb_utils.triton_string_to_numpy(
            pb_utils.get_output_config_by_name(
                json.loads(args["model_config"]), "OUTPUT_IMAGE"
            )["data_type"]
        )
        
        
        login("PLEASE ENTER YOUR HUGGINGFACE TOKEN HERE")
        
        self.pipe = StableDiffusionXLInpaintPipeline.from_pretrained("diffusers/stable-diffusion-xl-1.0-inpainting-0.1", torch_dtype=torch.float16, variant="fp16", cache_dir="/opt/tritonserver/pretrained_model").to("cuda")
        self.pipe.scheduler = DPMSolverMultistepScheduler.from_config(self.pipe.scheduler.config)
                
    def execute(self, requests):
        responses = []
        for request in requests:
            _input_image= pb_utils.get_input_tensor_by_name(request, "INPUT_IMAGE")
            input_image = Image.open(BytesIO(base64.b64decode(_input_image.as_numpy()[0].decode()))).convert("RGB")
            
            _mask= pb_utils.get_input_tensor_by_name(request, "MASK")
            mask = Image.open(BytesIO(base64.b64decode(_mask.as_numpy()[0].decode()))).convert("RGB")
            
            _prompt = pb_utils.get_input_tensor_by_name(request, "PROMPT")
            prompt = _prompt.as_numpy()[0].decode()

            _negative_prompt = pb_utils.get_input_tensor_by_name(request, "NEGATIVE_PROMPT")
            negative_prompt = _negative_prompt.as_numpy()[0].decode()
                        
            _num_images_per_prompt =  pb_utils.get_input_tensor_by_name(request, "SAMPLES")
            num_images_per_prompt = _num_images_per_prompt.as_numpy()[0]
            
            _num_inference_steps =  pb_utils.get_input_tensor_by_name(request, "STEPS")
            num_inference_steps = _num_inference_steps.as_numpy()[0]
            
            _guidance_scale =  pb_utils.get_input_tensor_by_name(request, "GUIDANCE_SCALE")
            guidance_scale = _guidance_scale.as_numpy()[0]    
            
            _strength =  pb_utils.get_input_tensor_by_name(request, "STRENGTH")
            strength = _strength.as_numpy()[0] 

 
            input_image=input_image.resize((1024, 1024))
            mask=mask.resize((1024, 1024))
            
            image = self.pipe(
                prompt=prompt,
                negative_prompt=negative_prompt,
                image=input_image,
                mask_image=mask,
                guidance_scale=guidance_scale,
                num_inference_steps=num_inference_steps,
                strength=strength,
                ).images
                        
            buffer = BytesIO()
            image[0].save(buffer, format="PNG")
            buffer.seek(0)
            img_str = np.asarray([base64.b64encode(buffer.getvalue()).decode()])
            
            inference_response = pb_utils.InferenceResponse(
                output_tensors=[
                    pb_utils.Tensor(
                        "OUTPUT_IMAGE",
                        img_str.astype(self.output_dtype),
                    )
                ]
            )
            responses.append(inference_response)
              
        return responses