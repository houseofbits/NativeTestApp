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
    int      numDrivers;

    result = FMOD::System_Create(&fmod_system);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:System_Create failed");
        return 0;
    }
    result = fmod_system->getVersion(&version);
    addConsoleLine(" FMOD version: "+std::to_string(version));

    result = fmod_system->getNumDrivers(&numDrivers);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:getNumDrivers failed");
        return 0;
    }
    addConsoleLine(" FMOD num drivers: "+std::to_string(numDrivers));

    for(int i=0; i<numDrivers; i++) {
        int szLength = 1024;
        char szName[szLength] = {0};
        FMOD_GUID sFmodGuid;
        result = fmod_system->getDriverInfo(i, szName, szLength, &sFmodGuid, 0, 0, 0);
        if (result == FMOD_OK){
            addConsoleLine(" FMOD driver: "+std::to_string(i)+"  name: "+std::string(szName));
        }
    }



    return 1;
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
