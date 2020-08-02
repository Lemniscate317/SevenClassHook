#include <jni.h>
#include <string>
#include <cassert>
#include <cstdlib>
#include <unistd.h>
#include <fstream>
#include <fcntl.h>
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

//void *(*old_Invoke)(void*, uint32_t *, uint32_t , void *,
//                     const char *) = nullptr;
//
//void *new_Invoke(void *self, uint32_t *args, uint32_t args_size, void *result,
//                 const char *shorty) {
//    LOGE("new_invoke");
//    return old_Invoke(self, args, args_size, result, shorty);
//}
//
void *(*old_Invoke)(void *thiz, art::Thread *, uint32_t *, uint32_t, art::JValue *,
                    const char *) = nullptr;

void *
new_Invoke(void *thiz, art::Thread *self, uint32_t *args, uint32_t args_size, art::JValue *result,
           const char *shorty) {
    LOGE("new_invoke");
    return old_Invoke(thiz, self, args, args_size, result, shorty);
}

//void *(*old_loadmethod)(art::DexFile& ,
//                        art::ClassDataItemIterator& ,
//                        art::Handle* ,
//                        art::ArtMethod* ) = nullptr;
//
//void *new_loadmethod(art::DexFile& dex_file,
//                             art::ClassDataItemIterator& it,
//                             art::Handle* klass,
//                             art::ArtMethod* dst){
//
//    LOGE("new loadmehtod");
//    return old_loadmethod(dex_file, it, klass, dst);
//}


void dumpArtMethod(art::ArtMethod *artmethod) {
    char *dexfilepath = (char *) malloc(sizeof(char) * 2000);
    if (dexfilepath == nullptr) {
        LOGE("malloc 2000 byte failed");
        return;
    }
    int fcmdline = -1;
    char szCmdline[64] = {0};
    char szProcName[256] = {0};
    int procid = getpid();
    sprintf(szCmdline, "/proc/%d/cmdline", procid);
    fcmdline = open(szCmdline, O_RDONLY, 0644);
    if (fcmdline > 0) {
        read(fcmdline, szProcName, 256);
        close(fcmdline);
    }


//    if (szProcName[0]) {
//
//        const DexFile *dex_file = artmethod->GetDexFile();
//        const char *methodname =
//                PrettyMethod(artmethod).c_str();
//        const uint8_t *begin_ = dex_file->Begin();
//        size_t size_ = dex_file->Size();
//
//        memset(dexfilepath, 0, 2000);
//        int size_int_ = (int) size_;
//
//        memset(dexfilepath, 0, 2000);
//        sprintf(dexfilepath, "%s", "/sdcard/fart");
//        mkdir(dexfilepath, 0777);
//
//        memset(dexfilepath, 0, 2000);
//        sprintf(dexfilepath, "/sdcard/fart/%s",
//                szProcName);
//        mkdir(dexfilepath, 0777);
//
//        memset(dexfilepath, 0, 2000);
//        sprintf(dexfilepath,
//                "/sdcard/fart/%s/%d_dexfile.dex",
//                szProcName, size_int_);
//        int dexfilefp = open(dexfilepath, O_RDONLY, 0666);
//        if (dexfilefp > 0) {
//            close(dexfilefp);
//            dexfilefp = 0;
//
//        } else {
//            dexfilefp =
//                    open(dexfilepath, O_CREAT | O_RDWR,
//                         0666);
//            if (dexfilefp > 0) {
//                write(dexfilefp, (void *) begin_,
//                      size_);
//                fsync(dexfilefp);
//                close(dexfilefp);
//            }
//
//
//        }
//    }
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

void *(*old_openCommon)(const uint8_t *base,
                        size_t size,
                        const std::string &location,
                        uint32_t location_checksum,
                        void *oat_dex_file,
                        bool verify,
                        bool verify_checksum,
                        std::string *error_msg,
                        void *verify_result) = nullptr;

void *new_openCommon(const uint8_t *base,
                     size_t size,
                     const std::string &location,
                     uint32_t location_checksum,
                     void *oat_dex_file,
                     bool verify,
                     bool verify_checksum,
                     std::string *error_msg,
                     void *verify_result) {

    LOGE("new open common");

    return old_openCommon(base, size, location, location_checksum, oat_dex_file, verify,
                          verify_checksum, error_msg, verify_result);

}

void hookLogic() {
//    if (sizeof(void *) == 8) {
//        const char *libartPath = "/system/lib64/libart.so";
//        old_Invoke = reinterpret_cast<void *(*)(void *, art::Thread *, uint32_t *,
//                                                uint32_t, art::JValue *,
//                                                const char *)>( SandInlineHookSym(libartPath,
//                                                                                  "_ZN3art9ArtMethod6InvokeEPNS_6ThreadEPjjPNS_6JValueEPKc",
//                                                                                  reinterpret_cast<void *>(new_Invoke)));
//    } else {
//        const char *libartPath = "/system/lib/libart.so";
//        old_Invoke = reinterpret_cast<void *(*)(void *, art::Thread *, uint32_t *,
//                                                uint32_t, art::JValue *,
//                                                const char *)>( SandInlineHookSym(libartPath,
//                                                                                  "_ZN3art9ArtMethod6InvokeEPNS_6ThreadEPjjPNS_6JValueEPKc",
//                                                                                  reinterpret_cast<void *>(new_Invoke)));
//    }

//    typedef void (*old_openCommon)(const uint8_t *base,
//                                   size_t size,
//                                   const std::string &location,
//                                   uint32_t location_checksum,
//                                   void *oat_dex_file,
//                                   bool verify,
//                                   bool verify_checksum,
//                                   std::string *error_msg,
//                                   void *verify_result);
//
//    old_openCommon oldOpenCommon_function = nullptr;
//
//    void *artBase = SandGetModuleBase("/system/lib64/libart.so");
//    if (sizeof(void *) == 4) {
//
//    } else {
////        unsigned long tmpaddr = (unsigned long) artBase + 0x194358;
////        void *testhookaddr = reinterpret_cast<void *>(tmpaddr);
////        LOGE("libart64.so base:%p,testhookaddr:%p", artBase, (void *) tmpaddr);
////        SandInlineHook(testhookaddr, reinterpret_cast<void *>(new_openCommon));
//
//        unsigned long tmpaddr = (unsigned long) artBase + 0xDC4B4;
//        void *testhookaddr = reinterpret_cast<void *>(tmpaddr);
//        LOGE("libart64.so base:%p,testhookaddr:%p", artBase, (void *) tmpaddr);
//        SandInlineHook(testhookaddr, reinterpret_cast<void *>(new_Invoke));
//    }


//    if (sizeof(void *) == 8) {
//        const char *libartPath = "/system/lib64/libart.so";
//        old_openCommon = reinterpret_cast<void *(*)(const uint8_t *base,
//                                                    size_t size,
//                                                    const std::string &location,
//                                                    uint32_t location_checksum,
//                                                    void *oat_dex_file,
//                                                    bool verify,
//                                                    bool verify_checksum,
//                                                    std::string *error_msg,
//                                                    void *verify_result)>( SandInlineHookSym(
//                libartPath,
//                "_ZN3art7DexFile10OpenCommonEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_PNS0_12VerifyResultE",
//                reinterpret_cast<void *>(new_openCommon)));
//    } else {
//        const char *libartPath = "/system/lib/libart.so";
//        old_openCommon = reinterpret_cast<void *(*)(const uint8_t *base,
//                                                    size_t size,
//                                                    const std::string &location,
//                                                    uint32_t location_checksum,
//                                                    void *oat_dex_file,
//                                                    bool verify,
//                                                    bool verify_checksum,
//                                                    std::string *error_msg,
//                                                    void *verify_result)>( SandInlineHookSym(
//                libartPath,
//                "_ZN3art7DexFile10OpenCommonEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_PNS0_12VerifyResultE",
//                reinterpret_cast<void *>(new_openCommon)));
//    }

//    if (sizeof(void *) == 8) {
//        const char *libartPath = "/system/lib64/libart.so";
//        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& ,
//                                                art::ClassDataItemIterator& ,
//                                                art::Handle* ,
//                                                art::ArtMethod* )>( SandInlineHookSym(libartPath,
//                                                                                        "_ZN3art15DexFileVerifier17CheckLoadMethodIdEjPKc",
//                                                                                        reinterpret_cast<void *>(new_loadmethod)));
//    } else {
//        const char *libartPath = "/system/lib/libart.so";
//        old_loadmethod = reinterpret_cast<void *(*)(art::DexFile& ,
//                                                    art::ClassDataItemIterator& ,
//                                                    art::Handle* ,
//                                                    art::ArtMethod* )>( SandInlineHookSym(libartPath,
//                                                                                             "_ZN3art15DexFileVerifier17CheckLoadMethodIdEjPKc",
//                                                                                             reinterpret_cast<void *>(new_loadmethod)));
//    }

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
