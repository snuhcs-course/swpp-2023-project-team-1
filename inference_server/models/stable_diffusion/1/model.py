import json
import numpy as np
import torch
import triton_python_backend_utils as pb_utils

from diffusers import AutoPipelineForText2Image, AutoPipelineForImage2Image, AutoPipelineForInpainting, EulerDiscreteScheduler

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
                
        self.pipe = AutoPipelineForText2Image.from_pretrained(
            "stabilityai/stable-diffusion-xl-base-1.0",
            torch_dtype=torch.float16,
            variant="fp16",
            use_safetensors=True,
            cache_dir="/opt/tritonserver/pretrained_model"
            ).to("cuda")
        self.pipe.scheduler = EulerDiscreteScheduler.from_config(self.pipe.scheduler.config,use_karras_sigmas=True)
        
        self.inpainting_pipe = AutoPipelineForInpainting.from_pipe(self.pipe).to("cuda")
        self.inpainting_pipe.scheduler = EulerDiscreteScheduler.from_config(self.inpainting_pipe.scheduler.config,use_karras_sigmas=True)
        
        self.refiner_pipe =  AutoPipelineForImage2Image.from_pretrained(
            "stabilityai/stable-diffusion-xl-refiner-1.0",
            text_encoder_2=self.pipe.text_encoder_2,
            vae=self.pipe.vae,
            torch_dtype=torch.float16,
            use_safetensors=True,
            variant="fp16",
            cache_dir="/opt/tritonserver/pretrained_model"
            ).to("cuda") 
        self.refiner_pipe.scheduler = EulerDiscreteScheduler.from_config(self.refiner_pipe.scheduler.config,use_karras_sigmas=True)

        self.inpainting_refiner_pipe = AutoPipelineForInpainting.from_pipe(
            self.refiner_pipe,
            text_encoder_2=self.inpainting_pipe.text_encoder_2,
            vae=self.inpainting_pipe.vae,
            torch_dtype=torch.float16,
            use_safetensors=True,
            variant="fp16",
            cache_dir="/opt/tritonserver/pretrained_model"
            ).to("cuda")
        self.inpainting_refiner_pipe.scheduler = EulerDiscreteScheduler.from_config(self.inpainting_refiner_pipe.scheduler.config,use_karras_sigmas=True)
        
    def execute(self, requests):
        responses = []
        for request in requests:
            input_image_tensor=pb_utils.get_input_tensor_by_name(request, "INPUT_IMAGE")
            mask_tensor=pb_utils.get_input_tensor_by_name(request, "MASK")
            strength_base_tensor=pb_utils.get_input_tensor_by_name(request, "STRENGTH_BASE")
            
            prompt = pb_utils.get_input_tensor_by_name(request, "PROMPT").as_numpy()[0].decode()
            negative_prompt = pb_utils.get_input_tensor_by_name(request, "NEGATIVE_PROMPT").as_numpy()[0].decode()      
            num_images_per_prompt =  pb_utils.get_input_tensor_by_name(request, "SAMPLES").as_numpy()[0]
            num_base_inference_steps =  pb_utils.get_input_tensor_by_name(request, "BASE_STEPS").as_numpy()[0]
            num_refiner_inference_steps =  pb_utils.get_input_tensor_by_name(request, "REFINER_STEPS").as_numpy()[0]
            guidance_scale_base =  pb_utils.get_input_tensor_by_name(request, "GUIDANCE_SCALE_BASE").as_numpy()[0]
            guidance_scale_refiner =  pb_utils.get_input_tensor_by_name(request, "GUIDANCE_SCALE_REFINER").as_numpy()[0]
            strength_refiner = pb_utils.get_input_tensor_by_name(request, "STRENGTH_REFINER").as_numpy()[0]
              
            pipe_args = {
                "prompt": prompt,
                "negative_prompt": negative_prompt,
                "guidance_scale": guidance_scale_base,
                "num_inference_steps": num_base_inference_steps,
                "num_images_per_prompt": num_images_per_prompt,
                "output_type": "latent",
            }
            
            refiner_pipe_args = {
                "prompt": prompt,
                "negative_prompt": negative_prompt,
                "guidance_scale": guidance_scale_refiner,
                "num_inference_steps": num_refiner_inference_steps.item(),
                "num_images_per_prompt": num_images_per_prompt.item(),
                "strength": strength_refiner,
            }
            
            image = None
            inference_response = None
            
            if (input_image_tensor is not None) and (mask_tensor is not None) and (strength_base_tensor is not None):
                input_image = Image.open(BytesIO(base64.b64decode(input_image_tensor.as_numpy()[0].decode()))).convert("RGB").resize((1024, 1024))
                mask = Image.open(BytesIO(base64.b64decode(mask_tensor.as_numpy()[0].decode()))).convert("RGB").resize((1024, 1024))
                strength_base =  strength_base_tensor.as_numpy()[0]
                
                image = self.inpainting_pipe(image=input_image, mask_image=mask, strength=strength_base, **pipe_args,).images                   
                image = self.inpainting_refiner_pipe(image=image, mask_image=mask, **refiner_pipe_args,).images
            
            elif (input_image_tensor is None) and (mask_tensor is None) and (strength_base_tensor is None):
                image = self.pipe(**pipe_args,).images         
                image = self.refiner_pipe(image=image, **refiner_pipe_args,).images

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
                    error=pb_utils.TritonError("Must all image, mask and base strength be given or none of them")
                )
                          
            responses.append(inference_response)

        return responses