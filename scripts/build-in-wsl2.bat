@echo off
chcp 65001 >nul
echo ================================
echo Android 项目 WSL2 构建启动器
echo ================================
echo.

REM 检查 WSL2 是否安装
wsl --list --verbose >nul 2>&1
if errorlevel 1 (
	echo [错误] WSL2 未安装或未正确配置
	echo.
	echo 请先安装 WSL2:
	echo 1. 以管理员身份打开 PowerShell
	echo 2. 运行: wsl --install -d Ubuntu-22.04
	echo 3. 重启计算机
	echo.
	pause
	exit /b 1
)

echo [信息] 正在 WSL2 中启动构建...
echo.

REM 在 WSL2 中执行构建脚本
wsl bash -c "cd /mnt/f/Documents/AndroidStudioProjects/netfeige2 && chmod +x build-wsl2.sh && ./scripts/build-wsl2.sh"

if errorlevel 1 (
	echo.
	echo [错误] 构建失败,请查看上方错误信息
	pause
	exit /b 1
)

echo.
echo ================================
echo 构建完成!
echo ================================
pause
