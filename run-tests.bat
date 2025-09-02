@echo off
echo ========================================
echo    设备权限系统单元测试运行脚本
echo ========================================
echo.

cd /d %~dp0

echo [1/4] 清理测试缓存...
call mvn clean -q
if errorlevel 1 (
    echo 清理失败，请检查Maven配置
    pause
    exit /b 1
)

echo.
echo [2/4] 编译项目...
call mvn compile -q
if errorlevel 1 (
    echo 编译失败，请检查代码
    pause
    exit /b 1
)

echo.
echo [3/4] 编译测试...
call mvn test-compile -q
if errorlevel 1 (
    echo 测试编译失败，请检查测试代码
    pause
    exit /b 1
)

echo.
echo [4/4] 运行设备权限相关测试...
call mvn test -Dtest=DevicePermissionServiceTest,DevicePermissionControllerTest -q
if errorlevel 1 (
    echo 测试执行失败，请查看测试结果
    pause
    exit /b 1
)

echo.
echo ========================================
echo           测试执行完成！
echo ========================================
echo.
echo 测试覆盖的功能：
echo - 房主权限测试（访问所有设备）
echo - 家庭成员权限测试（基于角色默认权限）
echo - 访客权限测试（只能访问特定设备类型）
echo - 用户自定义权限测试（覆盖默认权限）
echo - 控制器接口测试
echo.
pause
