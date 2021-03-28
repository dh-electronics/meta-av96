    Azure IoT cloud based demo with the Shiratech LTE-Sensors board

    This simple demo shows how to collect data from various I2C sensors on the
M4 core of the STM32MP1 and send them into the cloud via the Qeuctel EC-25
LTE modem.

    Prerequisites:
	- during boot-up please select "M4-core demo on LTE-sensors board" in the
	  U-Boot "boot mode" menu. Otherwise the kernel will crash when the M4
	  firmware starts


    Starting up LTE connection
    Connection settings can be found in /etc/ppp/peers/quectel-chat-connect
The current demo was tested with a 1NCE SIM card. For different SIM cards this
file needs to be modified before building up the LTE connection.

    The following command will build up the LTE connection through the Quectel
EC-25 modem:
      # systemctl start ppp@quectel
    It first resets the modem and waits 13s for the USB enumeration to complete.
Please see "Quectel EC25 Hardware Design Guide" for modem startup timings.
    The status of the connection can be checked by:
      # ifconfig ppp0
    Has the connection been built up properly this would display something like:
      ppp0      Link encap:Point-to-Point Protocol
                inet addr:100.101.252.46  P-t-P:10.64.64.64  Mask:255.255.255.255
    It is advised to test the connection by executing:
      # ping google.com


    Starting the demo
    The demo consists of two parts. One is running on the M4 core and sending
sensor data periodically to the A7 core. The other is a simple python application
using the Azure IoT SDK for Python. The SDK requires several variables to be set
for connecting to Azure IoT HUB. These variables can be found in
LTE_sensors_azure_demo/set_env.sh and must be adjusted before starting the demo.
The following variables need to be set:
        PROVISIONING_HOST
        PROVISIONING_IDSCOPE
        PROVISIONING_REGISTRATION_ID
        PROVISIONING_SYMMETRIC_KEY

    The demo can be started by executing:
      # cd LTE_sensors_azure_demo
      # ../fw_cortex_m4.sh start
            this starts the M4 firmware which will send sensor data
            periodically to Linux running on the A7 core
      # source ./set_env.sh
      # python3 send_sensors.py

    After successful cloud connection the demo will display something like this:
        The complete registration result is
        st4sim1
        iotc-a5be5c55-f900-402c-a809-307d005b85cf.azure-devices.net
        initialAssignment
        null
        Your device has been provisioned. It will now begin sending telemetry.
        tof: 1066.00   temp: 40.73   acc: 2.99
        ...

    One can stop the demo by pressing Ctrl-C. After stop it is recommended to
halt the M4 core by:
      # ../fw_cortex_m4.sh stop


    Source code for the demo
    All the sources of the simple Python application can be found in this image
at /home/root/LTE_sensors_azure_demo
    C and C++ sources of the M4 firmware can be found at:
    https://github.com/dh-electronics/meta-av96/tree/dunfell/recipes-bsp/m4projects/files/Projects/


    Rebuilding the image
    See: https://github.com/dh-electronics/manifest-av96/

