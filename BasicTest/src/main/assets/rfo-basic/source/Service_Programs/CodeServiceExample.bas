

REM Example for CodeService.bas

FN.DEF ReLaunch(basEngine$, basProgramPath$, mode, mStart, mEnd)
 eMode$ = ""
 IF mode > 0
  eMode$ = "_Editor"
  IF mStart > -1 & mEnd > -1 THEN eMode$ = eMode$ + "?start=" + INT$(mStart) + "?end=" + INT$(mEnd)   
 ENDIF
 LIST.CREATE S, commandListPointer
 LIST.ADD commandListPointer~
 "new Intent(Intent.ACTION_MAIN);" ~
 "setData("+ CHR$(34) + basProgramPath$ + CHR$(34) +");" ~
 "new ComponentName("+ CHR$(34) + basEngine$ + CHR$(34) + ","+CHR$(34)+ basEngine$ + ".Basic" + CHR$(34)+");" ~
 "addCategory(Intent.CATEGORY_DEFAULT);" ~
 "putExtra("+ CHR$(34) + "_BASIC!" + CHR$(34) + ","+CHR$(34)+ eMode$ + CHR$(34)+");" ~ %Starts program in Editor mode, if eMode$ = "_Editor"!
 "addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);" ~
 "addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);" ~
 "addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);" ~
 "addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);" ~
 "EOCL"
 BUNDLE.PL appVarPointer,"_CommandList",commandListPointer
 APP.SAR appVarPointer
FN.END
FN.DEF InitReLaunch(objectValues)
 BUNDLE.GET objectValues, "basProgramPath", basProgramPath$
 BUNDLE.GET objectValues, "startSelection", startSelection
 BUNDLE.GET objectValues, "endSelection", endSelection
 basEngine$ ="com.rfo.basicTest" % You favorite BASIC! engine
 mode = 1
 CALL ReLaunch(basEngine$, basProgramPath$, mode, startSelection, endSelection)
FN.END
FN.DEF GetBasObjectValues (objectValues)
 GLOBALS.FNIMP ##$ % On start a line started with ##$ is added by Basic!. See also the RUN command.
 SPLIT.ALL spRes$[], ##$, "\\?"% ? is a control delimiter
 ARRAY.LENGTH al, spRes$[]
 IF al > 0
  BUNDLE.PUT objectValues, "basProgramPath", spRes$[1]
  BUNDLE.PUT objectValues, "startSelection", VAL(MID$(spRes$[2], 7))
  BUNDLE.PUT objectValues, "endSelection", VAL(MID$(spRes$[3], 5))
 ENDIF
FN.END

!*** Main Part ***
BUNDLE.CREATE objectValues
GetBasObjectValues (objectValues)

!* The Selection Parameters 
!* If startSelection = endSelection THEN it is the cursor position
!* The position values start with 0 (before the first sign)
BUNDLE.GET objectValues, "startSelection", startSelection
BUNDLE.GET objectValues, "endSelection", endSelection
? startSelection
? endSelection

CLIPBOARD.GET mClipData$ % Get automatically created text from the selected Basic source contents
? mClipData$

!* Insert your code here!
sel = -6000
DIALOG.MESSAGE "Do your code here!", "In 6 seconds we will return!", sel

CLIPBOARD.PUT mClipData$

!* Set the new cursor/selection position(s)
BUNDLE.PUT objectValues, "startSelection", startSelection
BUNDLE.PUT objectValues, "endSelection", endSelection

InitReLaunch(objectValues)
EXIT
