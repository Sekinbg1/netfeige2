#!/bin/bash
# Android 项目 WSL2 构建脚本

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}Android 项目 WSL2 构建脚本${NC}"
echo -e "${GREEN}================================${NC}"

# 检查是否在 WSL2 环境中
if [ ! -d "/mnt/f" ]; then
    echo -e "${RED}错误: 未在 WSL2 环境中运行${NC}"
    exit 1
fi

# 进入项目目录
PROJECT_DIR="/mnt/f/Documents/AndroidStudioProjects/netfeige2"
if [ ! -d "$PROJECT_DIR" ]; then
    echo -e "${RED}错误: 项目目录不存在: $PROJECT_DIR${NC}"
    exit 1
fi

cd "$PROJECT_DIR"
echo -e "${YELLOW}项目目录: $PROJECT_DIR${NC}"

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo -e "${RED}错误: Java 未安装,请先安装 OpenJDK 17${NC}"
    echo "运行: sudo apt install -y openjdk-17-jdk"
    exit 1
fi

echo -e "${GREEN}✓ Java 版本:${NC}"
java -version

# 配置 Android SDK 路径 (优先使用 WSL2 原生 SDK)
if [ -d "$HOME/Android/Sdk" ]; then
    # 使用 WSL2 原生的 Android SDK
    export ANDROID_HOME="$HOME/Android/Sdk"
    export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
    echo -e "${GREEN}✓ 使用 WSL2 原生 Android SDK: $ANDROID_HOME${NC}"
else
    # 回退到 Windows 的 Android SDK
    export ANDROID_HOME="/mnt/c/Users/Administrator/AppData/Local/Android/Sdk"
    export ANDROID_SDK_ROOT="/mnt/c/Users/Administrator/AppData/Local/Android/Sdk"
    echo -e "${YELLOW}⚠ 使用 Windows Android SDK: $ANDROID_HOME${NC}"
    echo -e "${YELLOW}提示: 建议在 WSL2 中安装原生 Android SDK 以获得更好的兼容性${NC}"
fi

# 检查 gradlew 是否有执行权限
if [ ! -x "./gradlew" ]; then
    echo -e "${YELLOW}授予 gradlew 执行权限...${NC}"
    chmod +x gradlew
fi

# 清理之前的构建
echo -e "${YELLOW}清理之前的构建...${NC}"
./gradlew clean

# 构建 Release APK
echo -e "${YELLOW}开始构建 Release APK...${NC}"
./gradlew assembleRelease

# 检查构建结果
if [ $? -eq 0 ]; then
    echo -e "${GREEN}================================${NC}"
    echo -e "${GREEN}✓ 构建成功!${NC}"
    echo -e "${GREEN}================================${NC}"
    
    # 显示生成的 APK 文件
    APK_DIR="app/build/outputs/apk/release"
    if [ -d "$APK_DIR" ]; then
        echo -e "${YELLOW}生成的 APK 文件:${NC}"
        ls -lh "$APK_DIR"/*.apk 2>/dev/null || echo "未找到 APK 文件"
    fi
else
    echo -e "${RED}================================${NC}"
    echo -e "${RED}✗ 构建失败${NC}"
    echo -e "${RED}================================${NC}"
    exit 1
fi
