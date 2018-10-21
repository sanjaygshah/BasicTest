!!

******  READ!  *******

Sample program demonstrating
TCP/IP sockets. The program 
has two parts: a server side
and a client side. Both sides
need to be running on different
Android devices.

The Server will wait for a Client
to connect to it. When the
connection is made, the Server
will wait for a message from
the Client. Once the message
has been recieved, the Server
will send a message back
to the Client.

Some devices/networks do not
allow smart phone servers onto
their data network.

In that case, the server must be
inside a LAN.

LAN = Local Area Network (WiFi)
WAN = Wide Area Network (Internet)

If the Client is on the same LAN
as the Server, the Client can 
connect directly to the Server
using the Server's LAN IP.

If the Client is outside of the
Server LAN then the Client must
must connect using the Server's
WAN IP.

It will be necessary to program
the Server's LAN router to pass
a specific port through from the
WAN to Server's LAN IP

!!

 ARRAY.LOAD type$[], "Server", "Client"
 msg$ = "Select TCP/IP socket type"
 SELECT type, type$[], msg$
 IF type = 0
  "Thanks for playing"
  END
 ELSEIF type = 2
  GOTO doClient
 ENDIF

!***********  Server Demo  **************

 INPUT "Enter the port number", port, 1080

 SOCKET.MYIP ip$
 PRINT "LAN IP: " + ip$
 GRABURL ip$, "http://icanhazip.com"
 PRINT "WAN IP: " + ip$

 ! Create the server on the specified port
 SOCKET.SERVER.CREATE port

 newConnection:

 ! Connect to the next Client
 ! and print the Client IP
 PRINT "Waiting for client connect"
 SOCKET.SERVER.CONNECT 0
 DO
  SOCKET.SERVER.STATUS st
 UNTIL st = 3

 SOCKET.SERVER.CLIENT.IP ip$
 PRINT "Connected to ";ip$

 ! Connected to a Client
 ! Wait for Client to send a message
 ! or time out after 10 seconds

 maxclock = CLOCK() + 10000
 DO
  SOCKET.SERVER.READ.READY flag
  IF CLOCK() > maxclock
   PRINT "Read time out"
   END
  ENDIF
 UNTIL flag


 ! (1) Number the transmissions for a better overview.
 ! Message received. Read it.
 ! Print it
 SOCKET.SERVER.READ.LINE line$
 PRINT line$


 ! (2)
 ! Send a message back to the client
 SOCKET.SERVER.WRITE.LINE "Server to client message"


 ! (3)
 ! Send a single byte to the client
 byteAsNumber = 8364  %0 ... 65535
 out$ = CHR$(byteAsNumber )
 SOCKET.SERVER.WRITE.BYTES out$ 


 ! (4)
 ! Send a single byte text line to client
 SOCKET.SERVER.WRITE.BYTES "Server to client text line" + CHR$(10)


 ! (5)
 ! Send a file by single bytes to the client
 byte.open r,fr1, "cartman.png"
 DO
  byte.read.byte fr1, mByte
  t$ = chr$(mByte)
  SOCKET.SERVER.WRITE.BYTES t$
 UNTIL mByte < 0
 BYTE.CLOSE fr1
  
 
 ! (6)
 ! Send a file as stream to client
 BYTE.OPEN r,fr, "fly.gif"
 SOCKET.SERVER.WRITE.FILE fr
 BYTE.CLOSE fr
 
 
 ! (7)
 ! Send a file as stream to the client
 BYTE.OPEN r,fr, "boing.mp3"
 SOCKET.SERVER.WRITE.FILE fr
 BYTE.CLOSE fr


 ! (8)
 ! Send a file by single bytes to the client
 BYTE.OPEN r,fr, "galaxy.png"
 SOCKET.SERVER.WRITE.FILE fr
 BYTE.CLOSE fr


 ! (9)
 ! Send a file by text lines to the client
 TEXT.OPEN r,fr, "htmldemo1.html"
 DO
  TEXT.READLN fr, mLine$
  SOCKET.SERVER.WRITE.LINE mLine$
 UNTIL mLine$ = "EOF"
 TEXT.CLOSE fr


 ! (10)
 SOCKET.SERVER.WRITE.LINE "Finished Transmission"


! Finished this protocol
! Disconnect from Client

SOCKET.SERVER.DISCONNECT
PRINT "Disconnected from client"

! Loop to get the next Client

GOTO newConnection

! ****************** Client Demo ****************

doclient:

 INPUT "Enter the connect-to IP", ip$
 INPUT "Enter the port number", port, 1080

 ! Connect to the specified IP on the
 ! specified Port

 clientAgain:

 SOCKET.CLIENT.CONNECT ip$, port
 PRINT "Connected"

 ! (1) Number the transmissions for a better overview.
 ! When the connection is established,
 ! send the server a message
 SOCKET.CLIENT.WRITE.LINE "Client to server message"

 ! and then wait for Server to respond
 ! or time out after 10 seconds

 maxclock = CLOCK() + 10000
 DO
  SOCKET.CLIENT.READ.READY flag
  IF CLOCK() > maxclock
   PRINT "Read time out"
   END
  ENDIF
 UNTIL flag

 ! (2)
 ! Server has sent message.
 ! Read it. Print it.
 SOCKET.CLIENT.READ.LINE line$
 PRINT line$ 


 ! (3)
 ! Server has sent a single Byte.
 ! Read it. Print it.
 SOCKET.CLIENT.READ.BYTE in$
 byteAsNumber = UCODE(in$)
 PRINT "Single Byte from Server: "; in$; "  As number: ";byteAsNumber
 

 ! (4)
 ! Server has sent a text line as byte array.
 ! Read it. Print it.
 SOCKET.CLIENT.READ.LINE line$
 PRINT line$ 


 ! (5)
 ? "A_SocketTest_cartman.png"
 ! Server has sent a file as byte array.
 ! Client reseived with single Bytes SLOW
 BYTE.OPEN w, fw, "A_SocketTest_cartman.png"
 DO
  SOCKET.CLIENT.READ.BYTE mByte$
  if UCODE(mByte$) <> 65535
    Byte.write.byte fw , mByte$
  endif
  SOCKET.CLIENT.READ.READY flag
 until UCODE(mByte$) = 65535
 BYTE.CLOSE fw
 
 
 ! (6)
 ? "A_SocketTest_fly.gif"
 ! Server has sent a file as stream.
 ! Client reseived with READ.FILE 
 BYTE.OPEN w, fw, "A_SocketTest_fly.gif"
 SOCKET.CLIENT.READ.FILE fw
 BYTE.CLOSE fw

 
 ! (7)
 ? "A_SocketTest_boing.mp3"
 ! Server has sent a file as stream.
 ! Client reseived with READ.FILE 
 BYTE.OPEN w, fw, "A_SocketTest_boing.mp3"
 SOCKET.CLIENT.READ.FILE fw
 BYTE.CLOSE fw
 
 ! (8)
 ? "A_SocketTest_galaxy.png"
 ! Server has sent a file as stream.
 ! Client reseived with READ.BYTE 
 BYTE.OPEN w, fw, "A_SocketTest_galaxy.png"
  DO
   SOCKET.CLIENT.READ.BYTE mByte$
   IF UCODE(mByte$) <> 65535
    BYTE.WRITE.BYTE fw , mByte$
  ENDIF
 UNTIL UCODE(mByte$) = 65535
 BYTE.CLOSE fw
 


 ! (9)
 ? "A_SocketTest_htmldemo1.html"
 ! Server has sent a file as byte array.
 ! Client reseived with READ.LINE 
 BYTE.OPEN w, fw, "A_SocketTest_htmldemo1.html"
 DO
  SOCKET.CLIENT.READ.LINE line$
  IF line$<>"EOF"
   Byte.write.byte fw , line$ + chr$(10)
  ENDIF
 UNTIL line$ = "EOF"
 BYTE.CLOSE fw


 ! (10)
 SOCKET.CLIENT.READ.LINE line$ %"Finished Transmission"
 ? line$

 ? "Finish Client"

 ! Close the client

 SOCKET.CLIENT.CLOSE
 PRINT "Disconnected from server"

 INPUT "Connect again? Y or N", again$, "Y"
 IF again$ = "Y" THEN GOTO clientAgain

 END
