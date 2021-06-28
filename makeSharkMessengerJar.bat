@ECHO off

SET outProdDir="out/production/SharkMessenger/"
 
@rem Check if the destination Directory exist
IF not exist outProdDir (
	mkdir %outProdDir%
)

CD %outProdDir%
jar -cf SharkMessenger.jar net
MOVE SharkMessenger.jar ../../../SharkMessenger.jar

@rem move back to the root of the project
CD ../../..