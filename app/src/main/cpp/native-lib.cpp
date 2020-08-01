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

void *(*old_Invoke)(art::Thread *self, uint32_t *args, uint32_t args_size, art::JValue *result,
                     const char *shorty) = nullptr;

void *new_Invoke(art::Thread *self, uint32_t *args, uint32_t args_size, art::JValue *result,
                 const char *shorty) {
    LOGE("new_invoke");
    return old_Invoke(self, args, args_size, result, shorty);
}

void *(*old_loadmethod)(art::DexFile& dex_file,
                        art::ClassDataItemIterator& it,
                        art::Handle* klass,
                        art::ArtMethod* dst) = nullptr;

void *new_loadmethod(art::DexFile& dex_file,
                             art::ClassDataItemIterator& it,
                             art::Handle* klass,
                             art::ArtMethod* dst){

    LOGE("new loadmehtod");
    return old_loadmethod(dex_file, it, klass, dst);
}

void hookLogic() {
    if (sizeof(void *) == 8) {
        const char *libartPath = "/system/lib64/libart.so";
        old_Invoke = reinterpret_cast<void *(*)(art::Thread *self, uint32_t *args,
                                                uint32_t args_size, art::JValue *result,
                                                const char *shorty)>( SandInlineHookSym(libartPath,
                                                                                        "Invoke",
                                                                                        reinterpret_cast<void *>(new_Invoke)));
    } else {
        const char *libartPath = "/system/lib/libart.so";
        old_Invoke = reinterpret_cast<void *(*)(art::Thread *self, uint32_t *args,
                                                uint32_t args_size, art::JValue *result,
                                                const char *shorty)>( SandInlineHookSym(libartPath,
                                                                                        "Invoke",
                                                                                        reinterpret_cast<void *>(new_Invoke)));
    }

    if (sizeof(void *) == 8) {
        const char *libartPath = "/system/lib64/libart.so";
        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& dex_file,
                                                art::ClassDataItemIterator& it,
                                                art::Handle* klass,
                                                art::ArtMethod* dst)>( SandInlineHookSym(libartPath,
                                                                                        "LoadMethod",
                                                                                        reinterpret_cast<void *>(new_loadmethod)));
    } else {
        const char *libartPath = "/system/lib/libart.so";
        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& dex_file,
                                                    art::ClassDataItemIterator& it,
                                                    art::Handle* klass,
                                                    art::ArtMethod* dst)>( SandInlineHookSym(libartPath,
                                                                                             "LoadMethod",
                                                                                             reinterpret_cast<void *>(new_loadmethod)));
    }
}

extern "C" jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    LOGE("jni onload enter");
    hookLogic();
    LOGE("jni onload stop");

    return JNI_VERSION_1_6;
}
