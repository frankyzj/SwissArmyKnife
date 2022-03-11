@echo off
setlocal EnableDelayedExpansion
set /p name=请输入文件名称后回车，不包含后缀：
set a=0
pause
for /f "delims=" %%i in ('dir /a-d /b *.jpg') do (
	set /a a+=1
	echo !a!
	set fn="000!a!"
	echo !fn!
	echo !fn:~-4!
	echo %%i
	ren "%%i" "%name%!fn:~-4!.jpg"
	
)

pause