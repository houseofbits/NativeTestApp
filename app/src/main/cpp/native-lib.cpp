#include <jni.h>
#include <string>
#include "../../../inc/fmod.hpp"

std::string consoleStr = "";

void addConsoleLine(std::string str){
    consoleStr = consoleStr + "JNI: "+str + "\n";
}

FMOD::System     *fmod_system;
FMOD::Sound      *sound1, *sound2, *sound3;
FMOD::Channel    *channel = 0;

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_MainActivity_createFMODJNI(
        JNIEnv* env,
        jobject /* this */) {

    addConsoleLine("call createFMODJNI");

    FMOD_RESULT       result;
    unsigned int      version;

    result = FMOD::System_Create(&fmod_system);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:System_Create failed");
        return 0;
    }
    result = fmod_system->getVersion(&version);
    addConsoleLine(" FMOD version: "+std::to_string(version));



    return 0;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    addConsoleLine("call stringFromJNI");

    return env->NewStringUTF("TEST");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_doSomeWorkJNI(
        JNIEnv* env,
        jobject /* this */) {

    addConsoleLine("call doSomeWorkJNI");

    return env->NewStringUTF("TEST2");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_getDebugStringJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string str = consoleStr;
    consoleStr.empty();
    return env->NewStringUTF(str.c_str());
}
