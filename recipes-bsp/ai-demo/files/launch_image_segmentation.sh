#!/bin/sh
cd /usr/local/demo-ai/image-segmentation
python3 image_segmentation_demo.py -m lite-model_deeplabv3_1_metadata_2.tflite -b background_1.jpg -b background_2.jpg -b background_3.jpg -b background_4.jpg -b background_5.jpg
