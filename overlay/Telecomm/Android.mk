LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := SDM660CommonTelecommRes
LOCAL_VENDOR_MODULE := true
LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

include $(BUILD_RRO_PACKAGE)
