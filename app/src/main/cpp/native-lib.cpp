#include <jni.h>
#include <string>
#include "../../../inc/fmod.hpp"

std::string consoleStr = "";
FMOD::System     *fmod_system = 0;
FMOD::Channel    *channel = 0;
FMOD::Sound      *sounds[] = {0,0,0,0};

void addConsoleLine(std::string str){
    consoleStr = consoleStr + "JNI: "+str + "\n";
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_MainActivity_createFMODJNI(
        JNIEnv* env,
        jobject) {

    addConsoleLine("=========createFMODJNI==========");

    FMOD_RESULT       result;
    unsigned int      version;
    int             numDrivers;

    result = FMOD::System_Create(&fmod_system);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:System_Create failed");
        return 0;
    }
    result = fmod_system->getVersion(&version);
    addConsoleLine("FMOD version: "+std::to_string(version));

    result = fmod_system->getNumDrivers(&numDrivers);
    if (result != FMOD_OK){
        addConsoleLine("FMOD:getNumDrivers failed");
        return 0;
    }
    addConsoleLine("FMOD num drivers: "+std::to_string(numDrivers));

    for(int i=0; i<numDrivers; i++) {
        char szName[1024] = {0};
        FMOD_GUID sFmodGuid;
        int numChannels = 0;
        result = fmod_system->getDriverInfo(i, szName, 1024, &sFmodGuid, 0, 0, &numChannels);
        if (result == FMOD_OK){
            addConsoleLine("FMOD driver: "+std::to_string(i)+"  name: "+std::string(szName)+" channels: "+std::to_string(numChannels));
        }
    }

    return 1;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_MainActivity_loadSoundJNI(
        JNIEnv* env,
        jobject,
        jstring filename,
        jint index) {

    if(!fmod_system)return 0;

    const char* path_cstr = env->GetStringUTFChars(filename, NULL);

    FMOD_RESULT result = fmod_system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sounds[index]);

    if (result == FMOD_OK){
        addConsoleLine("sound loaded: " + std::string(path_cstr));
        return 1;
    }else{
        addConsoleLine("sound error: ["+std::to_string(result)+"] " + std::string(path_cstr));
    }
    return 0;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_getDebugStringJNI(
        JNIEnv* env,
        jobject) {
    std::string str = consoleStr;
    consoleStr.empty();
    return env->NewStringUTF(str.c_str());
}

extern "C" JNIEXPORT jbyte JNICALL
Java_com_nativetest_nativetestapp_MainActivity_bytetestJNI(
        JNIEnv* env,
        jobject,
        jbyte dta) {

    unsigned char d = (unsigned char)dta;

    return d;
}

//CRC-8 - based on the CRC8 formulas by Dallas/Maxim
//code released under the therms of the GNU GPL 3.0 license
extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_MainActivity_CRC8JNI(
        JNIEnv* env,
        jobject,
        jintArray jData,
        jint length) {

    unsigned char data[length];
    jint *getints = env->GetIntArrayElements(jData, NULL);
    for (int i = 0; i < length; i++) {
        int val = getints[i];
        if(val<0)val = 0;
        if(val>255)val = 255;
        data[i] = (unsigned char)val;
    }
    unsigned char crc = 0x00;
    unsigned char len = (unsigned char)length;
    int cnt=0;
    while (len--) {
        unsigned char extract = data[cnt];  //(*data)++;
        cnt++;
        for (unsigned char tempI = 8; tempI; tempI--) {
            unsigned char sum = (crc ^ extract) & 0x01;
            crc >>= 1;
            if (sum) {
                crc ^= 0x8C;
            }
            extract >>= 1;
        }
    }
    return (int)crc;
}
