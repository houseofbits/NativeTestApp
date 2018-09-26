#include <jni.h>
#include <string>

std::string consoleStr = "";

void addConsoleLine(std::string str){
    consoleStr = consoleStr + str + "\n";
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    addConsoleLine("call stringFromJNI");

    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_doSomeWorkJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    addConsoleLine("call doSomeWorkJNI");

    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_getDebugStringJNI(
        JNIEnv* env,
        jobject /* this */) {
    return env->NewStringUTF(consoleStr.c_str());
}
