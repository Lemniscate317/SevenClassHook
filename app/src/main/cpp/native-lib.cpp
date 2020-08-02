#include <jni.h>
#include <string>
#include <cassert>
#include <cstdlib>
#include "sandhook_native.h"
#include "hook.h"
#include "elf.h"
#include "android/log.h"

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

void *(*old_Invoke)(art::Thread *, uint32_t *, uint32_t , art::JValue *,
                     const char *) = nullptr;

void *new_Invoke(art::Thread *self, uint32_t *args, uint32_t args_size, art::JValue *result,
                 const char *shorty) {
    LOGE("new_invoke");
    return old_Invoke(self, args, args_size, result, shorty);
}

void *(*old_loadmethod)(art::DexFile& ,
                        art::ClassDataItemIterator& ,
                        art::Handle* ,
                        art::ArtMethod* ) = nullptr;

void *new_loadmethod(art::DexFile& dex_file,
                             art::ClassDataItemIterator& it,
                             art::Handle* klass,
                             art::ArtMethod* dst){

    LOGE("new loadmehtod");
    return old_loadmethod(dex_file, it, klass, dst);
}

void *(*old_strstr)(char *, char *) = nullptr;

void *new_strstr(char *arg0, char *arg1) {
    __android_log_print(4, "hooksoarm64", "strstr is called,arg1:%s,arg2:%s", arg0, arg1);
    if (strcmp(arg1, "hookso") == 0) {
        int a = 1;
        return &a;
    } else {
        void *result = old_strstr(arg0, arg1);
        return result;
    };
}

void hookLogic() {
    if (sizeof(void *) == 8) {
        const char *libartPath = "/system/lib64/libart.so";
        old_Invoke = reinterpret_cast<void *(*)(art::Thread *, uint32_t *,
                                                uint32_t , art::JValue *,
                                                const char *)>( SandInlineHookSym(libartPath,
                                                                                        "Invoke",
                                                                                        reinterpret_cast<void *>(new_Invoke)));
    } else {
        const char *libartPath = "/system/lib/libart.so";
        old_Invoke = reinterpret_cast<void *(*)(art::Thread *, uint32_t *,
                                                uint32_t , art::JValue *,
                                                const char *)>( SandInlineHookSym(libartPath,
                                                                                        "Invoke",
                                                                                        reinterpret_cast<void *>(new_Invoke)));
    }

    if (sizeof(void *) == 8) {
        const char *libartPath = "/system/lib64/libart.so";
        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& ,
                                                art::ClassDataItemIterator& ,
                                                art::Handle* ,
                                                art::ArtMethod* )>( SandInlineHookSym(libartPath,
                                                                                        "_ZN3art15DexFileVerifier17CheckLoadMethodIdEjPKc",
                                                                                        reinterpret_cast<void *>(new_loadmethod)));
    } else {
        const char *libartPath = "/system/lib/libart.so";
        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& ,
                                                    art::ClassDataItemIterator& ,
                                                    art::Handle* ,
                                                    art::ArtMethod* )>( SandInlineHookSym(libartPath,
                                                                                             "_ZN3art15DexFileVerifier17CheckLoadMethodIdEjPKc",
                                                                                             reinterpret_cast<void *>(new_loadmethod)));
    }

//    if (sizeof(void *) == 8) {
//        const char *libcpath = "/system/lib64/libc.so";
//        old_strstr = reinterpret_cast<void *(*)(char *, char *)>(SandInlineHookSym(libcpath,
//                                                                                   "strstr",
//                                                                                   reinterpret_cast<void *>(new_strstr)));
//    } else {
//        const char *libcpath = "/system/lib/libc.so";
//        old_strstr = reinterpret_cast<void *(*)(char *, char *)>(SandInlineHookSym(libcpath,
//                                                                                   "strstr",
//                                                                                   reinterpret_cast<void *>(new_strstr)));
//    }
}

extern "C" jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    LOGE("jni onload enter");
    hookLogic();
    LOGE("jni onload stop");

    return JNI_VERSION_1_6;
}
