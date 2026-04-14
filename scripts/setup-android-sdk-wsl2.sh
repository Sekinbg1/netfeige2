#!/bin/bash
# 在 WSL2 中安装 Android SDK

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}WSL2 Android SDK 安装脚本${NC}"
echo -e "${GREEN}================================${NC}"

# 检查是否在 WSL2 环境中
if [ ! -d "/mnt/f" ]; then
	echo -e "${RED}错误: 未在 WSL2 环境中运行${NC}"
	exit 1
fi

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
	echo -e "${RED}错误: Java 未安装${NC}"
	echo "正在安装 OpenJDK 17..."
	sudo apt update
	sudo apt install -y openjdk-17-jdk
fi

echo -e "${GREEN}✓ Java 版本:${NC}"
java -version

# 安装必要的依赖
echo -e "${YELLOW}安装必要的依赖包...${NC}"
sudo apt update
sudo apt install -y wget unzip zip lib32stdc++6 lib32z1

# 下载 Android Command Line Tools
SDK_DIR="$HOME/Android/Sdk"
CMDLINE_TOOLS_DIR="$SDK_DIR/cmdline-tools"
LATEST_VERSION="11076708"  # Command line tools latest version

if [ ! -d "$SDK_DIR" ]; then
	echo -e "${YELLOW}创建 Android SDK 目录...${NC}"
	mkdir -p "$CMDLINE_TOOLS_DIR"

	echo -e "${YELLOW}下载 Android Command Line Tools...${NC}"
	cd /tmp
	wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${LATEST_VERSION}_latest.zip" -O cmdline-tools.zip

	echo -e "${YELLOW}解压 Command Line Tools...${NC}"
	unzip -q cmdline-tools.zip
	mv cmdline-tools "$CMDLINE_TOOLS_DIR/latest"
	rm cmdline-tools.zip

	cd -
else
	echo -e "${GREEN}✓ Android SDK 目录已存在${NC}"
fi

# 配置环境变量
echo -e "${YELLOW}配置环境变量...${NC}"
export ANDROID_HOME="$SDK_DIR"
export ANDROID_SDK_ROOT="$SDK_DIR"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

# 接受许可证
echo -e "${YELLOW}接受 Android SDK 许可证...${NC}"
yes | sdkmanager --licenses > /dev/null 2>&1 || true

# 安装必要的 SDK 组件
echo -e "${YELLOW}安装 Android SDK 组件 (这可能需要几分钟)...${NC}"
sdkmanager \
	"platform-tools" \
	"platforms;android-34" \
	"build-tools;34.0.0" \
	"build-tools;33.0.1"

echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✓ Android SDK 安装完成!${NC}"
echo -e "${GREEN}================================${NC}"
echo -e "${YELLOW}SDK 位置: $SDK_DIR${NC}"
echo ""
echo -e "${YELLOW}请将以下内容添加到 ~/.bashrc 或 ~/.profile:${NC}"
echo ""
echo "export ANDROID_HOME=\$HOME/Android/Sdk"
echo "export ANDROID_SDK_ROOT=\$HOME/Android/Sdk"
echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools"
echo ""
echo -e "${YELLOW}然后运行: source ~/.bashrc${NC}"
