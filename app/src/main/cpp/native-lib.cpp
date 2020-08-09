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

    class ArtMethod;
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
                      art::ArtMethod *dst) {

    __android_log_print(4, "hookso", "loadmethod in");


    __android_log_print(4, "hookso", "magic %s %i %p", (char *) dex_file.pHeader->magic,
                        dex_file.pHeader->fileSize, (void *) dex_file.pHeader->magic);


    if (strcmp((char *) dex_file.pHeader->magic, "dex\n035") != 0) {
        __android_log_print(4, "hookso", "not 035 return");
        return old_loadmethod3(thiz, thread, dex_file, it, klass, dst);
    }

    const DexHeader *base = dex_file.pHeader;
    size_t size = dex_file.pHeader->fileSize;
//    __android_log_print(4, "new opheader::%p ", dex_file.pOptHeader);
//    __android_log_print(4, "new header::%p ", dex_file.pHeader);
//    __android_log_print(4, "new magic::%p ",
//                        dex_file.pHeader->magic);
//    __android_log_print(4, "new loadmehtod::%p  %i  %s", base, size, dex_file.pHeader->magic);


//    int pid = getpid();
//    char dexFilePath[100] = {0};
////    sprintf(dexFilePath, "/sdcard/xxxxx/%p %d LoadMethod.dex", base, size);
//    sprintf(dexFilePath, "/sdcard/xxxxx/%dLoadMethod.dex", size);
//    mkdir("/sdcard/xxxxx", 0777);
//
//    int fd = open(dexFilePath, O_CREAT | O_RDWR, 666);
//    if (fd > 0) {
//        ssize_t i = write(fd, (void *) base, size);
//        if (i > 0) {
//            close(fd);
//        }
//    }

    return old_loadmethod3(thiz, thread, dex_file, it, klass, dst);
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


