# --------------------------------------------------------
# X-Decoder -- Generalized Decoding for Pixel, Image, and Language
# Copyright (c) 2022 Microsoft
# Licensed under The MIT License [see LICENSE for details]
# Written by Xueyan Zou (xueyan@cs.wisc.edu)
# --------------------------------------------------------

import os
import sys
import logging
import os.path as osp
import itertools

pth = '/'.join(sys.path[0].split('/')[:-1])
sys.path.insert(0, pth)

from PIL import Image
import numpy as np
np.random.seed(1)

import torch
from torchvision import transforms

from utils.arguments import load_opt_command

from detectron2.data import MetadataCatalog
from detectron2.utils.colormap import random_color
from openseed.BaseModel import BaseModel
from openseed import build_model
from utils.visualizer import Visualizer

from detectron2.data.datasets.builtin_meta import COCO_CATEGORIES
sys.path.append("/opt/tritonserver/models/open_seed/1/Mask2Former")
from Mask2Former.mask2former.data.datasets.register_ade20k_panoptic import ADE20K_150_CATEGORIES

logger = logging.getLogger(__name__)

def get_openseg_labels(dataset, prompt_engineered=False):
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
        "datasets/openseg_labels",
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

def main(args=None):
    '''
    Main execution point for PyLearn.
    '''
    opt, cmdline_args = load_opt_command(args)
    if cmdline_args.user_dir:
        absolute_user_dir = os.path.abspath(cmdline_args.user_dir)
        opt['user_dir'] = absolute_user_dir

    # META DATA
    pretrained_pth = os.path.join(opt['WEIGHT'])
    output_root = './output'
    image_pth = 'images/street.jpg'

    model = BaseModel(opt, build_model(opt)).from_pretrained(pretrained_pth).eval().cuda()

    t = []
    t.append(transforms.Resize(512, interpolation=Image.BICUBIC))
    transform = transforms.Compose(t)

    COCO_THING_CLASSES = [
        label
        for idx, label in enumerate(get_openseg_labels("coco_panoptic", True))
        if COCO_CATEGORIES[idx]["isthing"] == 1
    ]
    COCO_THING_COLORS = [c["color"] for c in COCO_CATEGORIES if c["isthing"] == 1]
    COCO_STUFF_CLASSES = [
        label
        for idx, label in enumerate(get_openseg_labels("coco_panoptic", True))
        if COCO_CATEGORIES[idx]["isthing"] == 0
    ]
    COCO_STUFF_COLORS = [c["color"] for c in COCO_CATEGORIES if c["isthing"] == 0]

    ADE_THING_CLASSES = [
        label
        for idx, label in enumerate(get_openseg_labels("ade20k_150", True))
        if ADE20K_150_CATEGORIES[idx]["isthing"] == 1
    ]
    ADE_THING_COLORS = [c["color"] for c in ADE20K_150_CATEGORIES if c["isthing"] == 1]
    ADE_STUFF_CLASSES = [
        label
        for idx, label in enumerate(get_openseg_labels("ade20k_150", True))
        if ADE20K_150_CATEGORIES[idx]["isthing"] == 0
    ]
    ADE_STUFF_COLORS = [c["color"] for c in ADE20K_150_CATEGORIES if c["isthing"] == 0]

    LVIS_CLASSES = get_openseg_labels("lvis_1203", True)
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
    
    model.model.sem_seg_head.predictor.lang_encoder.get_text_embeddings(thing_classes + stuff_classes, is_eval=False)
    metadata = MetadataCatalog.get('demo')
    model.model.metadata = metadata
    model.model.sem_seg_head.num_classes = len(thing_classes + stuff_classes)

    with torch.no_grad():
        image_ori = Image.open(image_pth).convert("RGB")
        width = image_ori.size[0]
        height = image_ori.size[1]
        image = transform(image_ori)
        image = np.asarray(image)
        image_ori = np.asarray(image_ori)
        images = torch.from_numpy(image.copy()).permute(2,0,1).cuda()

        batch_inputs = [{'image': images, 'height': height, 'width': width}]
        outputs = model.forward(batch_inputs)
        visual = Visualizer(image_ori, metadata=metadata)

        pano_seg = outputs[-1]['panoptic_seg'][0]
        pano_seg_info = outputs[-1]['panoptic_seg'][1]

        for i in range(len(pano_seg_info)):
            if pano_seg_info[i]['category_id'] in metadata.thing_dataset_id_to_contiguous_id.keys():
                pano_seg_info[i]['category_id'] = metadata.thing_dataset_id_to_contiguous_id[pano_seg_info[i]['category_id']]
            else:
                pano_seg_info[i]['isthing'] = False
                pano_seg_info[i]['category_id'] = metadata.stuff_dataset_id_to_contiguous_id[pano_seg_info[i]['category_id']]

        demo = visual.draw_panoptic_seg(pano_seg.cpu(), pano_seg_info) # rgb Image

        if not os.path.exists(output_root):
            os.makedirs(output_root)
        demo.save(os.path.join(output_root, 'pano.png'))


if __name__ == "__main__":
    main()
    sys.exit(0)