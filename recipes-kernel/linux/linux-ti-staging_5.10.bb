SECTION = "kernel"
SUMMARY = "Linux kernel for TI devices"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel

DEFCONFIG_BUILDER = "${S}/ti_config_fragments/defconfig_builder.sh"
require recipes-kernel/linux/setup-defconfig.inc
require recipes-kernel/linux/cmem.inc
require recipes-kernel/linux/ti-uio.inc
require recipes-kernel/linux/bundle-devicetree.inc
require recipes-kernel/linux/kernel-rdepends.inc
require recipes-kernel/linux/ti-kernel.inc

DEPENDS += "gmp-native libmpc-native"

# Look in the generic major.minor directory for files
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-5.10:"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT} \
		      ${EXTRA_DTC_ARGS}"

S = "${WORKDIR}/git"

BRANCH = "ti-linux-5.10.y"

SRCREV = "2c23e6c538c879e380401ae4b236f54020618eaa"
PV = "5.10.168+git${SRCPV}"

# Append to the MACHINE_KERNEL_PR so that a new SRCREV will cause a rebuild
MACHINE_KERNEL_PR_append = "b"
PR = "${MACHINE_KERNEL_PR}"

KERNEL_GIT_URI = "git://git.ti.com/git/ti-linux-kernel/ti-linux-kernel.git"
KERNEL_GIT_PROTOCOL = "https"
SRC_URI += "${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
            file://0001-arm64-dts-am654-base-board-Reserve-memory-for-jailho.patch \
            file://0002-arm64-dts-k3-am654-base-board-jailhouse-Disable-mcu_.patch \
            file://0003-mm-Re-export-ioremap_page_range.patch \
            file://0004-mm-vmalloc-Export-__get_vm_area_caller.patch \
            file://0005-arm-arm64-export-__hyp_stub_vectors.patch \
            file://0006-uio-Enable-read-only-mappings.patch \
            file://0007-ivshmem-Add-header-file.patch \
            file://0008-uio-Add-driver-for-inter-VM-shared-memory-device.patch \
            file://0009-ivshmem-net-virtual-network-device-for-Jailhouse.patch \
            file://0010-arm64-dts-disable-peripherals-for-am654x-root-cell.patch \
            file://0011-arm64-dts-expanded-k3-am654-memory-region-for-jailho.patch \
            file://0012-arm64-dts-disable-pcie0_rc-node-in-k3-am654-jailhous.patch \
            file://0013-arm64-dts-am625-base-board-Reserve-memory-for-jailho.patch \
            file://0014-arm64-dts-add-reserved_memory-label-for-CMA-regions-.patch \
            file://defconfig"

FILES_${KERNEL_PACKAGE_NAME}-devicetree += "/${KERNEL_IMAGEDEST}/*.itb"

# Special configuration for remoteproc/rpmsg IPC modules
module_conf_rpmsg_client_sample = "blacklist rpmsg_client_sample"
module_conf_ti_k3_r5_remoteproc = "softdep ti_k3_r5_remoteproc pre: virtio_rpmsg_bus"
module_conf_ti_k3_dsp_remoteproc = "softdep ti_k3_dsp_remoteproc pre: virtio_rpmsg_bus"
KERNEL_MODULE_PROBECONF += "rpmsg_client_sample ti_k3_r5_remoteproc ti_k3_dsp_remoteproc"
KERNEL_MODULE_AUTOLOAD_append_j7 = " rpmsg_kdrv_switch"
