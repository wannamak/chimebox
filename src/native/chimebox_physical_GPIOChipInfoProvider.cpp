#include "chimebox_physical_GPIOChipInfoProvider.h"

#include <cstring>
#include <string>

#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/gpio.h>

struct GPIOChipInfo {
  std::string name;
  std::string label;
  int numLines;
};

JNIEXPORT jobject JNICALL Java_chimebox_physical_GPIOChipInfoProvider_getGPIOChipInfoInternal(
    JNIEnv *env, jobject obj, jstring device_path) {
  const char* c_device_path = env->GetStringUTFChars(device_path, 0);

  int fd;
  if ((fd = open(c_device_path, O_RDWR)) < 0) {
    printf("open %s failed: %s\n", c_device_path, strerror(errno));
    env->ReleaseStringUTFChars(device_path, c_device_path);
    return nullptr;
  }

  struct gpiochip_info info;
  int ret;
  if ((ret = ioctl(fd, GPIO_GET_CHIPINFO_IOCTL, &info)) < 0) {
    printf("GPIO_GET_CHIPINFO_IOCTL failed: %s\n", strerror(errno));
  }
  close(fd);
  env->ReleaseStringUTFChars(device_path, c_device_path);
  if (ret < 0) {
    return nullptr;
  }

  jclass gpioChipInfoClass = env->FindClass(
      "chimebox/physical/GPIOChipInfoProvider$GPIOChipInfo");
  if (gpioChipInfoClass == nullptr) {
    printf("Unable to find GPIOChipInfo class\n");
    return nullptr;
  }

  jmethodID constructorId = env->GetMethodID(
      gpioChipInfoClass,
      "<init>",
      "(Ljava/lang/String;Ljava/lang/String;I)V");
  if (constructorId == nullptr) {
    printf("Unable to find GPIOChipInfo constructor\n");
    return nullptr;
  }

  jstring nameString = env->NewStringUTF(info.name);
  jstring labelString = env->NewStringUTF(info.label);

  jobject gpioChipInfoObj = env->NewObject(gpioChipInfoClass, constructorId,
      nameString, labelString, info.lines);
  if (gpioChipInfoObj == nullptr) {
    printf("Unable to construct GPIOChipInfo\n");
    return nullptr;
  }

  return gpioChipInfoObj;
}
