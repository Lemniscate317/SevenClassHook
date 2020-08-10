#include <jni.h>
#include <string>
#include <android/log.h>
#include <dlfcn.h>
#include <unistd.h>
#include <fcntl.h>

#include "DexFile.h"
#include<sys/stat.h>


extern "C" {
#include "inlineHook.h"
}

namespace art {

    class Thread;

    class JValue;

    class OatDexFile;

    class MemMap;

    class DexFile;

    class ClassDataItemIterator;

    class Handle;

    class ArtMethod {
    public:
        uint32_t nothing1;
        uint32_t nothing2;

        // Offset to the CodeItem.
        uint32_t dex_code_item_offset_;

        // Index into method_ids of the dex file associated with this method.
        uint32_t dex_method_index_;
    };
}

//void *(*old_strstr)(char *, char *) = nullptr;
//
//void *new_strstr(char *arg0, char *arg1) {
//    __android_log_print(4, "hookso", "strstr is called,arg1:%s,arg2:%s", arg0, arg1);
//    if (strcmp(arg1, "hookso") == 0) {
//        int a = 1;
//        return &a;
//    } else {
//        void *result = old_strstr(arg0, arg1);
//        return result;
//    };
//}
//
//void starthooklibc() {
//
//    void *libchandle = dlopen("libc.so", RTLD_NOW);
//    void *strstr_addr = dlsym(libchandle, "strstr");
//    if (registerInlineHook((uint32_t) strstr_addr, (uint32_t) new_strstr,
//                           (uint32_t **) &old_strstr) !=
//        ELE7EN_OK) {
//        return;
//    }
//    if (inlineHook((uint32_t) strstr_addr) == ELE7EN_OK) {
//        __android_log_print(4, "hookso", "hook libc.so->strstr success!");
//        //return -1;
//    }
//}

void *(*old_loadmethod3)(void *, void *, DexFile &,
                         art::ClassDataItemIterator &,
                         art::Handle *,
                         art::ArtMethod *) = nullptr;

void *new_loadmethod3(void *thiz, void *thread, DexFile &dex_file,
                      art::ClassDataItemIterator &it,
                      art::Handle *klass,
                      art::ArtMethod *artmethod) {

    if (strcmp((char *) dex_file.pHeader->magic, "dex\n035") != 0) {
        __android_log_print(4, "hookso", "not 035 return");
        return old_loadmethod3(thiz, thread, dex_file, it, klass, artmethod);
    }

    const DexHeader *base = dex_file.pHeader;
    size_t size = dex_file.pHeader->fileSize;


    void *pVoid = old_loadmethod3(thiz, thread, dex_file, it, klass, artmethod);

    uint32_t codeItemOffset = artmethod->dex_code_item_offset_;
    uint32_t idx = artmethod->dex_method_index_;

    __android_log_print(4, "hookso", "dexFile ptr:%p   codeItemOffset %i idx %i",
                        (void *) &dex_file, codeItemOffset, idx);


    if (idx < 0 || idx > 65535) {
        __android_log_print(4, "hookso", "method idx error");
        return pVoid;
    }


    //codeItemOffset 1872740752 idx 4104
    //codeItemOffset 1872142480 idx 1025
//    if (codeItemOffset == 1872740752 || codeItemOffset == 1872142480) {
//        __android_log_print(4, "hookso", "ship these offset");
//        return pVoid;
//    }


    long codeItem = (long) base + codeItemOffset;
    __android_log_print(4, "hookso", "code item  %p  try ptr:%p  insSize ptr:%p", (void *) codeItem,
                        (void *) (codeItem + 6), (void *) (codeItem + 12));

    __android_log_print(4, "hookso", "code item  %p  try size:%i  insSize size:%i",
                        (void *) codeItem,
                        *(short *) (codeItem + 6), *(short *) (codeItem + 12));

    return pVoid;
}

int checkExc(JNIEnv *env) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe(); // writes to logcat
        env->ExceptionClear();
        return 1;
    }
    return -1;
}

void hook() {
    void *libchandle = dlopen("libart.so", RTLD_NOW);
    void *loadMethod = dlsym(libchandle,
                             "_ZN3art11ClassLinker10LoadMethodEPNS_6ThreadERKNS_7DexFileERKNS_21ClassDataItemIteratorENS_6HandleINS_6mirror5ClassEEEPNS_9ArtMethodE");

    if (registerInlineHook((uint32_t) loadMethod, (uint32_t) new_loadmethod3,
                           (uint32_t **) &old_loadmethod3) != ELE7EN_OK) {
        __android_log_print(4, "hookso", "load loadmethod fail1");
        return;
    }
    if (inlineHook((uint32_t) loadMethod) != ELE7EN_OK) {
        __android_log_print(4, "hookso", "load loadmethod fail2");
        return;
    }
    __android_log_print(4, "hookso", "load loadmethod success");
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    jint result = -1;
    __android_log_print(4, "hook", "go into JNI_OnLoad");
    hook();
    if ((vm)->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        printf("err!!");
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}


