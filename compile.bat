@echo off
echo Compilando o projeto...

:: Criar diretório bin se não existir
if not exist "bin" mkdir bin

:: Compilar todos os arquivos .java
javac -d bin src\Main.java src\gui\*.java src\model\*.java src\service\*.java

:: Verificar se a compilação foi bem sucedida
if %errorlevel% equ 0 (
    echo Compilacao concluida com sucesso!
    echo Executando o programa...
    java -cp bin Main
) else (
    echo Erro na compilacao!
)
pause
