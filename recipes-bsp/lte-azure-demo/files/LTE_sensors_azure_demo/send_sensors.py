#!/usr/bin/python3

import os
import json
import logging
from time import sleep

import serial
import math

from azure.iot.device import ProvisioningDeviceClient
from azure.iot.device import IoTHubDeviceClient, Message

NUM_AVG = 10

DEVICE_PATH = '/dev/ttyRPMSG0'
DEVICE_BAUD_RATE = 115200

ECHO_START_M4_CORE = "echo START > /dev/ttyRPMSG0"

ser = serial.Serial(DEVICE_PATH, DEVICE_BAUD_RATE)

log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

# ensure environment variables are set for your device and IoT Central application credentials
provisioning_host = os.getenv("PROVISIONING_HOST")
id_scope = os.getenv("PROVISIONING_IDSCOPE")
registration_id = os.getenv("PROVISIONING_REGISTRATION_ID")
symmetric_key = os.getenv("PROVISIONING_SYMMETRIC_KEY")


class RunningAvg():
    def __init__(self, length):
        self.len = length
        self.fifo = None

    def get_avg(self, value):
        if not self.fifo:
            self.fifo = [value]
            self.sum = value
            self.num = 1
            self.head = 1
        else:
            if self.num < self.len:
                self.num += 1
                self.fifo.append(value)
                self.sum += value
                self.head += 1
                if self.head >= self.len:
                    self.head = 0
            else:
                sub = self.fifo[self.head]
                self.fifo[self.head] = value
                self.head += 1
                if self.head >= self.len:
                    self.head = 0
                self.sum += value
                self.sum -= sub

        return self.sum / self.num


def sensor_data_read(data, device_client):
    """sensor_data_read() is called periodically when a new record arrives from the M4 core

    Args:
        data : dict
            sensor data received from M4 core
        device_client : IoTHubDeviceClient
            connected Azure IoT Hub device
    """

    global temp_avg
    global tof_avg
    global acc_avg
    global data_recvd

    # fetch sensor data
    gyro = data['gyr']
    acc = math.sqrt(gyro['x']**2 + gyro['y']**2 + gyro['z']**2)
    temp = data['temp']
    tof = data['tof']

    # calculate running average
    tofa = tof_avg.get_avg(tof)
    tempa = temp_avg.get_avg(temp)
    acca = acc_avg.get_avg(acc)

    # after every NUM_AVG receptions we send up the actual running average
    data_recvd += 1
    if data_recvd >= NUM_AVG:
        data_recvd = 0
        print(f'tof: {tofa:.2f}   temp: {tempa:.2f}   acc: {acca:.2f}')

        msg = Message(json.dumps({"temp": round(tempa, 2),
                                  "acc": round(acca, 2),
                                  "tof": round(tofa, 2)}))
        msg.content_encoding = "utf-8"
        msg.content_type = "application/json"
        device_client.send_message(msg)


def sensor_loop(ser, device_client):
    """sensor_loop() reads sensor data in an endless loop and calls sensor_data_read()

    Args:
        ser: serial.Srial
            the port to read data from
        device_client : IoTHubDeviceClient
            connected Azure IoT Hub device
    """

    # the M4 firmware will start sending data when it receives this message
    os.system(ECHO_START_M4_CORE)

    # endless loop to read and process sensor data
    while True:
        sensor_data = ser.readline().decode().replace('\x00', '')
        my_json = json.loads(sensor_data)
        sensor_data_read(my_json, device_client)
        sleep(0.1)


temp_avg = RunningAvg(NUM_AVG)
acc_avg = RunningAvg(NUM_AVG)
tof_avg = RunningAvg(NUM_AVG)
data_recvd = 0

if __name__ == '__main__':
    # provisions the device to IoT Central-- this uses the Device Provisioning Service behind the scenes
    provisioning_device_client = ProvisioningDeviceClient.create_from_symmetric_key(
        provisioning_host=provisioning_host,
        registration_id=registration_id,
        id_scope=id_scope,
        symmetric_key=symmetric_key,
    )

    registration_result = provisioning_device_client.register()

    print("The complete registration result is")
    print(registration_result.registration_state)

    if registration_result.status == "assigned":
        print("Your device has been provisioned. It will now begin sending telemetry.")
        device_client = IoTHubDeviceClient.create_from_symmetric_key(
            symmetric_key=symmetric_key,
            hostname=registration_result.registration_state.assigned_hub,
            device_id=registration_result.registration_state.device_id,
        )

        device_client.connect()

        # let's start the endless loop
        try:
            sensor_loop(ser, device_client)
        except (KeyboardInterrupt, SystemExit):
            print("exiting")
            exit(0)
