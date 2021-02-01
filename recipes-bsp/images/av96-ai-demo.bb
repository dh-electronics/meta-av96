require av96-weston.bb

SUMMARY = "Image segmentation demo"

CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-x-linux-ai \
    tflite-model-deeplabv3 \
    tflite-demo-image-segmentation-py \
"

