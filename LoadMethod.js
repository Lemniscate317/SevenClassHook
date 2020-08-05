function main() {


    // const libart = Module.load("libart.so")
    // var artExports = libart.enumerateExports()
    // for (var i = 0; i < artExports.length; i++) {
    //     if (artExports[i].name.indexOf("LoadMethod")> 0) {
    //         console.log(artExports[i].name);
    //     }
    // }

    var loadMethod_addr = Module.findExportByName("libart.so","_ZN3art11ClassLinker10LoadMethodERKNS_7DexFileERKNS_21ClassDataItemIteratorENS_6HandleINS_6mirror5ClassEEEPNS_9ArtMethodE")
    console.log("loadMethod_addr",loadMethod_addr)

    var addr = []

    var first  = true

    Interceptor.attach(loadMethod_addr,{
        onEnter:function(args){
            if(!first)return
            first = false

            var dexFile = args[1]

            var dexPtr = ptr(dexFile)
            console.log("dexPtr",dexPtr);

            var base = dexPtr.add(Process.pointerSize).readPointer()
            var size = dexPtr.add(Process.pointerSize*2).readUInt()

            console.log("base:",base,"size",size);

            console.log(dexPtr.readByteArray(40));

            // var isContain = false
            // for(var i in addr){
            //     console.log(i);
            //     if(i==dexFile){
            //         isContain = true
            //         //console.log(i+"  "+dexFile);
            //         break
            //     }
            // }
            // if(!isContain){
            //     addr.push(dexPtr)
            // }
        },onLeave:function(ret){

        }
    })

    console.log(addr);

}

setImmediate(main)

