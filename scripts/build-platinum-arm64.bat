@echo off
chcp 65001 >nul
echo =========================================
echo 在 WSL2 中编译 Platinum arm64-v8a 库
echo =========================================
echo.

REM 检查 WSL 是否可用
wsl --version >nul 2>&1
if errorlevel 1 (
	echo [错误] 未检测到 WSL2，请先安装 WSL2
	pause
	exit /b 1
)

echo [信息] 正在 WSL2 中执行编译脚本...
echo.

REM 在 WSL2 中执行编译脚本
wsl bash -c "cd /mnt/f/Documents/AndroidStudioProjects/netfeige2/scripts && chmod +x build-platinum-arm64.sh && ./build-platinum-arm64.sh"

if errorlevel 1 (
	echo.
	echo [错误] 编译失败
	pause
	exit /b 1
)

echo.
echo =========================================
echo 编译完成！
echo =========================================
echo.
echo 请检查 app/libs/arm64-v8a/ 目录是否有 libplatinum-jni.so
echo.
pause
