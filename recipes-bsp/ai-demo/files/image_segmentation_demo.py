#!/usr/bin/python3

import argparse
import cv2
import numpy as np
from PIL import Image
import signal
from threading import Thread, Event
from timeit import default_timer as timer
import tflite_runtime.interpreter as tflr

import gi
gi.require_version('Gtk', '3.0')

from gi.repository import Gtk
from gi.repository import GLib
from gi.repository import GdkPixbuf


SHAPE_WIDTH = 2
SHAPE_HEIGHT = 1

RGB_MAX = 255.0

INPUT_MEAN = 127.5
INPUT_STD_DEV = 127.5

SPACING_SMALL = 15
SPACING_WIDE1 = 75
SPACING_WIDE2 = 80
SPACING_LABEL = 100

INF_CLASSES_NUM = 21
INF_CLASS_PERSON = 15

COLOR_MAX = 1.0


def inference_thread(model_file, model_shape, inf_ready, cap_ready, cap_array, inf_array):
    """inference_thread() runs in the background and keeps on segmenting captured images.

    Args:
        model_file : string
            name of .tflite model file
        model_shape : list
            dimensions of model input
        inf_ready : Event
            signals that inferencing finished
        cap_ready : Event
            signals that captured image is ready, inferencing can start
        cap_array : np.array
            captured image
        inf_array : np.array
            result of inferencing
    """

    interpreter = tflr.Interpreter(model_path=model_file)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    floating_model = input_details[0]['dtype'] == np.float32

    width = input_details[0]['shape'][SHAPE_WIDTH]
    height = input_details[0]['shape'][SHAPE_HEIGHT]

    if (width != model_shape['x']) or (height != model_shape['y']):
        print(f'Incompatible model shape! w,h: {width},{height}')
        return

    while True:
        # wait for a single image capture to be ready
        cap_ready.wait()
        cap_ready.clear()

        input_img = np.expand_dims(cap_array, axis=0)
        if floating_model:
            input_data = (np.float32(input_img) - INPUT_MEAN) / INPUT_STD_DEV
        else:
            input_data = np.copy(input_img)
        interpreter.set_tensor(input_details[0]['index'], input_data)

        # do the inferencing
        interpreter.invoke()

        output_data = interpreter.get_tensor(output_details[0]['index'])
        results = np.squeeze(output_data)

        # results is a 257x257x21 array where the last dimension holds the 21
        # recognized classes. Sort each vector in descending order and keep
        # only the index of the most propable element
        inf_array[:, :] = np.argsort(-results)[:, :, 0]

        # notify the main process
        inf_ready.set()


class MainWindow(Gtk.Window):
    def __init__(self, args):
        Gtk.Window.__init__(self)

        # set the title bar
        self.headerbar = Gtk.HeaderBar()
        Gtk.HeaderBar.set_has_subtitle(self.headerbar, True)
        Gtk.HeaderBar.set_title(self.headerbar, "TensorFlow Lite")
        Gtk.HeaderBar.set_show_close_button(self.headerbar, False)
        self.set_titlebar(self.headerbar)

        # add close button
        self.close_button = Gtk.Button.new_with_label("Close")
        self.close_button.connect("clicked", self.close)
        self.headerbar.pack_end(self.close_button)

        self.maximize()

        self.set_position(Gtk.WindowPosition.CENTER)
        self.connect('destroy', Gtk.main_quit)

        self.vbox = Gtk.VBox()
        self.add(self.vbox)

        # temporary progressbar
        self.progressbar = Gtk.ProgressBar()
        self.vbox.pack_start(self.progressbar, False, False, SPACING_SMALL)

        self.hbox = Gtk.HBox()
        self.vbox.pack_start(self.hbox, False, False, SPACING_SMALL)

        # camera image
        dummy_label1 = Gtk.Label()
        dummy_label1.set_label(" ")
        self.hbox.pack_start(dummy_label1, False, False, SPACING_WIDE1)

        self.cap_image = Gtk.Image()
        self.hbox.pack_start(self.cap_image, False, False, SPACING_SMALL)

        # text in the middle
        self.label = Gtk.Label()
        self.label.set_size_request(SPACING_LABEL, -1)  # -1 to keep height automatic
        self.label.set_xalign(0)
        self.label.set_yalign(0)
        self.label.set_line_wrap(True)
        self.label.set_line_wrap_mode(Gtk.WrapMode.WORD)
        self.hbox.pack_start(self.label, False, False, SPACING_SMALL)

        dummy_label2 = Gtk.Label()
        dummy_label2.set_label(" ")
        self.hbox.pack_end(dummy_label2, False, False, SPACING_WIDE1)

        # AI image
        self.inf_image = Gtk.Image()
        self.hbox.pack_end(self.inf_image, False, False, SPACING_SMALL)

        # add placeholder for next_bg_img button
        self.hbox2 = Gtk.HBox()
        self.vbox.pack_start(self.hbox2, False, False, SPACING_SMALL)

        dummy_label3 = Gtk.Label()
        dummy_label3.set_label(" ")
        self.hbox2.pack_end(dummy_label3, False, False, SPACING_WIDE2)

        self.next_button_shown = False

        self.inf_ready = Event()
        self.cap_ready = Event()

        # RGB color weights of 21 image segment classes
        # all weights are zero but no. 15 which is for 'person'
        self.colors = np.zeros((INF_CLASSES_NUM, 3))
        self.colors[INF_CLASS_PERSON] = [COLOR_MAX, COLOR_MAX, COLOR_MAX]

        self.frame_width = int(args.frame_width)
        self.frame_height = int(args.frame_height)
        self.bg_img = []
        self.bg_num = 0
        self.bg_idx = 0
        for bg_img in args.background_image:
            self.bg_img.append(Image.open(bg_img[0]).resize((self.frame_width, self.frame_height)))
            self.bg_num += 1

        self.timeout_id = GLib.timeout_add(50, self.on_timeout)

    def close(self, button):
        """'Close' button

        """
        self.destroy()

    def get_video_frame(self):
        """Capture a single frame from the video stream.

        """
        frame = None
        correct_frame = False
        fail_counter = -1
        while not correct_frame:
            # capture the frame
            correct_frame, frame = self.camera.read()
            fail_counter += 1
            # raise exception if there's no output from the device
            if fail_counter > 10:
                raise Exception("Capture: exceeded number of max tries"
                                " to capture the frame.")
        return frame

    def display_img(self, img_src, img_obj):
        """Display a single uniform image.

        """
        data = img_src.tobytes()
        data = GLib.Bytes.new(data)
        pixbuf = GdkPixbuf.Pixbuf.new_from_bytes(data, GdkPixbuf.Colorspace.RGB, False, 8,
                                                 self.frame_width, self.frame_height, self.frame_width * 3)
        img_obj.set_from_pixbuf(pixbuf.copy())

    def next_bg_img(self, button):
        """'Change background' button

        """
        self.bg_idx += 1
        if self.bg_idx >= self.bg_num:
            self.bg_idx = 0
        self.display_img(self.bg_img[self.bg_idx], self.inf_image)

    def on_timeout(self):
        # pulse progressbar while waiting for the inferencing to start
        self.progressbar.pulse()
        if self.inf_ready.is_set():
            # hidde the progress bar
            GLib.source_remove(self.timeout_id)
            self.progressbar.hide()

            # replace this idle function with update_inf_img()
            GLib.idle_add(self.update_inf_img)
        return True

    def update_inf_img(self):
        """Show result of inferencing.

        """
        if self.inf_ready.is_set():
            self.inf_ready.clear()

            stop_time = timer()
            self.inference_fps = 1 / (stop_time - self.inference_start)

            # out_array1 will hold the 'person' mask from the original image
            out_array1 = cv2.resize(self.colors[self.inf_array], dsize=(self.frame_width, self.frame_height))

            # let's mask out the person from the background image and add
            # the person from the original image
            out_array2 = self.bg_img[self.bg_idx] * (COLOR_MAX - out_array1)
            out_array1 = self.cap_img_orig * out_array1 + out_array2

            # float operations sometimes create bigger values than 255
            out_max = out_array1.max()
            if out_max > RGB_MAX:
                out_array1 /= (RGB_MAX / out_max)

            # inferencing can be restarted with next captured image
            # (we don't use self.cap_img_orig anymore)
            self.new_capture = True

            # display the result
            self.display_img(Image.fromarray(np.uint8(out_array1)), self.inf_image)

        return True

    def update_label_preview(self, preview_fps, inference_fps):
        """Update labels next to camera preview image.

        """
        str_preview_fps = str("{0:.1f}".format(preview_fps))
        str_inference_fps = str("{0:.2f}".format(inference_fps))

        self.label.set_markup("<span font='10' color='#002052FF'><b>display: \n</b></span>"
                              "<span font='10' color='#002052FF'><b>   @%sfps\n\n</b></span>"
                              "<span font='10' color='#002052FF'><b>inference: \n</b></span>"
                              "<span font='10' color='#002052FF'><b>   @%sfps\n\n\n</b></span>"
                              % (str_preview_fps, str_inference_fps))

    def start_inference(self, img):
        """Start inferencing for a given image.

        It prepares the input for inference_thread (cap_array) and saves the current captured image for later use.
        """

        # we'll reuse self.cap_img_orig in update_inf_img() when displaying
        # the combined background-foreground image
        self.cap_img_orig = np.copy(img)
        img = img.resize((self.model_shape['x'], self.model_shape['y']))

        # self.cap_array is a shared array with inference_thread
        self.cap_array[:, :, :] = img
        self.inference_start = timer()

        # inference_thread is waiting for this event
        self.cap_ready.set()

    def update_frame(self):
        """Update camera preview image.

        """
        img = Image.fromarray(cv2.cvtColor(self.get_video_frame(),
                                           cv2.COLOR_BGR2RGB))

        # self.new_capture is a flag for signaling that inference_thread
        # is ready to process the next captured frame
        # we mustn't call start_inference() until update_inf_img()
        # has finished updating the final image
        if self.new_capture:
            self.new_capture = False
            self.start_inference(img)

        self.display_img(img, self.cap_image)

    def terminate(self):
        """Termination function.

        """
        print("Main: termination")
        if self.camera_not_started:
            return
        self.camera.release()

    def camera_preview(self):
        """GTK background function for refreshing the camera preview and the labels.

        """
        # compute preview FPS
        loop_stop = timer()
        loop_time = loop_stop - self.loop_start
        self.loop_start = loop_stop
        self.total_time = self.total_time + loop_time
        if self.loop_count == 15:
            preview_fps = self.loop_count / self.total_time
            self.loop_count = 0
            self.total_time = 0
            self.update_label_preview(preview_fps, self.inference_fps)
        self.loop_count += 1

        # update the preview frame
        self.update_frame()

        # "Change background" button will be shown the 1st time this function executes
        if not self.next_button_shown:
            self.next_button = Gtk.Button.new_with_label("Change background")
            self.next_button.connect("clicked", self.next_bg_img)
            self.hbox2.pack_end(self.next_button, False, False, SPACING_WIDE2)
            self.next_button.show()
            self.next_button_shown = True

        return True

    def main(self, args):
        self.camera_not_started = True

        # variables to compute preview framerate
        self.loop_count = 1
        self.loop_start = 0
        self.total_time = 0

        # variables to compute inference framerate
        self.inference_start = 0
        self.inference_fps = 0

        model_width = int(args.model_width)
        model_height = int(args.model_height)
        self.model_shape = {'x': model_width, 'y': model_height}

        # shared array for sending capture data to inference_thread
        self.cap_array = np.zeros((model_height, model_width, 3), 'uint8')

        # shared array for receiving the result of inferencing
        self.inf_array = np.zeros((model_height, model_width), 'uint8')

        self.camera = cv2.VideoCapture(args.video_device)
        self.camera.set(cv2.CAP_PROP_FRAME_WIDTH, self.frame_width)
        self.camera.set(cv2.CAP_PROP_FRAME_HEIGHT, self.frame_height)
        self.camera.set(cv2.CAP_PROP_FPS, int(args.framerate))
        assert self.camera.isOpened()

        self.camera_not_started = False

        Gtk.HeaderBar.set_subtitle(self.headerbar, "float model")

        # start the camera preview
        self.new_capture = True
        GLib.idle_add(self.camera_preview)

        # start inferencing in a separate thread
        self.inf_thread = Thread(target=inference_thread,
                                 args=(args.model_file, self.model_shape, self.inf_ready,
                                       self.cap_ready, self.cap_array, self.inf_array))
        self.inf_thread.daemon = True
        self.inf_thread.start()


def destroy_window(gtkobject):
    gtkobject.terminate()
    print("destroy")


if __name__ == '__main__':
    # add signal to catch CRTL+C
    signal.signal(signal.SIGINT, signal.SIG_DFL)

    # arguments for Tensorflow Lite NN intitalisation
    parser = argparse.ArgumentParser()
    parser.add_argument("-v", "--video_device", default=0, help="video device (default /dev/video0)")
    parser.add_argument("--frame_width", default=320, help="width of the camera frame (default is 320)")
    parser.add_argument("--frame_height", default=240, help="height of the camera frame (default is 240)")
    parser.add_argument("--framerate", default=15, help="framerate of the camera (default is 15fps)")
    parser.add_argument("-m", "--model_file", default="lite-model_deeplabv3_1_metadata_2.tflite",
                        help=".tflite model to be executed")
    parser.add_argument("--model_width", default=257, help="width of model input (default is 257)")
    parser.add_argument("--model_height", default=257, help="height of model input (default is 257)")
    parser.add_argument("-b", "--background_image", action="append", nargs="+", help="background image(s)")
    args = parser.parse_args()

    try:
        win = MainWindow(args)
        win.connect("delete-event", Gtk.main_quit)
        win.connect("destroy", destroy_window)
        win.show_all()
        win.main(args)
    except Exception as exc:
        print("Main Exception: ", exc)

    Gtk.main()
