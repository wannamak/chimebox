/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class chimebox_physical_SystemManagementBus */

#ifndef _Included_chimebox_physical_SystemManagementBus
#define _Included_chimebox_physical_SystemManagementBus
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     chimebox_physical_SystemManagementBus
 * Method:    readByte
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_chimebox_physical_SystemManagementBus_readByte
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     chimebox_physical_SystemManagementBus
 * Method:    writeByte
 * Signature: (III)V
 */
JNIEXPORT jint JNICALL Java_chimebox_physical_SystemManagementBus_writeByte
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     chimebox_physical_SystemManagementBus
 * Method:    initializeFileDescriptor
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_chimebox_physical_SystemManagementBus_initializeFileDescriptor
  (JNIEnv *, jobject, jstring, jint);

#ifdef __cplusplus
}
#endif
#endif
