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

struct CodeItem {
    uint16_t registers_size_;            // the number of registers used by this code
    //   (locals + parameters)
    uint16_t ins_size_;                  // the number of words of incoming arguments to the method
    //   that this code is for
    uint16_t outs_size_;                 // the number of words of outgoing argument space required
    //   by this code for method invocation
    uint16_t tries_size_;                // the number of try_items for this instance. If non-zero,
    //   then these appear as the tries array just after the
    //   insns in this instance.
    uint32_t debug_info_off_;            // file offset to debug info stream
    uint32_t insns_size_in_code_units_;  // size of the insns array, in 2 byte code units
    uint16_t insns_[1];                  // actual array of bytecode.
};

typedef std::string (*prettyMethod)(void *thiz, art::ArtMethod *, bool);

typedef std::string (*prettyMethod1)(void *thiz, uint32_t method_idx, const DexFile &dex_file,
                                     bool with_signature);

typedef void (*dumpArtMethod)( art::ArtMethod *);

prettyMethod prettyMethodFunction;
prettyMethod1 prettyMethod1Function;
dumpArtMethod dumpArtMethodFunction;

void *(*old_loadmethod3)(void *, void *, DexFile &,
                         art::ClassDataItemIterator &,
                         art::Handle *,
                         art::ArtMethod *) = nullptr;

void *new_loadmethod3(void *thiz, void *thread, DexFile &dex_file,
                      art::ClassDataItemIterator &it,
                      art::Handle *klass,
                      art::ArtMethod *artmethod) {

//    if (strcmp((char *) dex_file.pHeader->magic, "dex\n035") != 0) {
//        __android_log_print(4, "hookso", "not 035 return");
//        return old_loadmethod3(thiz, thread, dex_file, it, klass, artmethod);
//    }




    void *pVoid = old_loadmethod3(thiz, thread, dex_file, it, klass, artmethod);
    __android_log_print(5, "hookso", "pVoid ptr:%p", pVoid);


//    const DexHeader *base = dex_file.pHeader;
//    size_t size = dex_file.pHeader->fileSize;
//
//    uint32_t codeItemOffset = artmethod->dex_code_item_offset_;
//    uint32_t idx = artmethod->dex_method_index_;

    dumpArtMethodFunction(artmethod);

    return pVoid;

//    if (idx < 0 || idx > 65535) {
//        __android_log_print(4, "hookso", "method idx error");
//        return pVoid;
//    }
//
//    const std::string &string = prettyMethodFunction(artmethod, artmethod,
//                                                     true);
//
//    if (strstr(string.c_str(), "com.android") != nullptr) {
//        __android_log_print(5, "hookso", "method name: %s skip", string.c_str());
//        return pVoid;
//    }
//    if (strstr(string.c_str(), "NativeCrashCollector") != nullptr) {
//        __android_log_print(5, "hookso", "method name: %s skip", string.c_str());
//        return pVoid;
//    }
//    if (strstr(string.c_str(), "java.net.") != nullptr) {
//        __android_log_print(5, "hookso", "method name: %s skip", string.c_str());
//        return pVoid;
//    }
//    if (strstr(string.c_str(), "android.os.") != nullptr) {
//        __android_log_print(5, "hookso", "method name: %s skip", string.c_str());
//        return pVoid;
//    }



    //codeItemOffset 1872740752 idx 4104 method name:java.util.concurrent.atomic.AtomicBoolean android.os.AsyncTask.-get0(android.os.AsyncTask)
    //codeItemOffset 1872142480 idx 1025
    //codeItemOffset 315166720 idx 9  codeItemOffset 315351040 idx 9
//    if (codeItemOffset == 315351040 && idx == 9) {
//        __android_log_print(4, "hookso", "ship these offset %i  idx %i", 315351040, 9);
//        return pVoid;
//    }


//    __android_log_print(4, "hookso", "dexFile ptr:%p   codeItemOffset %i idx %i",
//                        (void *) &dex_file, codeItemOffset, idx);
//
//
//    __android_log_print(4, "hookso", "method name:%s", string.c_str());
//
//
//    long codeItemAddr = (long) base + codeItemOffset;
//    CodeItem *codeItem = (CodeItem *) codeItemAddr;


//    __android_log_print(4, "hookso", "code item  %p  try size:%i  insSize size:%i", codeItem,
//                        codeItem->tries_size_, codeItem->insns_size_in_code_units_);

//    __android_log_print(4, "hookso", "code item  %p  try ptr:%p  insSize ptr:%p", (void *) codeItem,
//                        (void *) (codeItem + 6), (void *) (codeItem + 12));
//
//    __android_log_print(4, "hookso", "code item  %p  try size:%i  insSize size:%i",
//                        (void *) codeItem,
//                        *(short *) (codeItem + 6), *(short *) (codeItem + 12));

//    return pVoid;
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

    void *prettyMethodAddr = dlsym(libchandle,
                                   "_ZN3art12PrettyMethodEPNS_9ArtMethodEb");
    prettyMethodFunction = reinterpret_cast<prettyMethod >(prettyMethodAddr);

    void *prettyMethod1Addr = dlsym(libchandle,
                                    "_ZN3art12PrettyMethodEjRKNS_7DexFileEb");
    prettyMethod1Function = reinterpret_cast<prettyMethod1 >(prettyMethod1Addr);

    void *dumpArtMethodAddr = dlsym(libchandle,
                                    "XcustomArtMethod");
    __android_log_print(4, "hookso", "dumpArtMethod ptr:%p", dumpArtMethodAddr);
    dumpArtMethodFunction = reinterpret_cast<dumpArtMethod >(dumpArtMethodAddr);

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


