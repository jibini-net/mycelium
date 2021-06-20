cmake_minimum_required(VERSION 3.8)

project("Mycelium")

#
# Use the latest supported standards for C and C++ syntax
#
# The latest C standard supported by both Visual Studio Code 1.57.1 and CMake
# 3.8 is C11. The latest C++ standard supported is C++20.
#
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

#
# Temporary test executable; will be a library later on
#
add_executable(MyceliumCore
    "src/mycelium.c"
    "src/packet.c"
    "src/uuid.c"
)

#
# Link standard thread and syncrhonization libraries
#
if(UNIX)
    set(CMAKE_THREAD_LIBS_INIT "-lpthread")
    set(THREADS_PREFER_PTHREAD_FLAG ON)

    find_package(Threads REQUIRED)
    target_link_libraries(MyceliumCore PRIVATE Threads::Threads)
endif(UNIX)

#
# Include project headers for developing dependent applications
#
include_directories("${PROJECT_SOURCE_DIR}/include")