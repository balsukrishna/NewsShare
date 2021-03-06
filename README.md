# For User
 * Works with browsers supporting WebSocket only
 * Submitting already submitted topic doesn't do any thing
 * There is no request to show all topics. Thus if all the top 20 topics are voted, any new topic submitted cannot be shown
   and thus cannot be voted

# For Developer

## Build
- Built using [Gradle](https://gradle.org/)

```
$ git clone https://github.com/balsukrishna/NewsShare.git
$ cd  NewsShare
$ gradle clean builf
```

### Dependencies
 Core Dependencies
   + [Netty](http://netty.io/) a NIO framework is used to write server application
   + [Gson](https://github.com/google/gson) is used to serialize and desirialize JSON Strings to Java POJO  
 Test Dependencies
   + [Mockito](https://github.com/mockito/mockito) for mocking dependencies
  
### Architecture
   - **Client Server Interaction**  
    - Browsing `/` initates a websocket connection to `/voteFeed`  
    - Two types of messages exchanged between server and client    
    
       + UPDATE: Sent from Client to Server
            ```
                {
                     "updateType": "TOPIC", //Update Type. Valid values are //TOPIC,UPVOTE,DOWNVOTE
                      "name": "dog" //Topic Name
                }
              
            ```
       + TOPTOPICS: Send from Server to Client

            ```
                {
                    "cat": [0,0], //topic name : Array of upvotes and downvotes
                    "dog": [0,0],
                }

            ``` 
            ```

             +---------+                    +---------+
             |         |  TOPTOPICS         |         |
             | Browser |<-------------------+ Server  |
             |         +------------------->|         |
             +---------+   UPDATE           +---------+


            ```  
      
    - **Server Architecture**  
      + Netty is a Non blocking event driven Framwork. The key features are
      + EventLoop: EventLoop reads incoming data and trigger events to registered ChannelHandlers  
      + ChanneHandlers: ChannelHandlers should perform business logic and trigger events/write to channel as required  
      + It gurantees one client/channel is handled by one eventloop(one thread) at any point of time  
      + Current List of Handlers is like this  
         

            ```

                    +---------------------------+
                    |                           |
                    |  Http Codec(upon upgrade  |<------+
                    |  changed to WS codec)     |       |
                    +-----------+---------------+       |
                                |                       |
                                |                       |
                                |                       |    +------------------+
                    +-----------v---------------+       |    |                  |
                    |  HttpRequest Handler      |       +----+ Event Loop       |
                    |                           |       |    |                  |
                    |                           |<------+    |                  |
                    +-----------+---------------+       |    +------------------+
                                |                       |
                                |                       |
                    +-----------v---------------+       |
                    |  WSFrame Handler          |       |Triggers events
                    |                           |<------+-----------------+
                    |                           |                         |
                    +---------------------------+                 +-------v---------+
                                                                  |                 |
                    +---------------------------+       +---------+Single Thread    |
                    |  Uodate Committer         |       |         | Executor        |
                    | (Data Structure Update)   |<------+         +-----------------+
                    |                           |
                    +---------------------------+

            ```  

     
       
### Improvements  

#### Front-End
  - Direct initialization of Socket in Script while loading page. More page loading time. 
   We can use some event like document ready etc(Jquery??)
  - Ordering of received top topics is done on client side. This may be done on server side
  - Voting and thus triggered top topics order change is not so visual/observable to Naked-Eye immediately
  - Unit tests for JavaScript 

#### Backed-End   
  - There are lot of fields hard coded, top topics, max topic length,WS request URI, index page loc etc. Create Config
  - Character encoding while serializing,reading index page etc is left to chance. Fix it  
  - Missing Unit Test for HttpRequest Handler
  - Current logic always sends top topics  upon new topic and upvote without really checking whether they really changed or not.
    This can be improved
  - Have to really profile performance of using concurrent data structure/vs context switching overhead to a single thread executor while updating TopicRepo to make sure our decision is optimal

  
#### Disclaimer:
  - No code generation tools used except IDE suggestions. However some code snippets are copied from internet and modified as required,Specifically randomNumberGenerator,randomTextGenerator functions in index.html, websocket handshake initialization logic in HttpRequestHandler etc
 
   
