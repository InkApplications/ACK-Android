#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/wait.h>
#include <stdlib.h>

#include <jni.h>

#include "multimon.h"

static const struct demod_param *dem[] = { &demod_afsk1200 };

#define NUMDEMOD (sizeof(dem)/sizeof(dem[0]))

static struct demod_state dem_st[NUMDEMOD];

static void process_buffer(float *buf, unsigned int len)
{
    dem[0]->demod(dem_st+0, buf, len);
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
    return JNI_VERSION_1_6;
}


void Java_com_inkapplications_aprs_data_Multimon_init(JNIEnv *env, jobject object) {
    static int sample_rate = -1;
    unsigned int i;
    unsigned int overlap = 0;

    for (i = 0; i < NUMDEMOD; i++) {
        memset(dem_st+i, 0, sizeof(dem_st[i]));
        dem_st[i].dem_par = dem[i];
        if (dem[i]->init)
            dem[i]->init(dem_st+i);
        if (sample_rate == -1)
            sample_rate = dem[i]->samplerate;
        else if (sample_rate != dem[i]->samplerate) {
            exit(3);
        }
        if (dem[i]->overlap > overlap)
            overlap = dem[i]->overlap;
    }
}


JNIEnv *env_global;
jobject *abp_global;

void Java_com_inkapplications_aprs_data_Multimon_process(JNIEnv *env, jobject object, jfloatArray fbuf, jint length) {
    env_global = env;
    abp_global = object;
    jfloat *jfbuf = (*env)->GetFloatArrayElements(env, fbuf, 0);
    process_buffer(jfbuf, length);
    (*env)->ReleaseFloatArrayElements(env, fbuf, jfbuf, 0);
}

void send_frame_to_java(unsigned char *bp, unsigned int len) {
    jbyteArray data = (*env_global)->NewByteArray(env_global, len);
    if (data == NULL) {
        __android_log_print(ANDROID_LOG_ERROR,"Multimon","OOM on allocating data buffer");
        return;
    }
    (*env_global)->SetByteArrayRegion(env_global, data, 0, len, (jbyte*)bp);

    jclass cls = (*env_global)->GetObjectClass(env_global, abp_global);
    jmethodID callback = (*env_global)->GetMethodID(env_global, cls, "callback", "([B)V");
    (*env_global)->CallVoidMethod(env_global, abp_global, callback, data);

}
