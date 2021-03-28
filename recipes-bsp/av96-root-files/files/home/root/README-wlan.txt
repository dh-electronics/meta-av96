    Setting up WLAN connection


    1st scan for networks via:
      # iw dev wlan0 scan | grep ssid -i

    Then set SSID and passphrase with:
      # wpa_passphrase <SSID> <passphrase> > /etc/wpa_supplicant/wpa_supplicant-wlan0.conf

    Start WLAN connection with:
      # systemctl start wpa_supplicant@wlan0

    Make this connection permenent:
      # systemctl enable wpa_supplicant@wlan0

