//
// Created by Gagandeep-Nickelfox on 26/09/22.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_app_currencyconverter_utils_Constants_getSecrets(JNIEnv *env, jobject thiz) {
    std::string jsonData = "{\n"
                           "  \"base_url\": \"https://openexchangerates.org/api/\",\n"
                           "  \"api_key\": \"72c3948067204596b588e8b77fcb9f93\"\n"
                           "}";
    return env->NewStringUTF(jsonData.c_str());
}