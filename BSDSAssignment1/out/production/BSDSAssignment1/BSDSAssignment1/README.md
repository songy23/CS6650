BSDS Assignment 1 

Remote interfaces are:
  BSDSPublishInterface
  BSDSSubscribeInterface

A skeleton RMI server supporting two remote interfaces:
  CAServer.java

Two test clients, one for each interface:
  CAPubClient
  CASubClient
  
To test:
  1 Run CAServer
  2 Run either/both clients, they should connect to ther server amd call each remote method successfully.
  
Also there are:
  BSDSContent
  BSDSMessage
  
We'll use these in a later assignment  
