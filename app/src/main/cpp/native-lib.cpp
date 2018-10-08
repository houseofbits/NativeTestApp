#include <jni.h>
#include <string>
#include "../../../inc/fmod.hpp"

#include <math.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/ioctl.h>
#include <linux/soundcard.h>
#include <errno.h>

std::string consoleStr = "";
FMOD::System     *fmod_system = 0;
FMOD::Channel    *channels[] = {0,0,0,0,0,0,0,0};
FMOD::Sound      *sounds[] = {0,0,0,0,0,0,0,0};

void addConsoleLine(std::string str){
    consoleStr = consoleStr + "JNI: "+str + "\n";
}

static std::string FMOD_RESULT_STRING(FMOD_RESULT result){
    switch(result){
        case FMOD_OK: return "No errors.";
        case FMOD_ERR_BADCOMMAND: return "Tried to call a function on a data type that does not allow this type of functionality (ie calling Sound::lock on a streaming sound).";
        case FMOD_ERR_CHANNEL_ALLOC: return "Error trying to allocate a channel.";
        case FMOD_ERR_CHANNEL_STOLEN: return "The specified channel has been reused to play another sound.";
        case FMOD_ERR_DMA: return "DMA Failure.  See debug output for more information.";
        case FMOD_ERR_DSP_CONNECTION: return "DSP connection error.  Connection possibly caused a cyclic dependency or connected dsps with incompatible buffer counts.";
        case FMOD_ERR_DSP_DONTPROCESS: return "DSP return code from a DSP process query callback.  Tells mixer not to call the process callback and therefore not consume CPU.  Use this to optimize the DSP graph.";
        case FMOD_ERR_DSP_FORMAT: return "DSP Format error.  A DSP unit may have attempted to connect to this network with the wrong format, or a matrix may have been set with the wrong size if the target unit has a specified channel map.";
        case FMOD_ERR_DSP_INUSE: return "DSP is already in the mixer's DSP network. It must be removed before being reinserted or released.";
        case FMOD_ERR_DSP_NOTFOUND: return "DSP connection error.  Couldn't find the DSP unit specified.";
        case FMOD_ERR_DSP_RESERVED: return "DSP operation error.  Cannot perform operation on this DSP as it is reserved by the system.";
        case FMOD_ERR_DSP_SILENCE: return "DSP return code from a DSP process query callback.  Tells mixer silence would be produced from read, so go idle and not consume CPU.  Use this to optimize the DSP graph.";
        case FMOD_ERR_DSP_TYPE: return "DSP operation cannot be performed on a DSP of this type.";
        case FMOD_ERR_FILE_BAD: return "Error loading file.";
        case FMOD_ERR_FILE_COULDNOTSEEK: return "Couldn't perform seek operation.  This is a limitation of the medium (ie netstreams) or the file format.";
        case FMOD_ERR_FILE_DISKEJECTED: return "Media was ejected while reading.";
        case FMOD_ERR_FILE_EOF: return "End of file unexpectedly reached while trying to read essential data (truncated?).";
        case FMOD_ERR_FILE_ENDOFDATA: return "End of current chunk reached while trying to read data.";
        case FMOD_ERR_FILE_NOTFOUND: return "File not found.";
        case FMOD_ERR_FORMAT: return "Unsupported file or audio format.";
        case FMOD_ERR_HEADER_MISMATCH: return "There is a version mismatch between the FMOD header and either the FMOD Studio library or the FMOD Low Level library.";
        case FMOD_ERR_HTTP: return "A HTTP error occurred. This is a catch-all for HTTP errors not listed elsewhere.";
        case FMOD_ERR_HTTP_ACCESS: return "The specified resource requires authentication or is forbidden.";
        case FMOD_ERR_HTTP_PROXY_AUTH: return "Proxy authentication is required to access the specified resource.";
        case FMOD_ERR_HTTP_SERVER_ERROR: return "A HTTP server error occurred.";
        case FMOD_ERR_HTTP_TIMEOUT: return "The HTTP request timed out.";
        case FMOD_ERR_INITIALIZATION: return "FMOD was not initialized correctly to support this function.";
        case FMOD_ERR_INITIALIZED: return "Cannot call this command after System::init.";
        case FMOD_ERR_INTERNAL: return "An error occurred that wasn't supposed to.  Contact support.";
        case FMOD_ERR_INVALID_FLOAT: return "Value passed in was a NaN, Inf or denormalized float.";
        case FMOD_ERR_INVALID_HANDLE: return "An invalid object handle was used.";
        case FMOD_ERR_INVALID_PARAM: return "An invalid parameter was passed to this function.";
        case FMOD_ERR_INVALID_POSITION: return "An invalid seek position was passed to this function.";
        case FMOD_ERR_INVALID_SPEAKER: return "An invalid speaker was passed to this function based on the current speaker mode.";
        case FMOD_ERR_INVALID_SYNCPOINT: return "The syncpoint did not come from this sound handle.";
        case FMOD_ERR_INVALID_THREAD: return "Tried to call a function on a thread that is not supported.";
        case FMOD_ERR_INVALID_VECTOR: return "The vectors passed in are not unit length, or perpendicular.";
        case FMOD_ERR_MAXAUDIBLE: return "Reached maximum audible playback count for this sound's soundgroup.";
        case FMOD_ERR_MEMORY: return "Not enough memory or resources.";
        case FMOD_ERR_MEMORY_CANTPOINT: return "Can't use FMOD_OPENMEMORY_POINT on non PCM source data, or non mp3/xma/adpcm data if FMOD_CREATECOMPRESSEDSAMPLE was used.";
        case FMOD_ERR_NEEDS3D: return "Tried to call a command on a 2d sound when the command was meant for 3d sound.";
        case FMOD_ERR_NEEDSHARDWARE: return "Tried to use a feature that requires hardware support.";
        case FMOD_ERR_NET_CONNECT: return "Couldn't connect to the specified host.";
        case FMOD_ERR_NET_SOCKET_ERROR: return "A socket error occurred.  This is a catch-all for socket-related errors not listed elsewhere.";
        case FMOD_ERR_NET_URL: return "The specified URL couldn't be resolved.";
        case FMOD_ERR_NET_WOULD_BLOCK: return "Operation on a non-blocking socket could not complete immediately.";
        case FMOD_ERR_NOTREADY: return "Operation could not be performed because specified sound/DSP connection is not ready.";
        case FMOD_ERR_OUTPUT_ALLOCATED: return "Error initializing output device, but more specifically, the output device is already in use and cannot be reused.";
        case FMOD_ERR_OUTPUT_CREATEBUFFER: return "Error creating hardware sound buffer.";
        case FMOD_ERR_OUTPUT_DRIVERCALL: return "A call to a standard soundcard driver failed, which could possibly mean a bug in the driver or resources were missing or exhausted.";
        case FMOD_ERR_OUTPUT_FORMAT: return "Soundcard does not support the specified format.";
        case FMOD_ERR_OUTPUT_INIT: return "Error initializing output device.";
        case FMOD_ERR_OUTPUT_NODRIVERS: return "The output device has no drivers installed.  If pre-init, FMOD_OUTPUT_NOSOUND is selected as the output mode.  If post-init, the function just fails.";
        case FMOD_ERR_PLUGIN: return "An unspecified error has been returned from a plugin.";
        case FMOD_ERR_PLUGIN_MISSING: return "A requested output, dsp unit type or codec was not available.";
        case FMOD_ERR_PLUGIN_RESOURCE: return "A resource that the plugin requires cannot be found. (ie the DLS file for MIDI playback)";
        case FMOD_ERR_PLUGIN_VERSION: return "A plugin was built with an unsupported SDK version.";
        case FMOD_ERR_RECORD: return "An error occurred trying to initialize the recording device.";
        case FMOD_ERR_REVERB_CHANNELGROUP: return "Reverb properties cannot be set on this channel because a parent channelgroup owns the reverb connection.";
        case FMOD_ERR_REVERB_INSTANCE: return "Specified instance in FMOD_REVERB_PROPERTIES couldn't be set. Most likely because it is an invalid instance number or the reverb doesn't exist.";
        case FMOD_ERR_SUBSOUNDS: return "The error occurred because the sound referenced contains subsounds when it shouldn't have, or it doesn't contain subsounds when it should have.  The operation may also not be able to be performed on a parent sound.";
        case FMOD_ERR_SUBSOUND_ALLOCATED: return "This subsound is already being used by another sound, you cannot have more than one parent to a sound.  Null out the other parent's entry first.";
        case FMOD_ERR_SUBSOUND_CANTMOVE: return "Shared subsounds cannot be replaced or moved from their parent stream, such as when the parent stream is an FSB file.";
        case FMOD_ERR_TAGNOTFOUND: return "The specified tag could not be found or there are no tags.";
        case FMOD_ERR_TOOMANYCHANNELS: return "The sound created exceeds the allowable input channel count.  This can be increased using the 'maxinputchannels' parameter in System::setSoftwareFormat.";
        case FMOD_ERR_TRUNCATED: return "The retrieved string is too long to fit in the supplied buffer and has been truncated.";
        case FMOD_ERR_UNIMPLEMENTED: return "Something in FMOD hasn't been implemented when it should be! contact support!";
        case FMOD_ERR_UNINITIALIZED: return "This command failed because System::init or System::setDriver was not called.";
        case FMOD_ERR_UNSUPPORTED: return "A command issued was not supported by this object.  Possibly a plugin without certain callbacks specified.";
        case FMOD_ERR_VERSION: return "The version number of this file format is not supported.";
        case FMOD_ERR_EVENT_ALREADY_LOADED: return "The specified bank has already been loaded.";
        case FMOD_ERR_EVENT_LIVEUPDATE_BUSY: return "The live update connection failed due to the game already being connected.";
        case FMOD_ERR_EVENT_LIVEUPDATE_MISMATCH: return "The live update connection failed due to the game data being out of sync with the tool.";
        case FMOD_ERR_EVENT_LIVEUPDATE_TIMEOUT: return "The live update connection timed out.";
        case FMOD_ERR_EVENT_NOTFOUND: return "The requested event, bus or vca could not be found.";
        case FMOD_ERR_STUDIO_UNINITIALIZED: return "The Studio::System object is not yet initialized.";
        case FMOD_ERR_STUDIO_NOT_LOADED: return "The specified resource is not loaded, so it can't be unloaded.";
        case FMOD_ERR_INVALID_STRING: return "An invalid string was passed to this function.";
        case FMOD_ERR_ALREADY_LOCKED: return "The specified resource is already locked.";
        case FMOD_ERR_NOT_LOCKED: return "The specified resource is not locked, so it can't be unlocked.";
        case FMOD_ERR_RECORD_DISCONNECTED: return "The specified recording driver has been disconnected.";
        case FMOD_ERR_TOOMANYSAMPLES: return "The length provided exceeds the allowable limit.";
    }
    return "RESULT CODE UNDEFINED";
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_MainActivity_createFMODJNI(
        JNIEnv* env,
        jobject) {

    //https://github.com/Themaister/libmaru/blob/master/cuse-maru/test/sweep.c
    //http://manuals.opensound.com/developer/oss_errno.html

    addConsoleLine("========= OSS TEST ==========");

    int fd = open("/dev/usb/003", O_WRONLY); //???

    if(fd < 0)addConsoleLine(" OSS DEV ERROR: "+std::to_string(errno));

    addConsoleLine(" OSS DEV: "+std::to_string(fd));



    addConsoleLine("=========createFMODJNI==========");

    FMOD_RESULT       result;
    unsigned int      version;
    int             numDrivers;
    void             *extradriverdata = 0;

    result = FMOD::System_Create(&fmod_system);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:System_Create failed - "+FMOD_RESULT_STRING(result));
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

    result = fmod_system->init(32, FMOD_INIT_NORMAL, extradriverdata);
    if (result != FMOD_OK){
        addConsoleLine(" FMOD:System::init failed - "+FMOD_RESULT_STRING(result));
        return 0;
    }
    return 1;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_ChannelData_loadSoundJNI(
        JNIEnv* env,
        jobject,
        jstring filename,
        jint index) {

    if(!fmod_system)return 0;

    const char* path_cstr = env->GetStringUTFChars(filename, NULL);

    FMOD_RESULT result = fmod_system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sounds[index]);
    addConsoleLine("loadSound: " + std::string(path_cstr));
    if (result == FMOD_OK){
        sounds[index]->setLoopCount(-1);
        sounds[index]->setMode(FMOD_LOOP_NORMAL);
        return 1;
    }else{
        addConsoleLine("loadSound: ["+std::to_string(result)+" "+FMOD_RESULT_STRING(result)+"] ");
    }
    return 0;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_ChannelData_playSoundJNI(
        JNIEnv* env,
        jobject,
        jint index) {

    if(!fmod_system)return 0;
    if(index > 7 || index < 0 || !sounds[index])return 0;
    if(channels[index]) {
        bool isPlaying = false;
        channels[index]->isPlaying(&isPlaying);
        if(isPlaying)return 0;
    }
    fmod_system->playSound(sounds[index], 0, false, &channels[index]);
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_ChannelData_soundChannelMixJNI(
        JNIEnv* env,
        jobject,
        jint index,
        jfloat al,
        jfloat ar,
        jfloat bl,
        jfloat br) {

    if(!fmod_system)return 0;
    if(index > 7 || index < 0 || !channels[index])return 0;
    bool isPlaying = false;
    channels[index]->isPlaying(&isPlaying);
    if(isPlaying){
        channels[index]->setMixLevelsOutput(al, ar, 0, 0, 0, 0, bl, br);
    }
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nativetest_nativetestapp_ChannelData_stopSoundJNI(
        JNIEnv* env,
        jobject,
        jint index) {

    if(!fmod_system)return 0;
    if(index > 7 || index < 0 || !channels[index])return 0;
    bool isPlaying = false;
    channels[index]->isPlaying(&isPlaying);
    if(isPlaying)channels[index]->stop();
    return 0;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nativetest_nativetestapp_ChannelData_isPlayingSoundJNI(
        JNIEnv* env,
        jobject,
        jint index) {

    if(!fmod_system)return false;
    if(index > 7 || index < 0 || !sounds[index])return false;
    bool isPlaying = false;
    channels[index]->isPlaying(&isPlaying);
    if(isPlaying)return true;
    return false;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nativetest_nativetestapp_MainActivity_getDebugStringJNI(
        JNIEnv* env,
        jobject) {
    std::string str = consoleStr;
    consoleStr.empty();
    return env->NewStringUTF(str.c_str());
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
