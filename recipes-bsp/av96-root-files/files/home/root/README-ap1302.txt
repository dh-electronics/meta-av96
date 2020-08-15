	Some words about the AP1302+AR1337 camera driver
	and instructions for testing it


	The ap1302 Linux driver is using the firmware file
on_ar1337_cdaf_dw9718.290520_01.bin which was created from
“guido_patch_290520_01” originally received from OnSemi. This firmware runs
on the AP1302 ISP and implements Contrast-Detect-Auto-Focus (CDAF) using the
DW9718S actuator found in the “Truly” AR1337 camera modules. CDAF works but
the firmware has problems with AE (auto-exposure) in very bright scenes.
Because of this AE is originally disabled by the ap1302 Linux driver. The
AP1302 ISP is still allowed to determine the gain value automatically.

	Rebuilding the image: follow instructions at
https://github.com/dh-electronics/manifest-av96/tree/thud-ap1302-dev


	Supported resolutions and maximum fps:
		320x240		25
		640x480		25
		1024x576	25
		1280x720	25
		1920x1080	15
		4160x3120	3


Some useful commands
	# v4l2-ctl -L	
			lists available camera controls

	# v4l2-ctl --list-formats-ext
			lists available output formats, resolutions and
			framerates

	# v4l2-ctl --set-ctrl auto_exposure=0
			enables AE - note the explanation for this “control”
			at the previous command

	# v4l2grab -d /dev/video0 -m -W 4160 -H 3120 -o out.jpg
			grabs a single 13MP image and stores it in out.jpg
			(takes some time at this resolution)

	# ./v4l2_start.sh &
			starts video stream on the Weston GUI. Controls can
			be changed using v4l2-ctl while the stream is on.


	Note on MIPI lane swap
	The ap1302 Linux driver uses the SRT-96B-MEZ-ISP REV1.1 board which
has swapped MIPI clock and data lines. Because of this st-mipid02 Linux
driver has a patch for swapping these lines. When testing with a different
board this patch must be removed and Linux rebuilt. The following line must be
removed from meta-av96/receipes-bsp/linux-stm32mp/linux-stm32mp_%.bbappend :
	file://0019-ap1302-swap-mipi-lanes.patch




