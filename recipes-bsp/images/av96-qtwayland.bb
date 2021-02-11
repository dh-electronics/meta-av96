require av96-weston.bb
  
SUMMARY = "OpenSTLinux Weston image qith Qt5"

inherit populate_sdk_qt5

REQUIRED_DISTRO_FEATURES = "wayland"

CORE_IMAGE_EXTRA_INSTALL += " \
    packagegroup-framework-sample-qtwayland     \
    packagegroup-framework-sample-qt-examples   \
"
