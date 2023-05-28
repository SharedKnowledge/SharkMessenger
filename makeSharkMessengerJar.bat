mkdir tempLibBuildFolder
cd tempLibBuildFolder
jar -x -f ..\libs\ASAPJava.jar
jar -x -f ..\libs\ASAPHub.jar
jar -x -f ..\libs\SharkPeer.jar
jar -x -f ..\libs\SharkPKI.jar
rmdir /s /q META-INF
xcopy /S ..\out\production\SharkMessenger\* .
jar cmvf META-INF/MANIFEST.MF SharkMessenger.jar *
cd ..
move tempLibBuildFolder\SharkMessenger.jar .
rmdir /s /q tempLibBuildFolder