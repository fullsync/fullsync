; FullSync script for Nullsoft Installer
; $Id$

  ;Compression options
  CRCCheck on
  SetCompress force
  SetCompressor lzma
  SetDatablockOptimize on

  Name "FullSync"

  ;Product information
  VIAddVersionKey ProductName "FullSync"
  VIAddVersionKey FileDescription "FullSync Installer"
  VIAddVersionKey LegalCopyright "Copyright (c) 2004 Jan Kopcsek"
  VIAddVersionKey FileVersion "0.1"
  VIAddVersionKey ProductVersion "@VERSION@"
  VIProductVersion @VERSION@.0

!include "MUI.nsh"
!include "StrFunc.nsh"
${StrRep}

;--------------------------------
;Configuration

  !define MUI_ABORTWARNING

  !define TEMP1 $R0
  !define TEMP2 $R1

  ;!define MUI_ICON tomcat.ico
  ;!define MUI_UNICON tomcat.ico

  ;General
  OutFile fullsync-installer.exe

  ;Install Options pages
  LangString TEXT_JVM_TITLE ${LANG_ENGLISH} "Java Virtual Machine"
  LangString TEXT_JVM_SUBTITLE ${LANG_ENGLISH} "Java Virtual Machine path selection."
  LangString TEXT_JVM_PAGETITLE ${LANG_ENGLISH} ": Java Virtual Machine path selection"

  ;Install Page order
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE LICENSE
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  Page custom SetChooseJVM Void "$(TEXT_JVM_PAGETITLE)"
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  ;Uninstall Page order
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

  ;License dialog
  LicenseData LICENSE

  ;Component-selection page
    ;Descriptions
    LangString DESC_SecFullSync ${LANG_ENGLISH} "Install FullSync."
    LangString DESC_SecFullSyncSource ${LANG_ENGLISH} "Install the FullSync source code."
    ;LangString DESC_SecTomcatDocs ${LANG_ENGLISH} "Install the Tomcat documentation bundle. This include documentation on the servlet container and its configuration options, on the Jasper JSP page compiler, as well as on the native webserver connectors."
    LangString DESC_SecMenu ${LANG_ENGLISH} "Create a Start Menu program group for FullSync."

  ;Language
  !insertmacro MUI_LANGUAGE English

  ;Folder-select dialog
  InstallDir "$PROGRAMFILES\FullSync"

  ;Install types
  InstType Normal
  ;InstType Minimum
  InstType Full

  ; Main registry key
  InstallDirRegKey HKLM "SOFTWARE\FullSync" ""

  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
  ReserveFile "jvm.ini"

;--------------------------------
;Installer Sections

Section "FullSync" SecFullSync

  SectionIn 1 2

  IfSilent +2 0
  Call checkJvm

  SetOutPath $INSTDIR
  ;File fullsync.ico
  File LICENSE
  File /r bin
  ;File /nonfatal /r temp
  ;SetOutPath $INSTDIR\webapps
  ;File /r webapps\balancer

  IfSilent 0 +3
  Call findJavaPath
  Pop $2

  IfSilent +2 0
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"

  ;CopyFiles /SILENT "$2\lib\tools.jar" "$INSTDIR\common\lib" 4500
  ;ClearErrors

  Call configure
  Call findJavaPath
  Pop $2

  IfSilent +2 0
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"

  Push $2
  Call findJVMPath
  Pop $2

  DetailPrint "Using Jvm: $2"

  ;nsExec::ExecToLog '"$INSTDIR\bin\tomcat5.exe" //IS//Tomcat5 --DisplayName "Apache Tomcat" --Description "Apache Tomcat @VERSION@ Server - http://jakarta.apache.org/tomcat/" --LogPath "$INSTDIR\logs" --Install "$INSTDIR\bin\tomcat5.exe" --Jvm "$2"'
  ;ClearErrors

SectionEnd

;Section "Service" SecTomcatService
;  SectionIn 2

;  IfSilent 0 +3
;  Call findJavaPath
;  Pop $2

;  IfSilent +2 0
;  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"

;  Push $2
;  Call findJVMPath
;  Pop $2

;  nsExec::ExecToLog '"$INSTDIR\bin\tomcat5.exe" //US//Tomcat5 --Startup auto'

;  ClearErrors

;SectionEnd

Section "Source Code" SecFullSyncSource

  SectionIn 2
  SetOutPath $INSTDIR
  File /r source

SectionEnd

;Section "Documentation" SecTomcatDocs

;  SectionIn 1 3
;  SetOutPath $INSTDIR\webapps
;  File /r webapps\tomcat-docs

;SectionEnd

Section "Start Menu Items" SecMenu

  SectionIn 1 2

  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"

  SetOutPath "$SMPROGRAMS\FullSync"

  CreateShortCut "$SMPROGRAMS\FullSync\FullSync Home Page.lnk" \
                 "http://fullsync.sourceforge.net"

  CreateShortCut "$SMPROGRAMS\FullSync\FullSync.lnk" \
                 "$INSTDIR\bin\fullsync.bat"

;  IfFileExists "$INSTDIR\webapps\webapps\tomcat-docs" 0 NoDocumentaion

;  CreateShortCut "$SMPROGRAMS\Apache Tomcat 5.0\Tomcat Documentation.lnk" \
                 "$INSTDIR\webapps\tomcat-docs\index.html"

;NoDocumentaion:

  CreateShortCut "$SMPROGRAMS\FullSync\Uninstall.lnk" \
                 "$INSTDIR\Uninstall.exe"

;  CreateShortCut "$SMPROGRAMS\Apache Tomcat 5.0\Monitor Tomcat.lnk" \
;                 "$INSTDIR\bin\tomcat5w.exe" \
;                 '//MS//Tomcat5' \
;                 "$INSTDIR\tomcat.ico" 0 SW_SHOWNORMAL

SectionEnd

Section -post
;  nsExec::ExecToLog '"$INSTDIR\bin\tomcat5.exe" //US//Tomcat5 --Classpath "$INSTDIR\bin\bootstrap.jar" --StartClass org.apache.catalina.startup.Bootstrap --StopClass org.apache.catalina.startup.Bootstrap --StartParams start --StopParams stop  --StartMode jvm --StopMode jvm'
;  nsExec::ExecToLog '"$INSTDIR\bin\tomcat5.exe" //US//Tomcat5 --JvmOptions "-Dcatalina.home=$INSTDIR#-Djava.endorsed.dirs=$INSTDIR\common\endorsed#-Djava.io.tmpdir=$INSTDIR" --StdOutput "$INSTDIR\logs\stdout.log" --StdError "$INSTDIR\logs\stderr.log"'

  WriteUninstaller "$INSTDIR\Uninstall.exe"

  WriteRegStr HKLM "SOFTWARE\FullSync" "InstallPath" $INSTDIR
  WriteRegStr HKLM "SOFTWARE\FullSync" "Version" @VERSION@
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\FullSync" \
                   "DisplayName" "FullSync (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\FullSync" \
                   "UninstallString" '"$INSTDIR\Uninstall.exe"'

SectionEnd

Function .onInit

  ;Extract Install Options INI Files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jvm.ini"

FunctionEnd

Function SetChooseJVM
  !insertmacro MUI_HEADER_TEXT "$(TEXT_JVM_TITLE)" "$(TEXT_JVM_SUBTITLE)"
  Call findJavaPath
  Pop $3
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jvm.ini" "Field 2" "State" $3
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "jvm.ini"
FunctionEnd

Function Void
FunctionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecFullSync} $(DESC_SecTomcat)
;  !insertmacro MUI_DESCRIPTION_TEXT ${SecTomcatService} $(DESC_SecTomcatService)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecFullSyncSource} $(DESC_SecTomcatSource)
;  !insertmacro MUI_DESCRIPTION_TEXT ${SecTomcatDocs} $(DESC_SecTomcatDocs)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecMenu} $(DESC_SecMenu)
!insertmacro MUI_FUNCTION_DESCRIPTION_END


; =====================
; FindJavaPath Function
; =====================
;
; Find the JAVA_HOME used on the system, and put the result on the top of the
; stack
; Will return an empty string if the path cannot be determined
;
Function findJavaPath

  ClearErrors

  ReadEnvStr $1 JAVA_HOME

  IfErrors 0 FoundJDK

  ClearErrors

  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "RuntimeLib"

  FoundJDK:

  IfErrors 0 NoErrors
  StrCpy $1 ""

NoErrors:

  ClearErrors

  ; Put the result in the stack
  Push $1

FunctionEnd


; ====================
; FindJVMPath Function
; ====================
;
; Find the full JVM path, and put the result on top of the stack
; Argument: JVM base path (result of findJavaPath)
; Will return an empty string if the path cannot be determined
;
Function findJVMPath

  Pop $1

  IfFileExists "$1\jre\bin\hotspot\jvm.dll" 0 TryJDK14
    StrCpy $2 "$1\jre\bin\hotspot\jvm.dll"
    Goto EndIfFileExists
  TryJDK14:
  IfFileExists "$1\jre\bin\server\jvm.dll" 0 TryClassic
    StrCpy $2 "$1\jre\bin\server\jvm.dll"
    Goto EndIfFileExists
  TryClassic:
  IfFileExists "$1\jre\bin\classic\jvm.dll" 0 JDKNotFound
    StrCpy $2 "$1\jre\bin\classic\jvm.dll"
    Goto EndIfFileExists
  JDKNotFound:
    SetErrors
  EndIfFileExists:

  IfErrors 0 FoundJVMPath

  ClearErrors

  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "RuntimeLib"
  
  FoundJVMPath:

  IfErrors 0 NoErrors
  StrCpy $2 ""

NoErrors:

  ClearErrors

  ; Put the result in the stack
  Push $2

FunctionEnd


; ====================
; CheckJvm Function
; ====================
;
Function checkJvm

  !insertmacro MUI_INSTALLOPTIONS_READ $3 "jvm.ini" "Field 2" "State"
  IfFileExists "$3\bin\java.exe" NoErrors1
  MessageBox MB_OK "No Java Virtual Machine found."
  Quit
NoErrors1:
  Push $3
  Call findJVMPath
  Pop $4
  StrCmp $4 "" 0 NoErrors2
  MessageBox MB_OK "No Java Virtual Machine found."
  Quit
NoErrors2:

FunctionEnd


; =================
; CopyFile Function
; =================
;
; Copy specified file contents to $R9
;
Function copyFile

  ClearErrors

  Pop $0

  FileOpen $1 $0 r

 NoError:

  FileRead $1 $2
  IfErrors EOF 0
  FileWrite $R9 $2

  IfErrors 0 NoError

 EOF:

  FileClose $1

  ClearErrors

FunctionEnd


;--------------------------------
;Uninstaller Section

Section Uninstall

  Delete "$INSTDIR\modern.exe"
  Delete "$INSTDIR\Uninstall.exe"

  ; Delete Tomcat service
;  nsExec::ExecToLog '"$INSTDIR\bin\tomcat5.exe" //DS//Tomcat5'
  ClearErrors

  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\FullSync"
  DeleteRegKey HKLM "SOFTWARE\FullSync"
  RMDir /r "$SMPROGRAMS\FullSync"
  Delete "$INSTDIR\fullsync.ico"
  Delete "$INSTDIR\LICENSE"
  RMDir /r "$INSTDIR\bin"
  RMDir /r "$INSTDIR\source"
  RMDir "$INSTDIR"

  ; if $INSTDIR was removed, skip these next ones
  IfFileExists "$INSTDIR" 0 Removed 
    MessageBox MB_YESNO|MB_ICONQUESTION \
      "Remove all files in your Tomcat 5.0 directory? (If you have anything\
 you created that you want to keep, click No)" IDNO Removed
    Delete "$INSTDIR\*.*" ; this would be skipped if the user hits no
    RMDir /r "$INSTDIR"
    Sleep 500
    IfFileExists "$INSTDIR" 0 Removed 
      MessageBox MB_OK|MB_ICONEXCLAMATION \
                 "Note: $INSTDIR could not be removed."
  Removed:

SectionEnd

;eof

