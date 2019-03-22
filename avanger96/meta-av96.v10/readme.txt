    Required steps after 'repo sync'

    $ cd layers/meta-st/meta-st-stm32mp/conf/eula
    $ ln -s ST_EULA_SLA stm32mp1-av96
    $ cd .../Distribution-Package/openstlinux-4.19-thud-mp1-19-02-20
    $ DISTRO=openstlinux-weston MACHINE=stm32mp1-av96 source layers/meta-st/scripts/envsetup.sh
    $ cp .../meta-av96.v10 ../layers/meta-av96 -R
    $ bitbake-layers add-layer ../layers/meta-av96/
    $ bitbake st-image-core
