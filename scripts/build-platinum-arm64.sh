#!/bin/bash
# 编译 Platinum JNI 库以支持 arm64-v8a 架构

set -e

echo "========================================="
echo "开始编译 Platinum arm64-v8a 库"
echo "========================================="

# 设置颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 检查是否在 WSL2 环境中
if grep -q Microsoft /proc/version 2>/dev/null || grep -q microsoft /proc/version 2>/dev/null; then
	echo -e "${YELLOW}检测到 WSL2 环境${NC}"
	IS_WSL=true
else
	echo -e "${YELLOW}检测到 Linux 环境${NC}"
	IS_WSL=false
fi

# 创建工作目录
WORK_DIR="$HOME/platinum-build"
mkdir -p "$WORK_DIR"
cd "$WORK_DIR"

echo -e "${YELLOW}[1/6] 克隆 Platinum 仓库...${NC}"
if [ ! -d "Platinum" ]; then
	git clone https://github.com/wesley666/Platinum.git
fi
cd Platinum

echo -e "${YELLOW}[2/6] 初始化子模块...${NC}"
git submodule update --init

echo -e "${YELLOW}[3/6] 下载 Android NDK r15c...${NC}"
NDK_DIR="$HOME/ndk/android-ndk-r15c"
if [ ! -d "$NDK_DIR" ]; then
	mkdir -p "$HOME/ndk"
	cd "$HOME/ndk"
	echo "正在下载 NDK (约 800MB)，请稍候..."
	wget -q https://dl.google.com/android/repository/android-ndk-r15c-linux-x86_64.zip -O android-ndk-r15c.zip
	unzip -q android-ndk-r15c.zip
	rm android-ndk-r15c.zip
	echo -e "${GREEN}✓ NDK 下载完成${NC}"
else
	echo -e "${GREEN}✓ NDK 已存在${NC}"
fi

cd "$WORK_DIR/Platinum"

echo -e "${YELLOW}[4/6] 安装编译依赖...${NC}"
# 检查 Python 2.7 或 Python 3
PYTHON_CMD=""
if command -v python2 &> /dev/null; then
	PYTHON_CMD="python2"
	echo "使用 Python 2"
elif command -v python &> /dev/null; then
	PYTHON_VERSION=$(python --version 2>&1 | grep -oP '\d+' | head -1)
	if [ "$PYTHON_VERSION" = "3" ]; then
		PYTHON_CMD="python"
		echo "使用 Python 3 (兼容模式)"
		# 创建 python2 符号链接以兼容旧脚本
		if [ ! -f /usr/local/bin/python2 ]; then
			sudo ln -sf $(which python) /usr/local/bin/python2 2>/dev/null || true
		fi
	else
		PYTHON_CMD="python"
		echo "使用 Python 2"
	fi
elif command -v python3 &> /dev/null; then
	PYTHON_CMD="python3"
	echo "使用 Python 3 (兼容模式)"
	# 创建 python2 符号链接以兼容旧脚本
	if [ ! -f /usr/local/bin/python2 ]; then
		sudo ln -sf $(which python3) /usr/local/bin/python2 2>/dev/null || true
	fi
else
	echo "安装 Python..."
	if [ -f /etc/debian_version ]; then
		sudo apt-get update && sudo apt-get install -y python3 python3-pip
		PYTHON_CMD="python3"
		# 创建 python2 符号链接
		sudo ln -sf $(which python3) /usr/local/bin/python2 2>/dev/null || true
	elif [ -f /etc/redhat-release ]; then
		sudo yum install -y python3
		PYTHON_CMD="python3"
		sudo ln -sf $(which python3) /usr/local/bin/python2 2>/dev/null || true
	fi
fi

if ! command -v scons &> /dev/null; then
	echo "安装 scons..."
	if [ -f /etc/debian_version ]; then
		sudo apt-get install -y scons
	elif [ -f /etc/redhat-release ]; then
		sudo yum install -y scons
	fi
fi

echo -e "${YELLOW}[5/6] 编译 arm64-android-linux 版本...${NC}"
export ANDROID_NDK_ROOT="$NDK_DIR"

# 清理之前的构建
scons -c target=arm64-android-linux build_config=Release || true

# 开始编译
scons target=arm64-android-linux build_config=Release

echo -e "${YELLOW}[6/6] 编译 JNI 库...${NC}"
cd Source/Platform/Android/module/platinum
"$NDK_DIR/ndk-build" clean
"$NDK_DIR/ndk-build" NDK_DEBUG=0

# 检查生成的文件
echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}✓ 编译完成！${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "生成的库文件位置："
ls -lh libs/*/libplatinum-jni.so 2>/dev/null || echo "未找到 libplatinum-jni.so"
echo ""

# 复制到项目目录
PROJECT_LIBS_DIR="/mnt/f/Documents/AndroidStudioProjects/netfeige2/app/libs"
if [ -d "$PROJECT_LIBS_DIR" ]; then
	echo -e "${YELLOW}复制库文件到项目...${NC}"
	mkdir -p "$PROJECT_LIBS_DIR/arm64-v8a"
	cp -f libs/arm64-v8a/libplatinum-jni.so "$PROJECT_LIBS_DIR/arm64-v8a/" 2>/dev/null || true

	# 如果 libgit-platinum.so 也存在，一并复制
	if [ -f "libs/arm64-v8a/libgit-platinum.so" ]; then
		cp -f libs/arm64-v8a/libgit-platinum.so "$PROJECT_LIBS_DIR/arm64-v8a/"
	fi

	echo -e "${GREEN}✓ 库文件已复制到: $PROJECT_LIBS_DIR/arm64-v8a/${NC}"
	echo ""
	echo "现在可以更新 build.gradle 启用 arm64-v8a 支持"
else
	echo -e "${RED}✗ 未找到项目目录: $PROJECT_LIBS_DIR${NC}"
	echo "请手动复制 libs/arm64-v8a/libplatinum-jni.so 到项目的 app/libs/arm64-v8a/ 目录"
fi

echo ""
echo -e "${GREEN}完成！${NC}"
