cmake_minimum_required(VERSION 3.4.1)

set(SOURCES ${SOURCES} hdlc.c multimon.c demod_afsk12.c)
add_library(multimon SHARED ${SOURCES})

# Include libraries needed for kotlin-jni lib
target_link_libraries(multimon android log)
