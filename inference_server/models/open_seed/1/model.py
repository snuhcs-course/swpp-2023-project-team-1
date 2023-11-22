import os
import json
import numpy as np
import torch
import torchvision
import triton_python_backend_utils as pb_utils

from io import BytesIO
import base64
from PIL import Image
from torchvision.io import image, ImageReadMode

import sys

import cv2

import argparse
import os.path as osp
import itertools

from torchvision import transforms

sys.path.append(os.path.join(os.getcwd(), "openseed"))
sys.path.append(os.path.join(os.getcwd(), "Mask2Former"))

from detectron2.data import MetadataCatalog
from detectron2.utils.colormap import random_color
from openseed.BaseModel import BaseModel
from openseed import build_model

from detectron2.data.datasets.builtin_meta import COCO_CATEGORIES
from Mask2Former.mask2former.data.datasets.register_ade20k_panoptic import ADE20K_150_CATEGORIES

from .utils.visualizer import Visualizer
from .utils.arguments import load_opt_command

class TritonPythonModel:
    def get_openseg_labels(self, dataset, prompt_engineered=False):
        """get the labels in double list format,
        e.g. [[background, bag, bed, ...], ["aeroplane"], ...]
        """

        invalid_name = "invalid_class_id"
        assert dataset in [
            "ade20k_150",
            "ade20k_847",
            "coco_panoptic",
            "pascal_context_59",
            "pascal_context_459",
            "pascal_voc_21",
            "lvis_1203",
        ]

        label_path = osp.join(
            osp.dirname(osp.abspath(__file__)),
            "demo/datasets/openseg_labels",
            f"{dataset}_with_prompt_eng.txt" if prompt_engineered else f"{dataset}.txt",
        )

        # read text in id:name format
        with open(label_path, "r") as f:
            lines = f.read().splitlines()

        categories = []
        for line in lines:
            id, name = line.split(":")
            if name == invalid_name:
                continue
            categories.append({"id": int(id), "name": name})

        return [dic["name"].split(",") for dic in categories]

    def initialize(self, args):
        self.output_overall_image_dtype = pb_utils.triton_string_to_numpy(
            pb_utils.get_output_config_by_name(
                json.loads(args["model_config"]), "OUTPUT_OVERALL_IMAGE"
            )["data_type"]
        )

        self.output_masks_dtype = pb_utils.triton_string_to_numpy(
            pb_utils.get_output_config_by_name(
                json.loads(args["model_config"]), "OUTPUT_MASKS"
            )["data_type"]
        )        
        
        self.output_labels_dtype = pb_utils.triton_string_to_numpy(
            pb_utils.get_output_config_by_name(
                json.loads(args["model_config"]), "OUTPUT_LABELS"
            )["data_type"]
        )
        
        args_temp = "evaluate --conf_files models/open_seed/1/configs/openseed/openseed_swint_lang.yaml --overrides WEIGHT pretrained_model/model_state_dict_swint_51.2ap.pt".split()
        
        opt, cmdline_args = load_opt_command(args_temp)
        if cmdline_args.user_dir:
            absolute_user_dir = os.path.abspath(cmdline_args.user_dir)
            opt['user_dir'] = absolute_user_dir

        # META DATA
        pretrained_pth = os.path.join(opt['WEIGHT'])
        
        self.model = BaseModel(opt, build_model(opt)).from_pretrained(pretrained_pth).eval().cuda()

        t = []
        t.append(transforms.Resize(512, interpolation=Image.BICUBIC))
        self.transform = transforms.Compose(t)

        COCO_THING_CLASSES = [
            label
            for idx, label in enumerate(self.get_openseg_labels("coco_panoptic", True))
            if COCO_CATEGORIES[idx]["isthing"] == 1
        ]
        COCO_THING_COLORS = [c["color"] for c in COCO_CATEGORIES if c["isthing"] == 1]
        COCO_STUFF_CLASSES = [
            label
            for idx, label in enumerate(self.get_openseg_labels("coco_panoptic", True))
            if COCO_CATEGORIES[idx]["isthing"] == 0
        ]
        COCO_STUFF_COLORS = [c["color"] for c in COCO_CATEGORIES if c["isthing"] == 0]

        ADE_THING_CLASSES = [
            label
            for idx, label in enumerate(self.get_openseg_labels("ade20k_150", True))
            if ADE20K_150_CATEGORIES[idx]["isthing"] == 1
        ]
        ADE_THING_COLORS = [c["color"] for c in ADE20K_150_CATEGORIES if c["isthing"] == 1]
        ADE_STUFF_CLASSES = [
            label
            for idx, label in enumerate(self.get_openseg_labels("ade20k_150", True))
            if ADE20K_150_CATEGORIES[idx]["isthing"] == 0
        ]
        ADE_STUFF_COLORS = [c["color"] for c in ADE20K_150_CATEGORIES if c["isthing"] == 0]

        LVIS_CLASSES = self.get_openseg_labels("lvis_1203", True)
        # use beautiful coco colors
        LVIS_COLORS = list(
            itertools.islice(itertools.cycle([c["color"] for c in COCO_CATEGORIES]), len(LVIS_CLASSES))
        )

        thing_classes = COCO_THING_CLASSES
        stuff_classes = COCO_STUFF_CLASSES
        thing_colors = COCO_THING_COLORS
        stuff_colors = COCO_STUFF_COLORS

        thing_classes += ADE_THING_CLASSES
        stuff_classes += ADE_STUFF_CLASSES
        thing_colors += ADE_THING_COLORS
        stuff_colors += ADE_STUFF_COLORS

        thing_classes += LVIS_CLASSES
        thing_colors += LVIS_COLORS

        thing_classes = [thing_class[0] for thing_class in thing_classes]
        stuff_classes = [stuff_class[0] for stuff_class in stuff_classes]
    
        thing_dataset_id_to_contiguous_id = {x:x for x in range(len(thing_classes))}
        stuff_dataset_id_to_contiguous_id = {x+len(thing_classes):x for x in range(len(stuff_classes))}

        MetadataCatalog.get("demo").set(
            thing_colors=thing_colors,
            thing_classes=thing_classes,
            thing_dataset_id_to_contiguous_id=thing_dataset_id_to_contiguous_id,
            stuff_colors=stuff_colors,
            stuff_classes=stuff_classes,
            stuff_dataset_id_to_contiguous_id=stuff_dataset_id_to_contiguous_id,
        )
    
        self.model.model.sem_seg_head.predictor.lang_encoder.get_text_embeddings(thing_classes + stuff_classes, is_eval=False)
        self.metadata = MetadataCatalog.get('demo')
        self.model.model.metadata = self.metadata
        self.model.model.sem_seg_head.num_classes = len(thing_classes + stuff_classes)
        
    def execute(self, requests):
        responses = []
        for request in requests:
            input_image_bytes = pb_utils.get_input_tensor_by_name(request, "INPUT_IMAGE").as_numpy()[0]
            input_image = Image.open(BytesIO(base64.b64decode(input_image_bytes))).convert("RGB")

            with torch.no_grad():
                width = input_image.size[0]
                height = input_image.size[1]
                image = self.transform(input_image)
                image = np.asarray(image)
                input_image = np.asarray(input_image)
                images = torch.from_numpy(image.copy()).permute(2,0,1).cuda()

                batch_inputs = [{'image': images, 'height': height, 'width': width}]
                outputs = self.model.forward(batch_inputs)
                visual = Visualizer(input_image, metadata=self.metadata)

                pano_seg = outputs[-1]['panoptic_seg'][0]
                pano_seg_info = outputs[-1]['panoptic_seg'][1]

                for i in range(len(pano_seg_info)):
                    if pano_seg_info[i]['category_id'] in self.metadata.thing_dataset_id_to_contiguous_id.keys():
                        pano_seg_info[i]['category_id'] = self.metadata.thing_dataset_id_to_contiguous_id[pano_seg_info[i]['category_id']]
                    else:
                        pano_seg_info[i]['isthing'] = False
                        pano_seg_info[i]['category_id'] = self.metadata.stuff_dataset_id_to_contiguous_id[pano_seg_info[i]['category_id']]

                demo, masks, labels = visual.draw_panoptic_seg(pano_seg.cpu(), pano_seg_info) # rgb Image
            
            overall_img = cv2.cvtColor(demo.get_image_rgba(), cv2.COLOR_RGBA2BGRA)
            _, buffer = cv2.imencode('.png', overall_img)
            overall_img_str = np.asarray([base64.b64encode(buffer)])

            masks = masks.astype("uint8")
            h, w = masks.shape[-2:]
            color = np.asarray([73, 66, 228, 204]).astype("uint8")
            mask_images = masks.reshape(-1, h, w, 1) * color.reshape(1, 1, 1, -1)
            
            mask_str=[]
            for mask in mask_images:
                mask_cv = cv2.cvtColor(mask, cv2.COLOR_RGBA2BGRA)
                _, buffer = cv2.imencode('.png', mask_cv)
                mask_str.append(base64.b64encode(buffer))
            mask_str = np.asarray(mask_str)
            
            inference_response = pb_utils.InferenceResponse(
                output_tensors=[
                    pb_utils.Tensor(
                        "OUTPUT_OVERALL_IMAGE",
                        overall_img_str.astype(self.output_overall_image_dtype),
                    ),
                    pb_utils.Tensor(
                        "OUTPUT_MASKS",
                        mask_str.astype(self.output_masks_dtype),
                    ),
                    pb_utils.Tensor(
                        "OUTPUT_LABELS",
                        np.asarray(labels).astype(self.output_labels_dtype),
                    )                    
                ]
            )
            responses.append(inference_response)

        return responses